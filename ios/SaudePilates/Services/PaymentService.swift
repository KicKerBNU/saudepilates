import FirebaseFirestore

@MainActor
final class PaymentService: ObservableObject {
    private let db = Firestore.firestore()

    func fetchStudentPayments(companyId: String, studentId: String? = nil) async throws -> [StudentPayment] {
        var query: Query = db.collection("studentPayments").whereField("companyId", isEqualTo: companyId)
        if let studentId {
            query = query.whereField("studentId", isEqualTo: studentId)
        }
        let snapshot = try await query.getDocuments()
        return snapshot.documents.compactMap(mapStudentPayment)
            .sorted { $0.paymentDate > $1.paymentDate }
    }

    func fetchProfessorPayments(companyId: String, professorId: String? = nil) async throws -> [ProfessorPayment] {
        var query: Query = db.collection("professorPayments").whereField("companyId", isEqualTo: companyId)
        if let professorId {
            query = query.whereField("professorId", isEqualTo: professorId)
        }
        let snapshot = try await query.getDocuments()
        let professorPayments = snapshot.documents.compactMap(mapProfessorPayment)
        let studentPayments = try await fetchStudentPayments(companyId: companyId)
        return filterActiveProfessorPayments(professorPayments, studentPayments: studentPayments)
    }

    func fetchProfessorPayouts(companyId: String, month: Int, year: Int) async throws -> [ProfessorPayout] {
        let snapshot = try await db.collection("professorPayouts")
            .whereField("companyId", isEqualTo: companyId)
            .whereField("month", isEqualTo: month)
            .whereField("year", isEqualTo: year)
            .getDocuments()
        return snapshot.documents.map { doc in
            let data = doc.data()
            return ProfessorPayout(
                id: doc.documentID,
                professorId: data["professorId"] as? String ?? "",
                companyId: data["companyId"] as? String,
                month: data["month"] as? Int ?? month,
                year: data["year"] as? Int ?? year,
                totalAmount: data["totalAmount"] as? Double ?? 0,
                paidAt: FirestoreDate.decode(data["paidAt"])
            )
        }
    }

    func addStudentPayment(_ payment: [String: Any]) async throws -> String {
        var payload = payment
        if let date = payment["paymentDate"] as? Date {
            payload["paymentDate"] = FirestoreDate.encode(date)
        }
        payload["createdAt"] = FirestoreDate.encode(Date())
        let ref = try await db.collection("studentPayments").addDocument(data: payload)
        return ref.documentID
    }

    func addProfessorPayment(_ payment: [String: Any]) async throws {
        var payload = payment
        if let date = payment["paymentDate"] as? Date {
            payload["paymentDate"] = FirestoreDate.encode(date)
        }
        payload["createdAt"] = FirestoreDate.encode(Date())
        try await db.collection("professorPayments").addDocument(data: payload)
    }

    func deleteStudentPayment(id: String, companyId: String) async throws {
        let paymentRef = db.collection("studentPayments").document(id)
        let snapshot = try await paymentRef.getDocument()
        guard let data = snapshot.data() else { throw ServiceError.notFound }

        try await deleteLinkedProfessorPayments(
            studentPaymentId: id,
            studentId: data["studentId"] as? String ?? "",
            paymentDate: FirestoreDate.decode(data["paymentDate"]) ?? Date(),
            amount: data["finalAmount"] as? Double ?? data["amount"] as? Double ?? 0,
            createdAt: FirestoreDate.decode(data["createdAt"]),
            companyId: companyId
        )
        try await paymentRef.delete()
    }

    func markProfessorPaid(professorId: String, month: Int, year: Int, totalAmount: Double, companyId: String) async throws {
        try await db.collection("professorPayouts").addDocument(data: [
            "professorId": professorId,
            "month": month,
            "year": year,
            "totalAmount": totalAmount,
            "companyId": companyId,
            "paidAt": FirestoreDate.encode(Date())
        ])
    }

    func unmarkProfessorPaid(payoutId: String) async throws {
        try await db.collection("professorPayouts").document(payoutId).delete()
    }

    private func deleteLinkedProfessorPayments(
        studentPaymentId: String,
        studentId: String,
        paymentDate: Date,
        amount: Double,
        createdAt: Date?,
        companyId: String
    ) async throws {
        let snapshot = try await db.collection("professorPayments")
            .whereField("companyId", isEqualTo: companyId)
            .whereField("studentId", isEqualTo: studentId)
            .getDocuments()

        var candidates = snapshot.documents
        let byId = candidates.filter { $0.data()["studentPaymentId"] as? String == studentPaymentId }
        if !byId.isEmpty {
            candidates = byId
        } else {
            candidates = candidates.filter { doc in
                let data = doc.data()
                guard data["studentPaymentId"] == nil else { return false }
                let profDate = FirestoreDate.decode(data["paymentDate"]) ?? Date.distantPast
                let original = data["originalStudentPayment"] as? Double ?? 0
                return DateHelpers.isSameDay(profDate, paymentDate) && original == amount
            }
        }

        guard let best = candidates.min(by: { lhs, rhs in
            let lhsTime = FirestoreDate.decode(lhs.data()["createdAt"])?.timeIntervalSince1970 ?? 0
            let rhsTime = FirestoreDate.decode(rhs.data()["createdAt"])?.timeIntervalSince1970 ?? 0
            let target = createdAt?.timeIntervalSince1970 ?? 0
            return abs(lhsTime - target) < abs(rhsTime - target)
        }) else { return }

        try await db.collection("professorPayments").document(best.documentID).delete()
    }

    private func filterActiveProfessorPayments(
        _ professorPayments: [ProfessorPayment],
        studentPayments: [StudentPayment]
    ) -> [ProfessorPayment] {
        var available = studentPayments.sorted {
            ($0.createdAt ?? $0.paymentDate) < ($1.createdAt ?? $1.paymentDate)
        }
        var active: [ProfessorPayment] = []

        for payment in professorPayments.sorted(by: { ($0.createdAt ?? $0.paymentDate) < ($1.createdAt ?? $1.paymentDate) }) {
            let matchIndex: Int?
            if let studentPaymentId = payment.studentPaymentId {
                matchIndex = available.firstIndex(where: { $0.id == studentPaymentId })
            } else {
                matchIndex = available.firstIndex(where: { student in
                    student.studentId == payment.studentId &&
                    DateHelpers.isSameDay(student.paymentDate, payment.paymentDate) &&
                    student.paidAmount == payment.originalStudentPayment
                })
            }
            if let matchIndex {
                active.append(payment)
                available.remove(at: matchIndex)
            }
        }
        return active
    }

    private func mapStudentPayment(_ document: DocumentSnapshot) -> StudentPayment? {
        let data = document.data() ?? [:]
        guard let paymentDate = FirestoreDate.decode(data["paymentDate"]) else { return nil }
        return StudentPayment(
            id: document.documentID,
            studentId: data["studentId"] as? String ?? "",
            professorId: data["professorId"] as? String,
            planId: data["planId"] as? String,
            companyId: data["companyId"] as? String,
            amount: data["amount"] as? Double,
            finalAmount: data["finalAmount"] as? Double,
            originalAmount: data["originalAmount"] as? Double,
            discountPercent: data["discountPercent"] as? Double,
            discountAmount: data["discountAmount"] as? Double,
            commissionPercent: data["commissionPercent"] as? Double,
            commissionAmount: data["commissionAmount"] as? Double,
            paymentDate: paymentDate,
            paymentPeriod: data["paymentPeriod"] as? String,
            period: data["period"] as? String,
            periodMonths: data["periodMonths"] as? Int,
            notes: data["notes"] as? String,
            createdAt: FirestoreDate.decode(data["createdAt"])
        )
    }

    private func mapProfessorPayment(_ document: DocumentSnapshot) -> ProfessorPayment? {
        let data = document.data() ?? [:]
        guard let paymentDate = FirestoreDate.decode(data["paymentDate"]) else { return nil }
        return ProfessorPayment(
            id: document.documentID,
            professorId: data["professorId"] as? String ?? "",
            studentId: data["studentId"] as? String ?? "",
            studentPaymentId: data["studentPaymentId"] as? String,
            planId: data["planId"] as? String,
            companyId: data["companyId"] as? String,
            amount: data["amount"] as? Double ?? 0,
            commissionPercent: data["commissionPercent"] as? Double,
            originalStudentPayment: data["originalStudentPayment"] as? Double,
            paymentDate: paymentDate,
            notes: data["notes"] as? String,
            createdAt: FirestoreDate.decode(data["createdAt"]),
            studentName: nil
        )
    }
}

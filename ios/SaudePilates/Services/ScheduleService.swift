import FirebaseFirestore

@MainActor
final class ScheduleService: ObservableObject {
    private let db = Firestore.firestore()

    func fetchProfessorSchedule(professorId: String, startDate: Date, endDate: Date) async throws -> [ScheduledClass] {
        let snapshot = try await db.collection("scheduledClasses")
            .whereField("professorId", isEqualTo: professorId)
            .getDocuments()

        return snapshot.documents.compactMap { doc in
            let data = doc.data()
            guard let date = FirestoreDate.decode(data["date"]) else { return nil }
            guard date >= Calendar.current.startOfDay(for: startDate),
                  date <= Calendar.current.startOfDay(for: endDate) else { return nil }
            return ScheduledClass(
                id: doc.documentID,
                professorId: data["professorId"] as? String ?? "",
                studentId: data["studentId"] as? String,
                companyId: data["companyId"] as? String,
                date: date,
                startTime: data["startTime"] as? String,
                duration: data["duration"] as? Int,
                experimental: data["experimental"] as? Bool,
                experimentalStudentName: data["experimentalStudentName"] as? String,
                studentName: data["studentName"] as? String,
                notes: data["notes"] as? String
            )
        }.sorted { ($0.date, $0.startTime ?? "") < ($1.date, $1.startTime ?? "") }
    }

    func fetchStudentSchedule(studentId: String) async throws -> [ScheduledClass] {
        let snapshot = try await db.collection("scheduledClasses")
            .whereField("studentId", isEqualTo: studentId)
            .getDocuments()
        return snapshot.documents.compactMap { doc in
            let data = doc.data()
            guard let date = FirestoreDate.decode(data["date"]) else { return nil }
            return ScheduledClass(
                id: doc.documentID,
                professorId: data["professorId"] as? String ?? "",
                studentId: data["studentId"] as? String,
                companyId: data["companyId"] as? String,
                date: date,
                startTime: data["startTime"] as? String,
                duration: data["duration"] as? Int,
                experimental: data["experimental"] as? Bool,
                experimentalStudentName: data["experimentalStudentName"] as? String,
                studentName: data["studentName"] as? String,
                notes: data["notes"] as? String
            )
        }.sorted { $0.date < $1.date }
    }

    func saveScheduledClass(_ payload: [String: Any], id: String? = nil) async throws -> String {
        var data = payload
        if let date = payload["date"] as? Date {
            data["date"] = FirestoreDate.encode(date)
        }
        if let id, !id.isEmpty {
            try await db.collection("scheduledClasses").document(id).setData(data, merge: true)
            return id
        }
        let ref = try await db.collection("scheduledClasses").addDocument(data: data)
        return ref.documentID
    }

    func deleteScheduledClass(id: String) async throws {
        try await db.collection("scheduledClasses").document(id).delete()
    }
}

@MainActor
final class AttendanceService: ObservableObject {
    private let db = Firestore.firestore()

    func fetchAttendance(professorId: String, month: Int, year: Int) async throws -> [AttendanceRecord] {
        let snapshot = try await db.collection("attendanceRecords")
            .whereField("professorId", isEqualTo: professorId)
            .getDocuments()
        return snapshot.documents.compactMap { doc in
            let data = doc.data()
            guard let date = FirestoreDate.decode(data["date"]) else { return nil }
            let calendar = Calendar.current
            guard calendar.component(.month, from: date) - 1 == month,
                  calendar.component(.year, from: date) == year else { return nil }
            return AttendanceRecord(
                id: doc.documentID,
                studentId: data["studentId"] as? String ?? "",
                professorId: data["professorId"] as? String ?? "",
                companyId: data["companyId"] as? String,
                date: date,
                present: data["present"] as? Bool ?? true,
                notes: data["notes"] as? String
            )
        }
    }

    func saveAttendance(_ payload: [String: Any], id: String? = nil) async throws {
        var data = payload
        if let date = payload["date"] as? Date {
            data["date"] = FirestoreDate.encode(date)
        }
        if let id, !id.isEmpty {
            try await db.collection("attendanceRecords").document(id).setData(data, merge: true)
        } else {
            try await db.collection("attendanceRecords").addDocument(data: data)
        }
    }
}

@MainActor
final class EvolutionService: ObservableObject {
    private let db = Firestore.firestore()

    func fetchEvolutions(studentId: String) async throws -> [EvolutionRecord] {
        let snapshot = try await db.collection("evolutions")
            .whereField("studentId", isEqualTo: studentId)
            .getDocuments()
        return snapshot.documents.compactMap { doc in
            let data = doc.data()
            return EvolutionRecord(
                id: doc.documentID,
                studentId: data["studentId"] as? String ?? "",
                professorId: data["professorId"] as? String,
                companyId: data["companyId"] as? String,
                date: FirestoreDate.decode(data["date"]) ?? Date(),
                notes: data["notes"] as? String ?? "",
                exercises: data["exercises"] as? String
            )
        }.sorted { $0.date > $1.date }
    }

    func saveEvolution(_ payload: [String: Any], id: String? = nil) async throws {
        var data = payload
        if let date = payload["date"] as? Date {
            data["date"] = FirestoreDate.encode(date)
        }
        if let id, !id.isEmpty {
            try await db.collection("evolutions").document(id).setData(data, merge: true)
        } else {
            try await db.collection("evolutions").addDocument(data: data)
        }
    }

    func deleteEvolution(id: String) async throws {
        try await db.collection("evolutions").document(id).delete()
    }
}

@MainActor
final class AnamnesisService: ObservableObject {
    private let db = Firestore.firestore()

    func fetchAll(companyId: String) async throws -> [AnamnesisRecord] {
        let snapshot = try await db.collection("anamnesis")
            .whereField("companyId", isEqualTo: companyId)
            .getDocuments()
        return snapshot.documents.map(mapAnamnesis)
    }

    func fetchByStudent(studentId: String) async throws -> [AnamnesisRecord] {
        let snapshot = try await db.collection("anamnesis")
            .whereField("studentId", isEqualTo: studentId)
            .getDocuments()
        var records = snapshot.documents.map(mapAnamnesis)
        let legacy = try await db.collection("anamnesis").document(studentId).getDocument()
        if legacy.exists, !records.contains(where: { $0.id == legacy.documentID }) {
            records.append(mapAnamnesis(legacy))
        }
        return records.sorted { ($0.performedAt ?? "") > ($1.performedAt ?? "") }
    }

    func save(studentId: String, companyId: String, data: [String: Any], id: String?) async throws -> String {
        var payload = data
        payload["studentId"] = studentId
        payload["companyId"] = companyId
        payload["updatedAt"] = ISO8601DateFormatter().string(from: Date())
        if payload["performedAt"] == nil {
            payload["performedAt"] = payload["updatedAt"]
        }
        if let id, !id.isEmpty {
            try await db.collection("anamnesis").document(id).setData(payload, merge: true)
            return id
        }
        payload["createdAt"] = payload["updatedAt"]
        let ref = try await db.collection("anamnesis").addDocument(data: payload)
        return ref.documentID
    }

    func delete(id: String) async throws {
        try await db.collection("anamnesis").document(id).delete()
    }

    private func mapAnamnesis(_ document: DocumentSnapshot) -> AnamnesisRecord {
        let data = document.data() ?? [:]
        return AnamnesisRecord(
            id: document.documentID,
            studentId: data["studentId"] as? String ?? document.documentID,
            companyId: data["companyId"] as? String,
            performedAt: data["performedAt"] as? String,
            updatedAt: data["updatedAt"] as? String,
            createdAt: data["createdAt"] as? String,
            studentName: data["studentName"] as? String,
            mainComplaint: data["mainComplaint"] as? String,
            medicalHistory: data["medicalHistory"] as? String,
            surgeries: data["surgeries"] as? String,
            medications: data["medications"] as? String,
            physicalActivity: data["physicalActivity"] as? String,
            goals: data["goals"] as? String,
            observations: data["observations"] as? String
        )
    }
}

@MainActor
final class MessageService: ObservableObject {
    private let db = Firestore.firestore()

    func fetchMessages(professorId: String) async throws -> [StudentMessage] {
        let snapshot = try await db.collection("messages")
            .whereField("professorId", isEqualTo: professorId)
            .getDocuments()
        return snapshot.documents.map { doc in
            let data = doc.data()
            return StudentMessage(
                id: doc.documentID,
                studentId: data["studentId"] as? String ?? "",
                professorId: data["professorId"] as? String,
                companyId: data["companyId"] as? String,
                subject: data["subject"] as? String ?? "",
                message: data["message"] as? String ?? "",
                isRead: data["isRead"] as? Bool,
                createdAt: FirestoreDate.decode(data["createdAt"]),
                studentName: data["studentName"] as? String
            )
        }.sorted { ($0.createdAt ?? .distantPast) > ($1.createdAt ?? .distantPast) }
    }

    func markAsRead(id: String) async throws {
        try await db.collection("messages").document(id).updateData(["isRead": true])
    }
}

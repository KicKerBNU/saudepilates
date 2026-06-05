import FirebaseFirestore

enum UserService {
    static func mapUser(_ document: DocumentSnapshot) -> UserProfile? {
        guard let data = document.data() else { return nil }
        let role = UserRole(rawValue: data["role"] as? String ?? "student") ?? .student
        let name = data["name"] as? String
            ?? "\(data["firstName"] as? String ?? "") \(data["lastName"] as? String ?? "")".trimmingCharacters(in: .whitespaces)
        return UserProfile(
            id: document.documentID,
            email: data["email"] as? String ?? "",
            role: role,
            companyId: data["companyId"] as? String ?? "",
            name: name.isEmpty ? (data["email"] as? String ?? "Usuário") : name,
            phone: data["phone"] as? String,
            planId: data["planId"] as? String,
            professorId: data["professorId"] as? String,
            isActive: data["isActive"] as? Bool,
            deactivatedAt: data["deactivatedAt"] as? String,
            createdAt: data["createdAt"] as? String
        )
    }
}

@MainActor
final class PlanService: ObservableObject {
    private let db = Firestore.firestore()

    func fetchPlans(companyId: String) async throws -> [Plan] {
        let snapshot = try await db.collection("plans")
            .whereField("companyId", isEqualTo: companyId)
            .getDocuments()
        return snapshot.documents.map { doc in
            let data = doc.data()
            return Plan(
                id: doc.documentID,
                title: data["title"] as? String ?? "",
                price: data["price"] as? Double ?? 0,
                companyId: data["companyId"] as? String,
                description: data["description"] as? String,
                createdAt: data["createdAt"] as? String
            )
        }.sorted { $0.title.localizedCaseInsensitiveCompare($1.title) == .orderedAscending }
    }

    func savePlan(_ plan: Plan, companyId: String) async throws -> String {
        let payload: [String: Any] = [
            "title": plan.title,
            "price": plan.price,
            "description": plan.description ?? "",
            "companyId": companyId,
            "createdAt": plan.createdAt ?? ISO8601DateFormatter().string(from: Date())
        ]
        if plan.id.isEmpty {
            let ref = try await db.collection("plans").addDocument(data: payload)
            return ref.documentID
        }
        try await db.collection("plans").document(plan.id).setData(payload, merge: true)
        return plan.id
    }

    func deletePlan(id: String) async throws {
        try await db.collection("plans").document(id).delete()
    }
}

@MainActor
final class SubscriptionService: ObservableObject {
    private let db = Firestore.firestore()

    func fetchSubscription(companyId: String) async throws -> SubscriptionInfo {
        let ref = db.collection("subscriptions").document(companyId)
        let snapshot = try await ref.getDocument()

        if snapshot.exists, let data = snapshot.data() {
            return SubscriptionInfo(
                isSubscribed: data["isSubscribed"] as? Bool ?? false,
                expirationDate: FirestoreDate.decode(data["expirationDate"]),
                plan: data["plan"] as? String
            )
        }

        let trialExpiration = Calendar.current.date(byAdding: .day, value: 30, to: Date()) ?? Date()
        let payload: [String: Any] = [
            "isSubscribed": true,
            "expirationDate": ISO8601DateFormatter().string(from: trialExpiration),
            "plan": "trial",
            "createdAt": ISO8601DateFormatter().string(from: Date()),
            "companyId": companyId
        ]
        try await ref.setData(payload)
        return SubscriptionInfo(isSubscribed: true, expirationDate: trialExpiration, plan: "trial")
    }
}

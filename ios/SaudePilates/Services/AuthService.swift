import Foundation
import FirebaseAuth
import FirebaseFirestore

@MainActor
final class AuthService: ObservableObject {
    static let shared = AuthService()

    @Published var firebaseUser: User?
    @Published var userProfile: UserProfile?
    @Published var company: Company?
    @Published var isLoading = false
    @Published var errorMessage: String?

    private var auth: Auth { Auth.auth() }
    private var db: Firestore { Firestore.firestore() }
    private var authListener: AuthStateDidChangeListenerHandle?

    var isAuthenticated: Bool { firebaseUser != nil }
    var isAdmin: Bool { userProfile?.role == .admin }
    var isProfessor: Bool { userProfile?.role == .professor }
    var isStudent: Bool { userProfile?.role == .student }
    var companyId: String? { userProfile?.companyId }
    var userId: String? { firebaseUser?.uid }

    func start() {
        guard authListener == nil else { return }
        authListener = auth.addStateDidChangeListener { [weak self] _, user in
            Task { @MainActor in
                self?.firebaseUser = user
                if let user {
                    await self?.fetchUserProfile(userId: user.uid)
                } else {
                    self?.userProfile = nil
                    self?.company = nil
                }
            }
        }
    }

    func login(email: String, password: String) async throws {
        isLoading = true
        defer { isLoading = false }
        let result = try await auth.signIn(withEmail: email, password: password)
        await fetchUserProfile(userId: result.user.uid)
    }

    func register(email: String, password: String, companyName: String, adminName: String) async throws {
        isLoading = true
        defer { isLoading = false }

        let result = try await auth.createUser(withEmail: email, password: password)
        let companyRef = try await db.collection("companies").addDocument(data: [
            "name": companyName,
            "language": "pt",
            "createdAt": ISO8601DateFormatter().string(from: Date()),
            "ownerId": result.user.uid
        ])

        let profile = UserProfile(
            id: result.user.uid,
            email: email,
            role: .admin,
            companyId: companyRef.documentID,
            name: adminName,
            phone: nil,
            planId: nil,
            professorId: nil,
            isActive: true,
            deactivatedAt: nil,
            createdAt: ISO8601DateFormatter().string(from: Date())
        )

        try await db.collection("users").document(result.user.uid).setData([
            "email": profile.email,
            "role": profile.role.rawValue,
            "companyId": profile.companyId,
            "name": profile.name,
            "isActive": true,
            "createdAt": profile.createdAt ?? ""
        ])

        await fetchUserProfile(userId: result.user.uid)
    }

    func logout() throws {
        try auth.signOut()
        userProfile = nil
        company = nil
    }

    func fetchUserProfile(userId: String) async {
        do {
            let snapshot = try await db.collection("users").document(userId).getDocument()
            guard let data = snapshot.data() else { return }

            let role = UserRole(rawValue: data["role"] as? String ?? "student") ?? .student
            let name = data["name"] as? String
                ?? data["displayName"] as? String
                ?? "\(data["firstName"] as? String ?? "") \(data["lastName"] as? String ?? "")".trimmingCharacters(in: .whitespaces)
            let resolvedName = name.isEmpty ? (data["email"] as? String ?? "Usuário") : name

            userProfile = UserProfile(
                id: snapshot.documentID,
                email: data["email"] as? String ?? "",
                role: role,
                companyId: data["companyId"] as? String ?? "",
                name: resolvedName,
                phone: data["phone"] as? String,
                planId: data["planId"] as? String,
                professorId: data["professorId"] as? String,
                isActive: data["isActive"] as? Bool,
                deactivatedAt: data["deactivatedAt"] as? String,
                createdAt: data["createdAt"] as? String
            )

            if let companyId = userProfile?.companyId, !companyId.isEmpty {
                await fetchCompany(companyId: companyId)
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func fetchCompany(companyId: String) async {
        do {
            let snapshot = try await db.collection("companies").document(companyId).getDocument()
            guard let data = snapshot.data() else { return }
            company = Company(
                id: snapshot.documentID,
                name: data["name"] as? String ?? "",
                language: data["language"] as? String,
                ownerId: data["ownerId"] as? String,
                createdAt: data["createdAt"] as? String
            )
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func createUserForCompany(email: String, password: String, role: UserRole, userData: [String: Any]) async throws {
        guard isAdmin, let companyId else { throw ServiceError.unauthorized }

        let secondaryApp = try FirebaseAppManager.shared.createSecondaryApp()
        let secondaryAuth = Auth.auth(app: secondaryApp)
        let result = try await secondaryAuth.createUser(withEmail: email, password: password)

        var payload = userData
        payload["email"] = email
        payload["role"] = role.rawValue
        payload["companyId"] = companyId
        payload["isActive"] = true
        payload["createdAt"] = ISO8601DateFormatter().string(from: Date())

        try await db.collection("users").document(result.user.uid).setData(payload)
        try secondaryAuth.signOut()
    }

    func updateUser(userId: String, data: [String: Any]) async throws {
        try await db.collection("users").document(userId).updateData(data)
    }

    func deactivateUser(userId: String) async throws {
        try await db.collection("users").document(userId).updateData([
            "isActive": false,
            "deactivatedAt": ISO8601DateFormatter().string(from: Date())
        ])
    }

    func reactivateUser(userId: String) async throws {
        try await db.collection("users").document(userId).updateData([
            "isActive": true,
            "deactivatedAt": FieldValue.delete()
        ])
    }

    func getUsersByCompany(role: UserRole? = nil) async throws -> [UserProfile] {
        guard let companyId else { return [] }
        var query: Query = db.collection("users").whereField("companyId", isEqualTo: companyId)
        if let role {
            query = query.whereField("role", isEqualTo: role.rawValue)
        }
        let snapshot = try await query.getDocuments()
        return snapshot.documents.compactMap { UserService.mapUser($0) }
    }
}

enum ServiceError: LocalizedError {
    case unauthorized
    case notFound
    case invalidData

    var errorDescription: String? {
        switch self {
        case .unauthorized: return "Ação não autorizada"
        case .notFound: return "Registro não encontrado"
        case .invalidData: return "Dados inválidos"
        }
    }
}

import FirebaseCore

enum FirebaseAppManager {
    static let shared = FirebaseAppManagerImpl()
}

final class FirebaseAppManagerImpl {
    func createSecondaryApp() throws -> FirebaseApp {
        let name = "secondary-\(UUID().uuidString)"
        if let existing = FirebaseApp.app(name: name) {
            return existing
        }
        guard let options = FirebaseApp.app()?.options else {
            throw ServiceError.invalidData
        }
        FirebaseApp.configure(name: name, options: options)
        guard let app = FirebaseApp.app(name: name) else {
            throw ServiceError.invalidData
        }
        return app
    }
}

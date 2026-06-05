import SwiftUI

struct ContentView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var subscription: SubscriptionInfo?
    @State private var checkingSubscription = false

    var body: some View {
        Group {
            if authService.isLoading && authService.userProfile == nil {
                ProgressView("Carregando...")
            } else if !authService.isAuthenticated {
                LoginView()
            } else if let profile = authService.userProfile {
                roleRoot(for: profile)
            } else {
                LoginView()
            }
        }
        .task(id: authService.userProfile?.id) {
            await validateSubscriptionIfNeeded()
        }
    }

    @ViewBuilder
    private func roleRoot(for profile: UserProfile) -> some View {
        switch profile.role {
        case .admin:
            if let subscription, !subscription.isValid {
                SubscriptionGateView(subscription: subscription)
            } else {
                AdminRootView()
            }
        case .professor:
            ProfessorRootView()
        case .student:
            StudentRootView()
        }
    }

    private func validateSubscriptionIfNeeded() async {
        guard authService.isAdmin, let companyId = authService.companyId else { return }
        checkingSubscription = true
        defer { checkingSubscription = false }
        do {
            subscription = try await SubscriptionService().fetchSubscription(companyId: companyId)
        } catch {
            subscription = SubscriptionInfo(isSubscribed: true, expirationDate: Date().addingTimeInterval(86400 * 30), plan: "trial")
        }
    }
}

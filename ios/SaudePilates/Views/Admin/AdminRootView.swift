import SwiftUI

struct AdminRootView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            AdminDashboardView()
                .tabItem { Label("Início", systemImage: "house.fill") }
                .tag(0)

            AdminPeopleHubView()
                .tabItem { Label("Pessoas", systemImage: "person.2.fill") }
                .tag(1)

            AdminPaymentsHubView()
                .tabItem { Label("Pagamentos", systemImage: "creditcard.fill") }
                .tag(2)

            AdminMoreView()
                .tabItem { Label("Mais", systemImage: "ellipsis.circle.fill") }
                .tag(3)
        }
    }
}

struct AdminPeopleHubView: View {
    var body: some View {
        NavigationStack {
            List {
                NavigationLink("Alunos") { StudentsListView() }
                NavigationLink("Professores") { ProfessorsListView() }
                NavigationLink("Planos") { PlansListView() }
            }
            .navigationTitle("Pessoas")
        }
    }
}

struct AdminMoreView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    var body: some View {
        NavigationStack {
            List {
                NavigationLink("Agenda") { AdminScheduleView() }
                NavigationLink("Anamnese") { AnamnesisListView() }
                NavigationLink("Configurações") { CompanySettingsView() }
                NavigationLink("Assinatura") { SubscriptionGateView(subscription: nil, embedded: true) }

                Section {
                    Button("Sair", role: .destructive) {
                        do {
                            try authService.logout()
                        } catch {
                            toastManager.error(error.localizedDescription)
                        }
                    }
                }
            }
            .navigationTitle("Mais")
        }
    }
}

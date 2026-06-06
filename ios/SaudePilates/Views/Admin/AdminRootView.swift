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
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var plans: [Plan] = []
    @State private var professors: [UserProfile] = []
    @State private var students: [UserProfile] = []
    @State private var isLoading = true
    @State private var showingPlanForm = false
    @State private var showingProfessorForm = false
    @State private var showingStudentForm = false

    private let planService = PlanService()

    private enum OnboardingStep {
        case needsPlan
        case needsProfessor
        case needsStudent
    }

    private var onboardingStep: OnboardingStep? {
        if plans.isEmpty { return .needsPlan }
        if professors.filter(\.isUserActive).isEmpty { return .needsProfessor }
        if students.filter(\.isUserActive).isEmpty { return .needsStudent }
        return nil
    }

    @ViewBuilder
    private var onboardingEmptyState: some View {
        switch onboardingStep {
        case .needsPlan:
            EmptyStateView(
                title: "Crie seu primeiro plano",
                message: "Antes de cadastrar professores e alunos, defina um plano com o valor das aulas do seu estúdio.",
                illustrationStyle: .plans,
                actionTitle: "Criar plano",
                action: { showingPlanForm = true }
            )
        case .needsProfessor:
            EmptyStateView(
                title: "Cadastre um professor",
                message: "Com o plano criado, adicione um professor para vincular alunos, agenda e comissões.",
                illustrationStyle: .professorsActive,
                actionTitle: "Cadastrar professor",
                action: { showingProfessorForm = true }
            )
        case .needsStudent:
            EmptyStateView(
                title: "Cadastre seu primeiro aluno",
                message: "Tudo pronto para começar. Adicione um aluno para gerenciar planos, pagamentos e agenda.",
                illustrationStyle: .studentsActive,
                actionTitle: "Cadastrar aluno",
                action: { showingStudentForm = true }
            )
        case nil:
            EmptyView()
        }
    }

    var body: some View {
        NavigationStack {
            List {
                if onboardingStep != nil, !isLoading {
                    onboardingEmptyState
                        .listRowSeparator(.hidden)
                        .listRowBackground(Color.clear)
                } else if !isLoading {
                    NavigationLink("Alunos") { StudentsListView() }
                    NavigationLink("Professores") { ProfessorsListView() }
                    NavigationLink("Planos") { PlansListView() }
                }
            }
            .navigationTitle("Pessoas")
            .sheet(isPresented: $showingPlanForm) {
                PlanFormView(plan: nil) { Task { await load() } }
            }
            .sheet(isPresented: $showingProfessorForm) {
                ProfessorFormView(professor: nil) { Task { await load() } }
            }
            .sheet(isPresented: $showingStudentForm) {
                StudentFormView(student: nil, plans: plans, professors: professors) {
                    Task { await load() }
                }
            }
            .task { await load() }
            .refreshable { await load() }
            .overlay { LoadingOverlay(isLoading: isLoading) }
        }
    }

    private func load() async {
        guard let companyId = authService.companyId else { return }
        isLoading = true
        defer { isLoading = false }
        do {
            async let fetchedPlans = planService.fetchPlans(companyId: companyId)
            async let fetchedProfessors = authService.getUsersByCompany(role: .professor)
            async let fetchedStudents = authService.getUsersByCompany(role: .student)
            plans = try await fetchedPlans
            professors = try await fetchedProfessors
            students = try await fetchedStudents
        } catch {
            toastManager.error(error.localizedDescription)
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

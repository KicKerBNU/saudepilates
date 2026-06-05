import SwiftUI

struct ProfessorsListView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var professors: [UserProfile] = []
    @State private var search = ""
    @State private var showInactive = false
    @State private var isLoading = true
    @State private var showingForm = false
    @State private var editingProfessor: UserProfile?

    private var filtered: [UserProfile] {
        professors
            .filter { showInactive ? $0.isActive == false : $0.isActive != false }
            .filter { search.isEmpty || $0.displayName.localizedCaseInsensitiveContains(search) }
            .sorted { $0.displayName.localizedCaseInsensitiveCompare($1.displayName) == .orderedAscending }
    }

    var body: some View {
        List {
            Picker("Status", selection: $showInactive) {
                Text("Ativos").tag(false)
                Text("Inativos").tag(true)
            }
            .pickerStyle(.segmented)

            ForEach(filtered) { professor in
                VStack(alignment: .leading) {
                    Text(professor.displayName).font(.headline)
                    Text(professor.email).font(.caption).foregroundStyle(.secondary)
                }
                .swipeActions {
                    if professor.isUserActive {
                        Button("Editar") { editingProfessor = professor; showingForm = true }
                        Button("Desativar", role: .destructive) { Task { await deactivate(professor) } }
                    } else {
                        Button("Reativar") { Task { await reactivate(professor) } }
                    }
                }
            }
        }
        .searchable(text: $search)
        .navigationTitle("Professores")
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                Button { editingProfessor = nil; showingForm = true } label: { Image(systemName: "plus") }
            }
        }
        .sheet(isPresented: $showingForm) {
            ProfessorFormView(professor: editingProfessor) { Task { await load() } }
        }
        .task { await load() }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func load() async {
        isLoading = true
        defer { isLoading = false }
        do { professors = try await authService.getUsersByCompany(role: .professor) }
        catch { toastManager.error(error.localizedDescription) }
    }

    private func deactivate(_ professor: UserProfile) async {
        do {
            try await authService.deactivateUser(userId: professor.id)
            toastManager.success("Professor desativado")
            await load()
        } catch { toastManager.error(error.localizedDescription) }
    }

    private func reactivate(_ professor: UserProfile) async {
        do {
            try await authService.reactivateUser(userId: professor.id)
            toastManager.success("Professor reativado")
            await load()
        } catch { toastManager.error(error.localizedDescription) }
    }
}

struct ProfessorFormView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    let professor: UserProfile?
    let onSaved: () -> Void

    @State private var name = ""
    @State private var email = ""
    @State private var phone = ""
    @State private var password = ""

    var body: some View {
        NavigationStack {
            Form {
                TextField("Nome", text: $name)
                TextField("Email", text: $email).textInputAutocapitalization(.never)
                TextField("Telefone", text: $phone)
                if professor == nil { SecureField("Senha", text: $password) }
            }
            .navigationTitle(professor == nil ? "Novo professor" : "Editar professor")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) { Button("Salvar") { Task { await save() } } }
            }
            .onAppear {
                name = professor?.name ?? ""
                email = professor?.email ?? ""
                phone = professor?.phone ?? ""
            }
        }
    }

    private func save() async {
        do {
            if let professor {
                try await authService.updateUser(userId: professor.id, data: ["name": name, "phone": phone])
                toastManager.success("Professor atualizado")
            } else {
                try await authService.createUserForCompany(
                    email: email,
                    password: password,
                    role: .professor,
                    userData: ["name": name, "phone": phone]
                )
                toastManager.success("Professor criado")
            }
            onSaved()
            dismiss()
        } catch { toastManager.error(error.localizedDescription) }
    }
}

struct PlansListView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var plans: [Plan] = []
    @State private var showingForm = false
    @State private var editingPlan: Plan?
    @State private var isLoading = true

    private let planService = PlanService()

    var body: some View {
        List(plans) { plan in
            VStack(alignment: .leading) {
                Text(plan.title).font(.headline)
                Text(CurrencyFormatter.formatWithSymbol(plan.price, language: authService.company?.language))
                    .foregroundStyle(.secondary)
            }
            .swipeActions {
                Button("Editar") { editingPlan = plan; showingForm = true }
            }
        }
        .navigationTitle("Planos")
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                Button { editingPlan = nil; showingForm = true } label: { Image(systemName: "plus") }
            }
        }
        .sheet(isPresented: $showingForm) {
            PlanFormView(plan: editingPlan) { Task { await load() } }
        }
        .task { await load() }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func load() async {
        guard let companyId = authService.companyId else { return }
        isLoading = true
        defer { isLoading = false }
        do { plans = try await planService.fetchPlans(companyId: companyId) }
        catch { toastManager.error(error.localizedDescription) }
    }
}

struct PlanFormView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    let plan: Plan?
    let onSaved: () -> Void

    @State private var title = ""
    @State private var price = ""
    @State private var description = ""

    private let planService = PlanService()

    var body: some View {
        NavigationStack {
            Form {
                TextField("Título", text: $title)
                TextField("Preço", text: $price).keyboardType(.decimalPad)
                TextField("Descrição", text: $description, axis: .vertical)
            }
            .navigationTitle(plan == nil ? "Novo plano" : "Editar plano")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) { Button("Salvar") { Task { await save() } } }
            }
            .onAppear {
                title = plan?.title ?? ""
                price = plan.map { String($0.price) } ?? ""
                description = plan?.description ?? ""
            }
        }
    }

    private func save() async {
        guard let companyId = authService.companyId, let priceValue = Double(price.replacingOccurrences(of: ",", with: ".")) else { return }
        do {
            _ = try await planService.savePlan(
                Plan(id: plan?.id ?? "", title: title, price: priceValue, companyId: companyId, description: description, createdAt: plan?.createdAt),
                companyId: companyId
            )
            toastManager.success("Plano salvo")
            onSaved()
            dismiss()
        } catch { toastManager.error(error.localizedDescription) }
    }
}

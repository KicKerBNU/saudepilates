import SwiftUI

struct StudentsListView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var students: [UserProfile] = []
    @State private var plans: [Plan] = []
    @State private var professors: [UserProfile] = []
    @State private var search = ""
    @State private var showInactive = false
    @State private var isLoading = true
    @State private var selectedStudent: UserProfile?
    @State private var showingForm = false
    @State private var editingStudent: UserProfile?

    private let planService = PlanService()
    private let paymentService = PaymentService()

    private var filtered: [UserProfile] {
        students
            .filter { showInactive ? $0.isActive == false : $0.isActive != false }
            .filter { search.isEmpty || $0.displayName.localizedCaseInsensitiveContains(search) }
            .sorted { $0.displayName.localizedCaseInsensitiveCompare($1.displayName) == .orderedAscending }
    }

    @ViewBuilder
    private var studentsEmptyState: some View {
        if !search.isEmpty {
            EmptyStateView(
                title: "Nenhum resultado",
                message: "Não encontramos alunos para \"\(search)\".",
                illustrationStyle: showInactive ? .studentsInactive : .studentsActive
            )
        } else if showInactive {
            EmptyStateView(
                title: "Nenhum aluno inativo",
                message: "Alunos desativados aparecerão nesta lista. Você pode reativá-los quando necessário.",
                illustrationStyle: .studentsInactive
            )
        } else {
            EmptyStateView(
                title: "Nenhum aluno ativo",
                message: "Cadastre seu primeiro aluno para começar a gerenciar planos, pagamentos e agenda.",
                illustrationStyle: .studentsActive,
                actionTitle: "Cadastrar aluno",
                action: openCreateForm
            )
        }
    }

    private func openCreateForm() {
        editingStudent = nil
        showingForm = true
    }

    var body: some View {
        List {
            Picker("Status", selection: $showInactive) {
                Text("Ativos").tag(false)
                Text("Inativos").tag(true)
            }
            .pickerStyle(.segmented)
            .listRowSeparator(.hidden)

            if filtered.isEmpty && !isLoading {
                studentsEmptyState
                    .listRowSeparator(.hidden)
                    .listRowBackground(Color.clear)
            }

            ForEach(filtered) { student in
                Button {
                    selectedStudent = student
                } label: {
                    VStack(alignment: .leading, spacing: 4) {
                        HStack {
                            Text(student.displayName).font(.headline)
                            if student.isActive == false {
                                Text("Inativo").font(.caption).padding(.horizontal, 8).padding(.vertical, 2)
                                    .background(Color.gray.opacity(0.15)).clipShape(Capsule())
                            }
                        }
                        Text(student.email).font(.caption).foregroundStyle(.secondary)
                        if let planId = student.planId, let plan = plans.first(where: { $0.id == planId }) {
                            Text(plan.title).font(.caption2).foregroundStyle(.secondary)
                        }
                    }
                }
                .swipeActions {
                    if student.isUserActive {
                        Button("Editar") { editingStudent = student; showingForm = true }
                        Button("Desativar", role: .destructive) { Task { await deactivate(student) } }
                    } else {
                        Button("Reativar") { Task { await reactivate(student) } }
                    }
                }
            }
        }
        .searchable(text: $search, prompt: "Buscar aluno")
        .navigationTitle("Alunos")
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                Button(action: openCreateForm) {
                    Image(systemName: "plus")
                }
            }
        }
        .sheet(item: $selectedStudent) { student in
            NavigationStack {
                StudentPaymentHistoryView(student: student)
            }
        }
        .sheet(isPresented: $showingForm) {
            StudentFormView(student: editingStudent, plans: plans, professors: professors) {
                Task { await load() }
            }
        }
        .task { await load() }
        .refreshable { await load() }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func load() async {
        isLoading = true
        defer { isLoading = false }
        guard let companyId = authService.companyId else { return }
        do {
            async let users = authService.getUsersByCompany(role: .student)
            async let fetchedPlans = planService.fetchPlans(companyId: companyId)
            async let fetchedProfessors = authService.getUsersByCompany(role: .professor)
            students = try await users
            plans = try await fetchedPlans
            professors = try await fetchedProfessors
        } catch {
            toastManager.error(error.localizedDescription)
        }
    }

    private func deactivate(_ student: UserProfile) async {
        do {
            try await authService.deactivateUser(userId: student.id)
            toastManager.success("Aluno desativado")
            await load()
        } catch { toastManager.error(error.localizedDescription) }
    }

    private func reactivate(_ student: UserProfile) async {
        do {
            try await authService.reactivateUser(userId: student.id)
            toastManager.success("Aluno reativado")
            await load()
        } catch { toastManager.error(error.localizedDescription) }
    }
}

struct StudentFormView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    let student: UserProfile?
    let plans: [Plan]
    let professors: [UserProfile]
    let onSaved: () -> Void

    @State private var name = ""
    @State private var email = ""
    @State private var phone = ""
    @State private var password = ""
    @State private var selectedPlanId = ""
    @State private var selectedProfessorId = ""
    @State private var isSaving = false

    var body: some View {
        NavigationStack {
            Form {
                Section("Dados") {
                    TextField("Nome", text: $name)
                    TextField("Email", text: $email).textInputAutocapitalization(.never)
                    TextField("Telefone", text: $phone)
                    if student == nil {
                        SecureField("Senha", text: $password)
                    }
                }
                Section("Plano") {
                    Picker("Plano", selection: $selectedPlanId) {
                        Text("Selecione").tag("")
                        ForEach(plans) { plan in
                            Text("\(plan.title) - \(CurrencyFormatter.formatWithSymbol(plan.price, language: authService.company?.language))").tag(plan.id)
                        }
                    }
                }
                Section("Professor") {
                    Picker("Professor", selection: $selectedProfessorId) {
                        Text("Selecione").tag("")
                        Text("Rodízio").tag(RotationValue.rotation)
                        ForEach(professors.filter(\.isUserActive)) { professor in
                            Text(professor.displayName).tag(professor.id)
                        }
                    }
                }
            }
            .navigationTitle(student == nil ? "Novo aluno" : "Editar aluno")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Salvar") { Task { await save() } }.disabled(isSaving)
                }
            }
            .onAppear {
                name = student?.name ?? ""
                email = student?.email ?? ""
                phone = student?.phone ?? ""
                selectedPlanId = student?.planId ?? ""
                selectedProfessorId = student?.professorId ?? ""
            }
        }
    }

    private func save() async {
        isSaving = true
        defer { isSaving = false }
        do {
            if let student {
                try await authService.updateUser(userId: student.id, data: [
                    "name": name,
                    "phone": phone,
                    "planId": selectedPlanId,
                    "professorId": selectedProfessorId
                ])
                toastManager.success("Aluno atualizado")
            } else {
                try await authService.createUserForCompany(
                    email: email,
                    password: password,
                    role: .student,
                    userData: [
                        "name": name,
                        "phone": phone,
                        "planId": selectedPlanId,
                        "professorId": selectedProfessorId
                    ]
                )
                toastManager.success("Aluno criado")
            }
            onSaved()
            dismiss()
        } catch {
            toastManager.error(error.localizedDescription)
        }
    }
}

struct StudentPaymentHistoryView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    let student: UserProfile
    @State private var payments: [StudentPayment] = []
    @State private var paymentToDelete: StudentPayment?
    @State private var isLoading = true

    private let paymentService = PaymentService()

    var body: some View {
        List {
            Section(student.displayName) {
                Text(student.email).foregroundStyle(.secondary)
            }
            if payments.isEmpty {
                Text("Nenhum pagamento encontrado").foregroundStyle(.secondary)
            } else {
                ForEach(payments) { payment in
                    HStack {
                        VStack(alignment: .leading) {
                            Text(CurrencyFormatter.formatWithSymbol(payment.paidAmount, language: authService.company?.language))
                                .font(.headline)
                            Text(DateHelpers.shortDate(payment.paymentDate))
                                .font(.caption)
                                .foregroundStyle(.secondary)
                        }
                        Spacer()
                        Button(role: .destructive) {
                            paymentToDelete = payment
                        } label: {
                            Image(systemName: "trash")
                        }
                    }
                }
            }
        }
        .navigationTitle("Histórico")
        .toolbar {
            ToolbarItem(placement: .cancellationAction) {
                Button("Fechar") { dismiss() }
            }
        }
        .task { await load() }
        .confirmDialog(
            isPresented: Binding(
                get: { paymentToDelete != nil },
                set: { if !$0 { paymentToDelete = nil } }
            ),
            title: "Remover pagamento",
            message: "Tem certeza que deseja remover este pagamento?",
            confirmTitle: "Remover",
            isDestructive: true
        ) {
            guard let paymentToDelete, let companyId = authService.companyId else { return }
            Task {
                do {
                    try await paymentService.deleteStudentPayment(id: paymentToDelete.id, companyId: companyId)
                    toastManager.success("Pagamento removido")
                    await load()
                } catch {
                    toastManager.error(error.localizedDescription)
                }
                self.paymentToDelete = nil
            }
        }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func load() async {
        guard let companyId = authService.companyId else { return }
        isLoading = true
        defer { isLoading = false }
        do {
            payments = try await paymentService.fetchStudentPayments(companyId: companyId, studentId: student.id)
        } catch {
            toastManager.error(error.localizedDescription)
        }
    }
}

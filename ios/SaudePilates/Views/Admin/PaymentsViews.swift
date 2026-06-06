import SwiftUI
import Charts

struct AdminPaymentsHubView: View {
    var body: some View {
        NavigationStack {
            List {
                NavigationLink("Registrar pagamento") { PaymentRegistrationView() }
                NavigationLink("Pagamentos do mês") { MonthlyPaymentsView() }
                NavigationLink("Visualização") { PaymentVisualizationView() }
                NavigationLink("Pagamentos de professores") { ProfessorPaymentsView() }
            }
            .navigationTitle("Pagamentos")
        }
    }
}

struct PaymentRegistrationView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var students: [UserProfile] = []
    @State private var plans: [Plan] = []
    @State private var professors: [UserProfile] = []
    @State private var payments: [StudentPayment] = []
    @State private var selectedStudentId = ""
    @State private var paymentDate = Date()
    @State private var periodMonths = 1
    @State private var isSaving = false

    private let planService = PlanService()
    private let paymentService = PaymentService()

    private var selectedStudent: UserProfile? { students.first { $0.id == selectedStudentId } }
    private var selectedPlan: Plan? {
        guard let planId = selectedStudent?.planId else { return nil }
        return plans.first { $0.id == planId }
    }

    private var availableStudents: [UserProfile] {
        let paidIds = Set(paymentsForCurrentMonth.map(\.studentId))
        return students.filter { $0.isUserActive && !paidIds.contains($0.id) }
            .sorted { $0.displayName.localizedCaseInsensitiveCompare($1.displayName) == .orderedAscending }
    }

    private var paymentsForCurrentMonth: [StudentPayment] {
        let month = Calendar.current.component(.month, from: Date()) - 1
        let year = Calendar.current.component(.year, from: Date())
        return payments.filter {
            Calendar.current.component(.month, from: $0.paymentDate) - 1 == month &&
            Calendar.current.component(.year, from: $0.paymentDate) == year
        }
    }

    private var originalAmount: Double { (selectedPlan?.price ?? 0) * Double(periodMonths) }
    private var discountPercent: Double { periodMonths >= 12 ? 10 : periodMonths >= 6 ? 5 : periodMonths >= 3 ? 3 : 0 }
    private var discountAmount: Double { originalAmount * discountPercent / 100 }
    private var finalAmount: Double { originalAmount - discountAmount }
    private var commissionPercent: Double { 40 }
    private var commissionAmount: Double { finalAmount * commissionPercent / 100 }

    var body: some View {
        List {
            Section {
                Picker("Aluno", selection: $selectedStudentId) {
                    Text("Selecione").tag("")
                    ForEach(availableStudents) { student in
                        Text(student.displayName).tag(student.id)
                    }
                }
            }

            if selectedStudentId.isEmpty {
                EmptyStateView(
                    title: "Selecione um aluno",
                    message: "Escolha um aluno acima para registrar o pagamento do plano.",
                    illustrationStyle: .paymentSelectStudent
                )
                .listRowSeparator(.hidden)
                .listRowBackground(Color.clear)
            } else if let student = selectedStudent, let plan = selectedPlan {
                Section("Detalhes") {
                    LabeledContent("Plano", value: plan.title)
                    LabeledContent("Professor", value: professorName(for: student))
                }

                Section("Pagamento") {
                    DatePicker("Data", selection: $paymentDate, displayedComponents: .date)
                    Picker("Período", selection: $periodMonths) {
                        Text("Mensal (1 mês)").tag(1)
                        Text("Trimestral (3 meses)").tag(3)
                        Text("Semestral (6 meses)").tag(6)
                        Text("Anual (12 meses)").tag(12)
                    }
                    LabeledContent("Valor original", value: CurrencyFormatter.formatWithSymbol(originalAmount, language: authService.company?.language))
                    if discountPercent > 0 {
                        LabeledContent("Desconto", value: "\(Int(discountPercent))%")
                    }
                    LabeledContent("Valor final", value: CurrencyFormatter.formatWithSymbol(finalAmount, language: authService.company?.language))
                    LabeledContent("Comissão professor", value: CurrencyFormatter.formatWithSymbol(commissionAmount, language: authService.company?.language))
                }

                Section {
                    Button("Registrar pagamento") { Task { await register() } }
                        .disabled(isSaving || student.professorId == nil)
                }
            }
        }
        .navigationTitle("Registrar pagamento")
        .task { await load() }
        .overlay { LoadingOverlay(isLoading: isSaving) }
    }

    private func professorName(for student: UserProfile) -> String {
        if student.professorId == RotationValue.rotation { return "Rodízio" }
        return professors.first { $0.id == student.professorId }?.displayName ?? "Não atribuído"
    }

    private func load() async {
        guard let companyId = authService.companyId else { return }
        do {
            async let users = authService.getUsersByCompany(role: .student)
            async let fetchedPlans = planService.fetchPlans(companyId: companyId)
            async let fetchedProfessors = authService.getUsersByCompany(role: .professor)
            async let fetchedPayments = paymentService.fetchStudentPayments(companyId: companyId)
            students = try await users
            plans = try await fetchedPlans
            professors = try await fetchedProfessors
            payments = try await fetchedPayments
        } catch { toastManager.error(error.localizedDescription) }
    }

    private func register() async {
        guard let companyId = authService.companyId,
              let student = selectedStudent,
              let plan = selectedPlan,
              let professorId = student.professorId else { return }
        isSaving = true
        defer { isSaving = false }
        do {
            let studentPaymentId = try await paymentService.addStudentPayment([
                "studentId": student.id,
                "professorId": professorId,
                "planId": plan.id,
                "companyId": companyId,
                "amount": finalAmount,
                "finalAmount": finalAmount,
                "originalAmount": originalAmount,
                "discountPercent": discountPercent,
                "discountAmount": discountAmount,
                "commissionPercent": commissionPercent,
                "commissionAmount": commissionAmount,
                "paymentDate": paymentDate,
                "periodMonths": periodMonths,
                "paymentPeriod": monthRangeText(),
                "createdBy": authService.userId ?? ""
            ])

            if professorId != RotationValue.rotation, commissionAmount > 0 {
                try await paymentService.addProfessorPayment([
                    "professorId": professorId,
                    "studentId": student.id,
                    "studentPaymentId": studentPaymentId,
                    "planId": plan.id,
                    "companyId": companyId,
                    "amount": commissionAmount,
                    "commissionPercent": commissionPercent,
                    "originalStudentPayment": finalAmount,
                    "paymentDate": paymentDate,
                    "notes": "Comissão referente ao pagamento de \(periodMonths) mês(es) do aluno \(student.displayName)"
                ])
            }

            toastManager.success("Pagamento registrado com sucesso")
            selectedStudentId = ""
            await load()
        } catch {
            toastManager.error(error.localizedDescription)
        }
    }

    private func monthRangeText() -> String {
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "pt_BR")
        formatter.dateFormat = "MMMM/yyyy"
        return formatter.string(from: paymentDate)
    }
}

struct MonthlyPaymentsView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var selectedMonth = Calendar.current.component(.month, from: Date()) - 1
    @State private var selectedYear = Calendar.current.component(.year, from: Date())
    @State private var payments: [StudentPayment] = []
    @State private var students: [UserProfile] = []
    @State private var paymentToDelete: StudentPayment?
    @State private var isLoading = true

    private let paymentService = PaymentService()

    private var monthPayments: [StudentPayment] {
        payments.filter {
            Calendar.current.component(.month, from: $0.paymentDate) - 1 == selectedMonth &&
            Calendar.current.component(.year, from: $0.paymentDate) == selectedYear
        }.sorted { studentName(for: $0).localizedCaseInsensitiveCompare(studentName(for: $1)) == .orderedAscending }
    }

    var body: some View {
        List {
            Section {
                Picker("Mês", selection: $selectedMonth) {
                    ForEach(0..<12, id: \.self) { index in
                        Text(DateHelpers.monthNames[index]).tag(index)
                    }
                }
                Stepper("Ano: \(selectedYear)", value: $selectedYear, in: 2020...2100)
            }

            Section("Resumo") {
                LabeledContent("Total recebido", value: CurrencyFormatter.formatWithSymbol(monthPayments.reduce(0) { $0 + $1.paidAmount }, language: authService.company?.language))
                LabeledContent("Pagamentos", value: "\(monthPayments.count)")
            }

            Section("Lista") {
                ForEach(monthPayments) { payment in
                    HStack {
                        VStack(alignment: .leading) {
                            Text(studentName(for: payment)).font(.headline)
                            Text(DateHelpers.shortDate(payment.paymentDate)).font(.caption).foregroundStyle(.secondary)
                        }
                        Spacer()
                        Text(CurrencyFormatter.formatWithSymbol(payment.paidAmount, language: authService.company?.language))
                        Button(role: .destructive) { paymentToDelete = payment } label: { Image(systemName: "trash") }
                    }
                }
            }
        }
        .navigationTitle("Pagamentos do mês")
        .task(id: selectedMonth) { await load() }
        .onChange(of: selectedYear) { _ in Task { await load() } }
        .confirmDialog(
            isPresented: Binding(get: { paymentToDelete != nil }, set: { if !$0 { paymentToDelete = nil } }),
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
                } catch { toastManager.error(error.localizedDescription) }
                self.paymentToDelete = nil
            }
        }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func studentName(for payment: StudentPayment) -> String {
        students.first { $0.id == payment.studentId }?.displayName ?? payment.studentId
    }

    private func load() async {
        guard let companyId = authService.companyId else { return }
        isLoading = true
        defer { isLoading = false }
        do {
            async let fetchedPayments = paymentService.fetchStudentPayments(companyId: companyId)
            async let fetchedStudents = authService.getUsersByCompany(role: .student)
            payments = try await fetchedPayments
            students = try await fetchedStudents
        } catch { toastManager.error(error.localizedDescription) }
    }
}

struct PaymentVisualizationView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var payments: [StudentPayment] = []
    @State private var isLoading = true
    @State private var showRegistration = false
    private let paymentService = PaymentService()

    private struct MonthlyChartItem: Identifiable {
        let id: String
        let month: String
        let total: Double
    }

    private var chartData: [MonthlyChartItem] {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM/yy"
        formatter.locale = Locale(identifier: "pt_BR")
        let calendar = Calendar.current

        var totals: [String: (label: String, total: Double, sortKey: Int)] = [:]
        for payment in payments {
            let date = payment.paymentDate
            let year = calendar.component(.year, from: date)
            let month = calendar.component(.month, from: date)
            let key = String(format: "%04d-%02d", year, month)
            let sortKey = year * 100 + month
            let label = formatter.string(from: date)

            if var existing = totals[key] {
                existing.total += payment.paidAmount
                totals[key] = existing
            } else {
                totals[key] = (label, payment.paidAmount, sortKey)
            }
        }

        return totals
            .sorted { $0.value.sortKey < $1.value.sortKey }
            .map { MonthlyChartItem(id: $0.key, month: $0.value.label, total: $0.value.total) }
    }

    var body: some View {
        Group {
            if !isLoading && payments.isEmpty {
                EmptyStateView(
                    title: "Nenhum pagamento registrado",
                    message: "Registre pagamentos de alunos para visualizar aqui a evolução do valor recebido mês a mês no seu estúdio.",
                    illustrationStyle: .paymentVisualization,
                    actionTitle: "Registrar pagamento",
                    action: { showRegistration = true }
                )
            } else {
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        if #available(iOS 16.0, *) {
                            Chart(chartData) { item in
                                BarMark(x: .value("Mês", item.month), y: .value("Total", item.total))
                            }
                            .frame(height: 240)
                            .padding()
                            .background(Color(.secondarySystemBackground))
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                        }
                        Text("Total geral: \(CurrencyFormatter.formatWithSymbol(payments.reduce(0) { $0 + $1.paidAmount }, language: authService.company?.language))")
                            .font(.headline)
                    }
                    .padding()
                }
            }
        }
        .navigationTitle("Visualização")
        .navigationDestination(isPresented: $showRegistration) {
            PaymentRegistrationView()
        }
        .task { await load() }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func load() async {
        guard let companyId = authService.companyId else { return }
        isLoading = true
        defer { isLoading = false }
        do { payments = try await paymentService.fetchStudentPayments(companyId: companyId) }
        catch {}
    }
}

struct ProfessorPaymentsView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var professors: [UserProfile] = []
    @State private var students: [UserProfile] = []
    @State private var payments: [ProfessorPayment] = []
    @State private var payouts: [ProfessorPayout] = []
    @State private var selectedProfessorId = ""
    @State private var selectedMonth = Calendar.current.component(.month, from: Date()) - 1
    @State private var selectedYear = Calendar.current.component(.year, from: Date())
    @State private var isLoading = true

    private let paymentService = PaymentService()

    private var monthPayments: [ProfessorPayment] {
        payments.filter {
            $0.professorId == selectedProfessorId &&
            Calendar.current.component(.month, from: $0.paymentDate) - 1 == selectedMonth &&
            Calendar.current.component(.year, from: $0.paymentDate) == selectedYear
        }.sorted { ($0.studentName ?? "").localizedCaseInsensitiveCompare($1.studentName ?? "") == .orderedAscending }
    }

    private var totalCommission: Double { monthPayments.reduce(0) { $0 + $1.amount } }
    private var currentPayout: ProfessorPayout? {
        payouts.first { $0.professorId == selectedProfessorId }
    }

    var body: some View {
        Form {
            Section("Professor") {
                Picker("Professor", selection: $selectedProfessorId) {
                    Text("Selecione").tag("")
                    ForEach(professors.filter(\.isUserActive)) { professor in
                        Text(professor.displayName).tag(professor.id)
                    }
                }
                Picker("Mês", selection: $selectedMonth) {
                    ForEach(0..<12, id: \.self) { index in
                        Text(DateHelpers.monthNames[index]).tag(index)
                    }
                }
            }

            if !selectedProfessorId.isEmpty {
                Section("Resumo") {
                    LabeledContent("Comissões", value: "\(monthPayments.count)")
                    LabeledContent("Total", value: CurrencyFormatter.formatWithSymbol(totalCommission, language: authService.company?.language))
                    LabeledContent("Status", value: currentPayout == nil ? "Pendente" : "Pago")
                }

                Section("Detalhes") {
                    ForEach(monthPayments) { payment in
                        VStack(alignment: .leading, spacing: 4) {
                            Text(payment.studentName ?? studentName(for: payment.studentId)).font(.headline)
                            Text("Pagamento aluno: \(CurrencyFormatter.formatWithSymbol(payment.originalStudentPayment ?? 0, language: authService.company?.language))")
                                .font(.caption)
                            Text("Comissão: \(CurrencyFormatter.formatWithSymbol(payment.amount, language: authService.company?.language))")
                                .font(.subheadline.weight(.semibold))
                                .foregroundStyle(.purple)
                            Text(DateHelpers.shortDate(payment.paymentDate)).font(.caption2).foregroundStyle(.secondary)
                        }
                    }
                }

                Section {
                    if currentPayout == nil {
                        Button("Marcar como pago") { Task { await markPaid() } }
                    } else if let payout = currentPayout {
                        Button("Desfazer pagamento") { Task { await unmarkPaid(payout) } }
                    }
                }
            }
        }
        .navigationTitle("Pagamentos professores")
        .task { await load() }
        .onChange(of: selectedMonth) { _ in Task { await loadPayouts() } }
        .onChange(of: selectedYear) { _ in Task { await loadPayouts() } }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func studentName(for id: String) -> String {
        students.first { $0.id == id }?.displayName ?? id
    }

    private func load() async {
        guard let companyId = authService.companyId else { return }
        isLoading = true
        defer { isLoading = false }
        do {
            async let fetchedProfessors = authService.getUsersByCompany(role: .professor)
            async let fetchedStudents = authService.getUsersByCompany(role: .student)
            async let fetchedPayments = paymentService.fetchProfessorPayments(companyId: companyId)
            professors = try await fetchedProfessors
            students = try await fetchedStudents
            payments = try await fetchedPayments.map { payment in
                var copy = payment
                copy.studentName = studentName(for: payment.studentId)
                return copy
            }
            if selectedProfessorId.isEmpty {
                selectedProfessorId = professors.first?.id ?? ""
            }
            await loadPayouts()
        } catch { toastManager.error(error.localizedDescription) }
    }

    private func loadPayouts() async {
        guard let companyId = authService.companyId else { return }
        do { payouts = try await paymentService.fetchProfessorPayouts(companyId: companyId, month: selectedMonth, year: selectedYear) }
        catch {}
    }

    private func markPaid() async {
        guard let companyId = authService.companyId, !selectedProfessorId.isEmpty else { return }
        do {
            try await paymentService.markProfessorPaid(
                professorId: selectedProfessorId,
                month: selectedMonth,
                year: selectedYear,
                totalAmount: totalCommission,
                companyId: companyId
            )
            toastManager.success("Professor marcado como pago")
            await loadPayouts()
        } catch { toastManager.error(error.localizedDescription) }
    }

    private func unmarkPaid(_ payout: ProfessorPayout) async {
        do {
            try await paymentService.unmarkProfessorPaid(payoutId: payout.id)
            toastManager.success("Pagamento desfeito")
            await loadPayouts()
        } catch { toastManager.error(error.localizedDescription) }
    }
}

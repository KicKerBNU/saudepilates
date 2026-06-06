import SwiftUI

struct AdminDashboardView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var stats = DashboardStats()
    @State private var isLoading = true

    private let paymentService = PaymentService()
    private let planService = PlanService()

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 16) {
                    Text(authService.company?.name ?? "Painel Admin")
                        .font(.title2.bold())
                    Text(AppVersion.display)
                        .font(.footnote)
                        .foregroundStyle(.secondary)

                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        StatCard(title: "Alunos ativos", value: "\(stats.totalStudents)", color: .indigo)
                        StatCard(title: "Professores", value: "\(stats.totalProfessors)", color: .green)
                        StatCard(title: "Receita do mês", value: CurrencyFormatter.formatWithSymbol(stats.monthlyRevenue, language: authService.company?.language), color: .blue)
                        StatCard(title: "Planos", value: "\(stats.totalPlans)", color: .orange)
                    }

                    VStack(spacing: 10) {
                        NavigationLink { PaymentRegistrationView() } label: {
                            QuickActionRow(title: "Registrar pagamento", icon: "plus.circle.fill")
                        }
                        NavigationLink { MonthlyPaymentsView() } label: {
                            QuickActionRow(title: "Pagamentos do mês", icon: "calendar")
                        }
                        NavigationLink { ProfessorPaymentsView() } label: {
                            QuickActionRow(title: "Pagamentos de professores", icon: "person.crop.circle.badge.checkmark")
                        }
                        NavigationLink { AdminScheduleView() } label: {
                            QuickActionRow(title: "Agenda", icon: "calendar.badge.clock")
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Dashboard")
            .task { await loadStats() }
            .refreshable { await loadStats() }
            .overlay { LoadingOverlay(isLoading: isLoading) }
        }
    }

    private func loadStats() async {
        guard let companyId = authService.companyId else { return }
        isLoading = true
        defer { isLoading = false }
        do {
            async let students = authService.getUsersByCompany(role: .student)
            async let professors = authService.getUsersByCompany(role: .professor)
            async let plans = planService.fetchPlans(companyId: companyId)
            async let payments = paymentService.fetchStudentPayments(companyId: companyId)

            let allStudents = try await students
            let allProfessors = try await professors
            let allPlans = try await plans
            let allPayments = try await payments

            let now = Date()
            let month = Calendar.current.component(.month, from: now) - 1
            let year = Calendar.current.component(.year, from: now)

            let monthly = allPayments.filter {
                Calendar.current.component(.month, from: $0.paymentDate) - 1 == month &&
                Calendar.current.component(.year, from: $0.paymentDate) == year
            }.reduce(0) { $0 + $1.paidAmount }

            stats = DashboardStats(
                totalStudents: allStudents.filter(\.isUserActive).count,
                totalProfessors: allProfessors.filter(\.isUserActive).count,
                monthlyRevenue: monthly,
                totalPlans: allPlans.count
            )
        } catch {}
    }
}

struct StatCard: View {
    let title: String
    let value: String
    let color: Color

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(title).font(.caption).foregroundStyle(.secondary)
            Text(value).font(.title3.bold())
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding()
        .background(color.opacity(0.1))
        .clipShape(RoundedRectangle(cornerRadius: 14))
    }
}

struct QuickActionRow: View {
    let title: String
    let icon: String

    var body: some View {
        HStack {
            Image(systemName: icon)
            Text(title)
            Spacer()
            Image(systemName: "chevron.right")
                .foregroundStyle(.secondary)
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

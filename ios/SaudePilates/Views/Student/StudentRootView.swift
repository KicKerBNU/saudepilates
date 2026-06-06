import SwiftUI

struct StudentRootView: View {
    @EnvironmentObject private var authService: AuthService

    var body: some View {
        TabView {
            StudentDashboardView()
                .tabItem { Label("Início", systemImage: "house.fill") }

            StudentPaymentsView()
                .tabItem { Label("Pagamentos", systemImage: "creditcard.fill") }

            StudentScheduleView()
                .tabItem { Label("Agenda", systemImage: "calendar") }
        }
    }
}

struct StudentDashboardView: View {
    @EnvironmentObject private var authService: AuthService

    var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: 16) {
                Text("Bem-vindo, \(authService.userProfile?.displayName ?? "Aluno")")
                    .font(.title2.bold())
                Text("Acompanhe suas aulas e pagamentos pelo app.")
                    .foregroundStyle(.secondary)
                Spacer()
            }
            .padding()
            .navigationTitle("Início")
        }
    }
}

struct StudentPaymentsView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var payments: [StudentPayment] = []

    private let paymentService = PaymentService()

    var body: some View {
        NavigationStack {
            List(payments) { payment in
                VStack(alignment: .leading) {
                    Text(CurrencyFormatter.formatWithSymbol(payment.paidAmount, language: authService.company?.language))
                        .font(.headline)
                    Text(DateHelpers.shortDate(payment.paymentDate))
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    if let period = payment.paymentPeriod {
                        Text(period).font(.caption2).foregroundStyle(.secondary)
                    }
                }
            }
            .navigationTitle("Meus pagamentos")
            .overlay {
                if payments.isEmpty {
                    EmptyStateView(title: "Nenhum pagamento", systemImage: "creditcard")
                }
            }
            .task { await load() }
        }
    }

    private func load() async {
        guard let companyId = authService.companyId, let studentId = authService.userId else { return }
        do { payments = try await paymentService.fetchStudentPayments(companyId: companyId, studentId: studentId) }
        catch {}
    }
}

struct StudentScheduleView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var classes: [ScheduledClass] = []

    private let scheduleService = ScheduleService()

    var body: some View {
        NavigationStack {
            List(classes) { item in
                VStack(alignment: .leading) {
                    Text(DateHelpers.shortDate(item.date)).font(.headline)
                    Text(item.startTime ?? "--:--").foregroundStyle(.secondary)
                }
            }
            .navigationTitle("Minha agenda")
            .overlay {
                if classes.isEmpty {
                    EmptyStateView(
                        title: "Sem aulas agendadas",
                        message: "Quando o estúdio agendar suas aulas, elas aparecerão aqui.",
                        illustrationStyle: .scheduleNoClasses
                    )
                }
            }
            .task { await load() }
        }
    }

    private func load() async {
        guard let studentId = authService.userId else { return }
        do {
            classes = try await scheduleService.fetchStudentSchedule(studentId: studentId)
                .filter { $0.date >= Calendar.current.startOfDay(for: Date()) }
        } catch {}
    }
}

import SwiftUI

struct ProfessorRootView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    var body: some View {
        TabView {
            ProfessorDashboardView()
                .tabItem { Label("Início", systemImage: "house.fill") }

            ProfessorStudentsView()
                .tabItem { Label("Alunos", systemImage: "person.3.fill") }

            ProfessorScheduleView()
                .tabItem { Label("Agenda", systemImage: "calendar") }

            ProfessorMoreView()
                .tabItem { Label("Mais", systemImage: "ellipsis.circle.fill") }
        }
    }
}

struct ProfessorDashboardView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var studentsCount = 0
    @State private var monthEarnings: Double = 0
    @State private var upcomingClasses = 0

    private let paymentService = PaymentService()
    private let scheduleService = ScheduleService()

    var body: some View {
        NavigationStack {
            List {
                Section(authService.userProfile?.displayName ?? "Professor") {
                    LabeledContent("Alunos", value: "\(studentsCount)")
                    LabeledContent("Ganhos do mês", value: CurrencyFormatter.formatWithSymbol(monthEarnings, language: authService.company?.language))
                    LabeledContent("Próximas aulas", value: "\(upcomingClasses)")
                }
                Section("Ações rápidas") {
                    NavigationLink("Controle de presença") { ProfessorAttendanceView() }
                    NavigationLink("Evolução dos alunos") { ProfessorEvolutionView() }
                    NavigationLink("Histórico de ganhos") { ProfessorEarningsView() }
                    NavigationLink("Mensagens") { ProfessorMessagesView() }
                    NavigationLink("Anamnese") { AnamnesisListView() }
                }
            }
            .navigationTitle("Dashboard")
            .task { await load() }
        }
    }

    private func load() async {
        guard let companyId = authService.companyId, let professorId = authService.userId else { return }
        do {
            let students = try await authService.getUsersByCompany(role: .student)
            studentsCount = students.filter { $0.professorId == professorId || $0.professorId == RotationValue.rotation }.count

            let month = Calendar.current.component(.month, from: Date()) - 1
            let year = Calendar.current.component(.year, from: Date())
            let payments = try await paymentService.fetchProfessorPayments(companyId: companyId, professorId: professorId)
            monthEarnings = payments.filter {
                Calendar.current.component(.month, from: $0.paymentDate) - 1 == month &&
                Calendar.current.component(.year, from: $0.paymentDate) == year
            }.reduce(0) { $0 + $1.amount }

            let start = Date()
            let end = Calendar.current.date(byAdding: .day, value: 7, to: start) ?? start
            let classes = try await scheduleService.fetchProfessorSchedule(professorId: professorId, startDate: start, endDate: end)
            upcomingClasses = classes.count
        } catch {}
    }
}

struct ProfessorStudentsView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var students: [UserProfile] = []

    var body: some View {
        List(students) { student in
            VStack(alignment: .leading) {
                Text(student.displayName).font(.headline)
                Text(student.email).font(.caption).foregroundStyle(.secondary)
            }
        }
        .navigationTitle("Meus alunos")
        .task { await load() }
    }

    private func load() async {
        guard let professorId = authService.userId else { return }
        do {
            let all = try await authService.getUsersByCompany(role: .student)
            students = all.filter {
                ($0.professorId == professorId || $0.professorId == RotationValue.rotation) && $0.isUserActive
            }.sorted { $0.displayName.localizedCaseInsensitiveCompare($1.displayName) == .orderedAscending }
        } catch {}
    }
}

struct ProfessorScheduleView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var selectedDate = Date()
    @State private var classes: [ScheduledClass] = []
    @State private var students: [UserProfile] = []

    private let scheduleService = ScheduleService()

    private var formattedSelectedDate: String {
        DateHelpers.shortDate(selectedDate)
    }

    var body: some View {
        VStack(spacing: 0) {
            DatePicker("Data", selection: $selectedDate, displayedComponents: .date)
                .datePickerStyle(.graphical)
                .padding(.horizontal)

            if classes.isEmpty {
                EmptyStateView(
                    title: "Nenhuma aula neste dia",
                    message: "Você não possui alunos agendados para \(formattedSelectedDate).",
                    illustrationStyle: .scheduleNoClasses
                )
            } else {
                List(classes) { item in
                    VStack(alignment: .leading) {
                        Text(item.studentName ?? "Aluno").font(.headline)
                        Text(item.startTime ?? "--:--").foregroundStyle(.secondary)
                    }
                }
                .listStyle(.insetGrouped)
            }
        }
        .navigationTitle("Agenda")
        .task { await loadStudents() }
        .onChange(of: selectedDate) { _ in Task { await loadClasses() } }
        .onAppear { Task { await loadClasses() } }
    }

    private func loadStudents() async {
        do { students = try await authService.getUsersByCompany(role: .student) } catch {}
    }

    private func loadClasses() async {
        guard let professorId = authService.userId else { return }
        let start = Calendar.current.startOfDay(for: selectedDate)
        let end = Calendar.current.date(byAdding: .day, value: 1, to: start) ?? selectedDate
        do {
            classes = try await scheduleService.fetchProfessorSchedule(professorId: professorId, startDate: start, endDate: end)
                .map { item in
                    var copy = item
                    copy.studentName = students.first { $0.id == item.studentId }?.displayName ?? item.experimentalStudentName ?? "Aluno"
                    return copy
                }
        } catch {}
    }
}

struct ProfessorEarningsView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var payments: [ProfessorPayment] = []

    private let paymentService = PaymentService()

    var body: some View {
        List(payments) { payment in
            VStack(alignment: .leading) {
                Text(CurrencyFormatter.formatWithSymbol(payment.amount, language: authService.company?.language))
                    .font(.headline)
                Text(DateHelpers.shortDate(payment.paymentDate)).font(.caption).foregroundStyle(.secondary)
            }
        }
        .navigationTitle("Ganhos")
        .task { await load() }
    }

    private func load() async {
        guard let companyId = authService.companyId, let professorId = authService.userId else { return }
        do { payments = try await paymentService.fetchProfessorPayments(companyId: companyId, professorId: professorId) }
        catch {}
    }
}

struct ProfessorAttendanceView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var students: [UserProfile] = []
    @State private var selectedStudentId = ""
    @State private var date = Date()
    @State private var present = true
    @State private var notes = ""

    private let attendanceService = AttendanceService()

    var body: some View {
        Form {
            Picker("Aluno", selection: $selectedStudentId) {
                Text("Selecione").tag("")
                ForEach(students) { student in Text(student.displayName).tag(student.id) }
            }
            DatePicker("Data", selection: $date, displayedComponents: .date)
            Toggle("Presente", isOn: $present)
            TextField("Observações", text: $notes, axis: .vertical)
            Button("Salvar presença") { Task { await save() } }
        }
        .navigationTitle("Presença")
        .task { await load() }
    }

    private func load() async {
        guard let professorId = authService.userId else { return }
        do {
            let all = try await authService.getUsersByCompany(role: .student)
            students = all.filter { $0.professorId == professorId || $0.professorId == RotationValue.rotation }
        } catch {}
    }

    private func save() async {
        guard let professorId = authService.userId, let companyId = authService.companyId, !selectedStudentId.isEmpty else { return }
        do {
            try await attendanceService.saveAttendance([
                "studentId": selectedStudentId,
                "professorId": professorId,
                "companyId": companyId,
                "date": date,
                "present": present,
                "notes": notes
            ])
            ToastManager.shared.success("Presença registrada")
        } catch { ToastManager.shared.error(error.localizedDescription) }
    }
}

struct ProfessorEvolutionView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var students: [UserProfile] = []
    @State private var selectedStudentId = ""
    @State private var records: [EvolutionRecord] = []
    @State private var notes = ""
    @State private var exercises = ""

    private let evolutionService = EvolutionService()

    var body: some View {
        Form {
            Picker("Aluno", selection: $selectedStudentId) {
                Text("Selecione").tag("")
                ForEach(students) { student in Text(student.displayName).tag(student.id) }
            }
            Section("Novo registro") {
                TextField("Exercícios", text: $exercises, axis: .vertical)
                TextField("Observações", text: $notes, axis: .vertical)
                Button("Salvar evolução") { Task { await save() } }
            }
            Section("Histórico") {
                ForEach(records) { record in
                    VStack(alignment: .leading) {
                        Text(DateHelpers.shortDate(record.date)).font(.headline)
                        if let exercises = record.exercises, !exercises.isEmpty { Text(exercises).font(.caption) }
                        Text(record.notes).font(.caption).foregroundStyle(.secondary)
                    }
                }
            }
        }
        .navigationTitle("Evolução")
        .task { await loadStudents() }
        .onChange(of: selectedStudentId) { _ in Task { await loadRecords() } }
    }

    private func loadStudents() async {
        guard let professorId = authService.userId else { return }
        do {
            let all = try await authService.getUsersByCompany(role: .student)
            students = all.filter { $0.professorId == professorId || $0.professorId == RotationValue.rotation }
        } catch {}
    }

    private func loadRecords() async {
        guard !selectedStudentId.isEmpty else { return }
        do { records = try await evolutionService.fetchEvolutions(studentId: selectedStudentId) }
        catch {}
    }

    private func save() async {
        guard let professorId = authService.userId, let companyId = authService.companyId, !selectedStudentId.isEmpty else { return }
        do {
            try await evolutionService.saveEvolution([
                "studentId": selectedStudentId,
                "professorId": professorId,
                "companyId": companyId,
                "date": Date(),
                "notes": notes,
                "exercises": exercises
            ])
            notes = ""
            exercises = ""
            ToastManager.shared.success("Evolução salva")
            await loadRecords()
        } catch { ToastManager.shared.error(error.localizedDescription) }
    }
}

struct ProfessorMessagesView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var messages: [StudentMessage] = []
    @State private var filter = "all"

    private let messageService = MessageService()

    private var filtered: [StudentMessage] {
        switch filter {
        case "unread": return messages.filter { $0.isRead != true }
        case "read": return messages.filter { $0.isRead == true }
        default: return messages
        }
    }

    var body: some View {
        List {
            Picker("Status", selection: $filter) {
                Text("Todas").tag("all")
                Text("Não lidas").tag("unread")
                Text("Lidas").tag("read")
            }
            .pickerStyle(.segmented)

            ForEach(filtered) { message in
                VStack(alignment: .leading, spacing: 6) {
                    HStack {
                        Text(message.subject).font(.headline)
                        if message.isRead != true {
                            Text("Nova").font(.caption2).padding(.horizontal, 6).padding(.vertical, 2)
                                .background(Color.blue.opacity(0.15)).clipShape(Capsule())
                        }
                    }
                    Text(message.message).font(.caption).foregroundStyle(.secondary)
                }
                .onTapGesture { Task { await markRead(message) } }
            }
        }
        .navigationTitle("Mensagens")
        .task { await load() }
    }

    private func load() async {
        guard let professorId = authService.userId else { return }
        do { messages = try await messageService.fetchMessages(professorId: professorId) }
        catch {}
    }

    private func markRead(_ message: StudentMessage) async {
        do {
            try await messageService.markAsRead(id: message.id)
            await load()
        } catch {}
    }
}

struct ProfessorMoreView: View {
    @EnvironmentObject private var authService: AuthService

    var body: some View {
        NavigationStack {
            List {
                NavigationLink("Ganhos") { ProfessorEarningsView() }
                NavigationLink("Presença") { ProfessorAttendanceView() }
                NavigationLink("Evolução") { ProfessorEvolutionView() }
                NavigationLink("Mensagens") { ProfessorMessagesView() }
                NavigationLink("Anamnese") { AnamnesisListView() }
                Button("Sair", role: .destructive) { try? authService.logout() }
            }
            .navigationTitle("Mais")
        }
    }
}

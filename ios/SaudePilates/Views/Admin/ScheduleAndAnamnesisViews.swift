import SwiftUI
import FirebaseFirestore

struct AdminScheduleView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var professors: [UserProfile] = []
    @State private var students: [UserProfile] = []
    @State private var selectedProfessorId = ""
    @State private var selectedDate = Date()
    @State private var classes: [ScheduledClass] = []
    @State private var showingForm = false
    @State private var isLoading = true

    private let scheduleService = ScheduleService()

    var body: some View {
        VStack {
            Form {
                Picker("Professor", selection: $selectedProfessorId) {
                    Text("Selecione").tag("")
                    ForEach(professors.filter(\.isUserActive)) { professor in
                        Text(professor.displayName).tag(professor.id)
                    }
                }
                DatePicker("Data", selection: $selectedDate, displayedComponents: .date)
            }
            .frame(maxHeight: 160)

            List(classes) { item in
                VStack(alignment: .leading) {
                    Text(item.studentName ?? studentName(for: item.studentId)).font(.headline)
                    Text(item.startTime ?? "--:--").font(.caption).foregroundStyle(.secondary)
                }
                .swipeActions {
                    Button(role: .destructive) {
                        Task { await deleteClass(item) }
                    } label: { Label("Remover", systemImage: "trash") }
                }
            }
        }
        .navigationTitle("Agenda")
        .toolbar {
            ToolbarItem(placement: .primaryAction) {
                Button { showingForm = true } label: { Image(systemName: "plus") }
                    .disabled(selectedProfessorId.isEmpty)
            }
        }
        .sheet(isPresented: $showingForm) {
            ScheduleFormView(
                professorId: selectedProfessorId,
                students: students,
                defaultDate: selectedDate,
                companyId: authService.companyId ?? ""
            ) { Task { await loadClasses() } }
        }
        .task { await load() }
        .onChange(of: selectedProfessorId) { _ in Task { await loadClasses() } }
        .onChange(of: selectedDate) { _ in Task { await loadClasses() } }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func studentName(for id: String?) -> String {
        students.first { $0.id == id }?.displayName ?? "Experimental"
    }

    private func load() async {
        isLoading = true
        defer { isLoading = false }
        do {
            async let fetchedProfessors = authService.getUsersByCompany(role: .professor)
            async let fetchedStudents = authService.getUsersByCompany(role: .student)
            professors = try await fetchedProfessors
            students = try await fetchedStudents
            if selectedProfessorId.isEmpty { selectedProfessorId = professors.first?.id ?? "" }
            await loadClasses()
        } catch { toastManager.error(error.localizedDescription) }
    }

    private func loadClasses() async {
        guard !selectedProfessorId.isEmpty else { return }
        let start = Calendar.current.startOfDay(for: selectedDate)
        let end = Calendar.current.date(byAdding: .day, value: 1, to: start) ?? selectedDate
        do {
            let fetched = try await scheduleService.fetchProfessorSchedule(
                professorId: selectedProfessorId,
                startDate: start,
                endDate: end
            )
            classes = fetched.map { item in
                var copy = item
                copy.studentName = item.experimental == true
                    ? (item.experimentalStudentName ?? "Experimental")
                    : studentName(for: item.studentId)
                return copy
            }
        } catch { toastManager.error(error.localizedDescription) }
    }

    private func deleteClass(_ item: ScheduledClass) async {
        do {
            try await scheduleService.deleteScheduledClass(id: item.id)
            toastManager.success("Aula removida")
            await loadClasses()
        } catch { toastManager.error(error.localizedDescription) }
    }
}

struct ScheduleFormView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var toastManager: ToastManager

    let professorId: String
    let students: [UserProfile]
    let defaultDate: Date
    let companyId: String
    let onSaved: () -> Void

    @State private var selectedStudentId = ""
    @State private var date = Date()
    @State private var startTime = "09:00"
    @State private var duration = 50

    private let scheduleService = ScheduleService()

    var body: some View {
        NavigationStack {
            Form {
                Picker("Aluno", selection: $selectedStudentId) {
                    Text("Selecione").tag("")
                    ForEach(students.filter(\.isUserActive)) { student in
                        Text(student.displayName).tag(student.id)
                    }
                }
                DatePicker("Data", selection: $date, displayedComponents: .date)
                TextField("Horário (HH:mm)", text: $startTime)
                Stepper("Duração: \(duration) min", value: $duration, in: 30...120, step: 10)
            }
            .navigationTitle("Nova aula")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Cancelar") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) { Button("Salvar") { Task { await save() } } }
            }
            .onAppear { date = defaultDate }
        }
    }

    private func save() async {
        guard !selectedStudentId.isEmpty else { return }
        do {
            _ = try await scheduleService.saveScheduledClass([
                "professorId": professorId,
                "studentId": selectedStudentId,
                "companyId": companyId,
                "date": date,
                "startTime": startTime,
                "duration": duration
            ])
            toastManager.success("Aula agendada")
            onSaved()
            dismiss()
        } catch { toastManager.error(error.localizedDescription) }
    }
}

struct AnamnesisListView: View {
    @EnvironmentObject private var authService: AuthService
    @State private var students: [UserProfile] = []
    @State private var selectedStudentId = ""
    @State private var records: [AnamnesisRecord] = []
    @State private var selectedRecord: AnamnesisRecord?
    @State private var showingNewForm = false
    @State private var isLoading = true

    private let anamnesisService = AnamnesisService()

    var body: some View {
        Form {
            Picker("Aluno", selection: $selectedStudentId) {
                Text("Selecione").tag("")
                ForEach(students.filter(\.isUserActive)) { student in
                    Text(student.displayName).tag(student.id)
                }
            }

            if !records.isEmpty {
                Section("Histórico") {
                    ForEach(records) { record in
                        Button(DateHelpers.shortDate(FirestoreDate.decode(record.performedAt) ?? Date())) {
                            selectedRecord = record
                        }
                    }
                }
            }

            if !selectedStudentId.isEmpty {
                Section {
                    Button("Nova anamnese") { showingNewForm = true }
                }
            }
        }
        .navigationTitle("Anamnese")
        .sheet(item: $selectedRecord) { record in
            AnamnesisFormView(record: record) { Task { await loadRecords() } }
        }
        .sheet(isPresented: $showingNewForm) {
            AnamnesisFormView(record: AnamnesisRecord(id: "", studentId: selectedStudentId)) {
                Task { await loadRecords() }
            }
        }
        .task { await loadStudents() }
        .onChange(of: selectedStudentId) { _ in Task { await loadRecords() } }
        .overlay { LoadingOverlay(isLoading: isLoading) }
    }

    private func loadStudents() async {
        isLoading = true
        defer { isLoading = false }
        do { students = try await authService.getUsersByCompany(role: .student) }
        catch {}
    }

    private func loadRecords() async {
        guard !selectedStudentId.isEmpty else { return }
        do { records = try await anamnesisService.fetchByStudent(studentId: selectedStudentId) }
        catch {}
    }
}

struct AnamnesisFormView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    let record: AnamnesisRecord
    let onSaved: () -> Void

    @State private var mainComplaint = ""
    @State private var medicalHistory = ""
    @State private var surgeries = ""
    @State private var medications = ""
    @State private var physicalActivity = ""
    @State private var goals = ""
    @State private var observations = ""

    private let anamnesisService = AnamnesisService()

    var body: some View {
        NavigationStack {
            Form {
                Section("Queixa principal") { TextField("Queixa", text: $mainComplaint, axis: .vertical) }
                Section("Histórico médico") { TextField("Histórico", text: $medicalHistory, axis: .vertical) }
                Section("Cirurgias") { TextField("Cirurgias", text: $surgeries, axis: .vertical) }
                Section("Medicamentos") { TextField("Medicamentos", text: $medications, axis: .vertical) }
                Section("Atividade física") { TextField("Atividade", text: $physicalActivity, axis: .vertical) }
                Section("Objetivos") { TextField("Objetivos", text: $goals, axis: .vertical) }
                Section("Observações") { TextField("Observações", text: $observations, axis: .vertical) }
            }
            .navigationTitle("Anamnese")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) { Button("Fechar") { dismiss() } }
                ToolbarItem(placement: .confirmationAction) { Button("Salvar") { Task { await save() } } }
            }
            .onAppear {
                mainComplaint = record.mainComplaint ?? ""
                medicalHistory = record.medicalHistory ?? ""
                surgeries = record.surgeries ?? ""
                medications = record.medications ?? ""
                physicalActivity = record.physicalActivity ?? ""
                goals = record.goals ?? ""
                observations = record.observations ?? ""
            }
        }
    }

    private func save() async {
        guard let companyId = authService.companyId else { return }
        do {
            _ = try await anamnesisService.save(
                studentId: record.studentId,
                companyId: companyId,
                data: [
                    "mainComplaint": mainComplaint,
                    "medicalHistory": medicalHistory,
                    "surgeries": surgeries,
                    "medications": medications,
                    "physicalActivity": physicalActivity,
                    "goals": goals,
                    "observations": observations
                ],
                id: record.id.isEmpty ? nil : record.id
            )
            toastManager.success("Anamnese salva")
            onSaved()
            dismiss()
        } catch { toastManager.error(error.localizedDescription) }
    }
}

struct CompanySettingsView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var companyName = ""
    @State private var language = "pt"

    var body: some View {
        Form {
            Section("Empresa") {
                TextField("Nome", text: $companyName)
                Picker("Idioma", selection: $language) {
                    Text("Português").tag("pt")
                    Text("English").tag("en")
                    Text("Español").tag("es")
                    Text("Français").tag("fr")
                }
            }
            Section {
                Button("Salvar") { Task { await save() } }
            }
        }
        .navigationTitle("Configurações")
        .onAppear {
            companyName = authService.company?.name ?? ""
            language = authService.company?.language ?? "pt"
        }
    }

    private func save() async {
        guard let companyId = authService.companyId else { return }
        do {
            try await Firestore.firestore().collection("companies").document(companyId).updateData([
                "name": companyName,
                "language": language
            ])
            await authService.fetchCompany(companyId: companyId)
            toastManager.success("Configurações salvas")
        } catch { toastManager.error(error.localizedDescription) }
    }
}

struct SubscriptionGateView: View {
    let subscription: SubscriptionInfo?
    var embedded = false

    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "creditcard.circle.fill")
                .font(.system(size: 48))
                .foregroundStyle(.indigo)
            Text("Assinatura")
                .font(.title2.bold())
            if let subscription {
                Text(subscription.isValid ? "Assinatura ativa" : "Assinatura expirada")
                if let expiration = subscription.expirationDate {
                    Text("Validade: \(DateHelpers.shortDate(expiration))")
                        .foregroundStyle(.secondary)
                }
            } else {
                Text("Gerencie a assinatura da empresa pelo portal web.")
                    .multilineTextAlignment(.center)
                    .foregroundStyle(.secondary)
            }
            Link("Abrir portal de assinatura", destination: URL(string: "https://saudepilates.com/admin/subscription")!)
                .buttonStyle(.borderedProminent)
        }
        .padding()
        .navigationTitle(embedded ? "Assinatura" : "")
    }
}

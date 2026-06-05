import SwiftUI

struct LoginView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var email = ""
    @State private var password = ""
    @State private var showingRegister = false

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("Email", text: $email)
                        .textInputAutocapitalization(.never)
                        .keyboardType(.emailAddress)
                    SecureField("Senha", text: $password)
                }

                Section {
                    Button("Entrar") {
                        Task { await login() }
                    }
                    .disabled(email.isEmpty || password.isEmpty || authService.isLoading)
                }

                Section {
                    Button("Criar conta da empresa") {
                        showingRegister = true
                    }
                }
            }
            .navigationTitle("Saúde Pilates")
            .sheet(isPresented: $showingRegister) {
                RegisterView()
            }
            .overlay { LoadingOverlay(isLoading: authService.isLoading) }
        }
    }

    private func login() async {
        do {
            try await authService.login(email: email, password: password)
        } catch {
            toastManager.error(error.localizedDescription)
        }
    }
}

struct RegisterView: View {
    @Environment(\.dismiss) private var dismiss
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var companyName = ""
    @State private var adminName = ""
    @State private var email = ""
    @State private var password = ""

    var body: some View {
        NavigationStack {
            Form {
                Section("Empresa") {
                    TextField("Nome da empresa", text: $companyName)
                }
                Section("Administrador") {
                    TextField("Seu nome", text: $adminName)
                    TextField("Email", text: $email)
                        .textInputAutocapitalization(.never)
                        .keyboardType(.emailAddress)
                    SecureField("Senha", text: $password)
                }
                Section {
                    Button("Criar conta") {
                        Task { await register() }
                    }
                    .disabled(companyName.isEmpty || adminName.isEmpty || email.isEmpty || password.count < 6)
                }
            }
            .navigationTitle("Nova conta")
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Fechar") { dismiss() }
                }
            }
            .overlay { LoadingOverlay(isLoading: authService.isLoading) }
        }
    }

    private func register() async {
        do {
            try await authService.register(email: email, password: password, companyName: companyName, adminName: adminName)
            dismiss()
            toastManager.success("Conta criada com sucesso")
        } catch {
            toastManager.error(error.localizedDescription)
        }
    }
}

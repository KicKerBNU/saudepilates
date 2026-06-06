import SwiftUI

struct LoginView: View {
    @EnvironmentObject private var authService: AuthService
    @EnvironmentObject private var toastManager: ToastManager

    @State private var email = ""
    @State private var password = ""
    @State private var showingRegister = false

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 28) {
                    AuthBrandHeader()

                    AuthGroupedSection(title: "Conta") {
                        TextField("Email", text: $email)
                            .textInputAutocapitalization(.never)
                            .keyboardType(.emailAddress)
                            .textContentType(.emailAddress)
                            .autocorrectionDisabled()
                            .padding(.horizontal, 16)
                            .padding(.vertical, 12)

                        AuthFieldDivider()

                        SecureField("Senha", text: $password)
                            .textContentType(.password)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 12)
                    }

                    Button {
                        Task { await login() }
                    } label: {
                        Text("Entrar")
                            .font(.headline)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(BrandColors.indigo)
                    .disabled(email.isEmpty || password.isEmpty || authService.isLoading)

                    Button("Criar conta da empresa") {
                        showingRegister = true
                    }
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(BrandColors.indigo)

                    Text(AppVersion.display)
                        .font(.caption)
                        .foregroundStyle(.tertiary)
                        .padding(.top, 4)
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 24)
            }
            .background(Color(uiColor: .systemGroupedBackground))
            .scrollDismissesKeyboard(.interactively)
            .navigationBarHidden(true)
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
            ScrollView {
                VStack(spacing: 24) {
                    AuthBrandHeader(compact: true)

                    AuthGroupedSection(title: "Empresa") {
                        TextField("Nome da empresa", text: $companyName)
                            .textContentType(.organizationName)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 12)
                    }

                    AuthGroupedSection(title: "Administrador") {
                        TextField("Seu nome", text: $adminName)
                            .textContentType(.name)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 12)

                        AuthFieldDivider()

                        TextField("Email", text: $email)
                            .textInputAutocapitalization(.never)
                            .keyboardType(.emailAddress)
                            .textContentType(.emailAddress)
                            .autocorrectionDisabled()
                            .padding(.horizontal, 16)
                            .padding(.vertical, 12)

                        AuthFieldDivider()

                        SecureField("Senha (mín. 6 caracteres)", text: $password)
                            .textContentType(.newPassword)
                            .padding(.horizontal, 16)
                            .padding(.vertical, 12)
                    }

                    Button {
                        Task { await register() }
                    } label: {
                        Text("Criar conta")
                            .font(.headline)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 14)
                    }
                    .buttonStyle(.borderedProminent)
                    .tint(BrandColors.indigo)
                    .disabled(companyName.isEmpty || adminName.isEmpty || email.isEmpty || password.count < 6)
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 16)
            }
            .background(Color(uiColor: .systemGroupedBackground))
            .scrollDismissesKeyboard(.interactively)
            .navigationTitle("Nova conta")
            .navigationBarTitleDisplayMode(.inline)
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

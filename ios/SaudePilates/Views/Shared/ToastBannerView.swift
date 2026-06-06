import SwiftUI

struct ToastBannerView: View {
    @EnvironmentObject private var toastManager: ToastManager

    var body: some View {
        if let toast = toastManager.currentToast {
            HStack(spacing: 12) {
                Image(systemName: icon(for: toast.type))
                Text(toast.message)
                    .font(.subheadline.weight(.medium))
                Spacer(minLength: 0)
            }
            .foregroundStyle(foreground(for: toast.type))
            .padding()
            .background(background(for: toast.type))
            .clipShape(RoundedRectangle(cornerRadius: 14, style: .continuous))
            .padding(.horizontal)
            .padding(.top, 8)
            .transition(.move(edge: .top).combined(with: .opacity))
            .animation(.spring(), value: toast.id)
        }
    }

    private func icon(for type: ToastType) -> String {
        switch type {
        case .success: return "checkmark.circle.fill"
        case .error: return "xmark.circle.fill"
        case .warning: return "exclamationmark.triangle.fill"
        case .info: return "info.circle.fill"
        }
    }

    private func foreground(for type: ToastType) -> Color {
        switch type {
        case .success: return .green
        case .error: return .red
        case .warning: return .orange
        case .info: return .blue
        }
    }

    private func background(for type: ToastType) -> Color {
        switch type {
        case .success: return Color.green.opacity(0.12)
        case .error: return Color.red.opacity(0.12)
        case .warning: return Color.orange.opacity(0.12)
        case .info: return Color.blue.opacity(0.12)
        }
    }
}

struct ConfirmDialogModifier: ViewModifier {
    @Binding var isPresented: Bool
    let title: String
    let message: String
    let confirmTitle: String
    let isDestructive: Bool
    let onConfirm: () -> Void

    func body(content: Content) -> some View {
        content.confirmationDialog(title, isPresented: $isPresented, titleVisibility: .visible) {
            Button(confirmTitle, role: isDestructive ? .destructive : nil, action: onConfirm)
            Button("Cancelar", role: .cancel) {}
        } message: {
            Text(message)
        }
    }
}

extension View {
    func confirmDialog(
        isPresented: Binding<Bool>,
        title: String,
        message: String,
        confirmTitle: String = "Confirmar",
        isDestructive: Bool = false,
        onConfirm: @escaping () -> Void
    ) -> some View {
        modifier(ConfirmDialogModifier(
            isPresented: isPresented,
            title: title,
            message: message,
            confirmTitle: confirmTitle,
            isDestructive: isDestructive,
            onConfirm: onConfirm
        ))
    }
}

struct LoadingOverlay: View {
    let isLoading: Bool

    var body: some View {
        if isLoading {
            ZStack {
                Color.black.opacity(0.08).ignoresSafeArea()
                ProgressView()
                    .controlSize(.large)
            }
        }
    }
}


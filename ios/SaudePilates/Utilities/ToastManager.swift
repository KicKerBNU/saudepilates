import SwiftUI

enum ToastType {
    case success, error, warning, info
}

struct ToastMessage: Identifiable, Equatable {
    let id = UUID()
    let message: String
    let type: ToastType
}

@MainActor
final class ToastManager: ObservableObject {
    static let shared = ToastManager()

    @Published var currentToast: ToastMessage?

    func show(_ message: String, type: ToastType = .success) {
        currentToast = ToastMessage(message: message, type: type)
        Task {
            try? await Task.sleep(nanoseconds: 4_000_000_000)
            if currentToast?.message == message {
                currentToast = nil
            }
        }
    }

    func success(_ message: String) { show(message, type: .success) }
    func error(_ message: String) { show(message, type: .error) }
}

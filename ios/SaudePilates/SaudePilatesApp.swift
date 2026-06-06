import SwiftUI
import FirebaseCore

@main
struct SaudePilatesApp: App {
    @StateObject private var authService: AuthService
    @StateObject private var toastManager: ToastManager

    init() {
        // Must configure Firebase before AuthService touches Auth.auth() / Firestore.
        FirebaseConfiguration.configure()
        _authService = StateObject(wrappedValue: AuthService.shared)
        _toastManager = StateObject(wrappedValue: ToastManager.shared)
        if FirebaseApp.app() != nil {
            AuthService.shared.start()
        }
    }

    var body: some Scene {
        WindowGroup {
            Group {
                if FirebaseApp.app() == nil {
                    FirebaseSetupView()
                } else {
                    ContentView()
                }
            }
                .environmentObject(authService)
                .environmentObject(toastManager)
                .overlay(alignment: .top) {
                    ToastBannerView()
                        .environmentObject(toastManager)
                }
        }
    }
}

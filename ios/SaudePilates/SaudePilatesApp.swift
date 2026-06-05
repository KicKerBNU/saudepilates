import SwiftUI
import FirebaseCore

@main
struct SaudePilatesApp: App {
    @StateObject private var authService = AuthService.shared
    @StateObject private var toastManager = ToastManager.shared

    init() {
        FirebaseConfiguration.configure()
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

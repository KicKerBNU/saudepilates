import Foundation
import FirebaseCore

enum FirebaseConfiguration {
    private static let placeholderMarkers = [
        "replace_with",
        "REPLACE_WITH"
    ]

    static func configure() {
        guard
            let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
            let options = FirebaseOptions(contentsOfFile: path),
            isValidFirebaseOptions(options)
        else {
            NSLog("Firebase not configured: add GoogleService-Info.plist from Firebase Console (see ios/README.md)")
            return
        }

        FirebaseApp.configure(options: options)
    }

    private static func isValidFirebaseOptions(_ options: FirebaseOptions) -> Bool {
        guard options.googleAppID.contains(":ios:") else { return false }
        guard let apiKey = options.apiKey, !apiKey.isEmpty else { return false }
        return !containsPlaceholder(options.googleAppID)
            && !containsPlaceholder(apiKey)
    }

    private static func containsPlaceholder(_ value: String) -> Bool {
        placeholderMarkers.contains { value.localizedCaseInsensitiveContains($0) }
    }
}

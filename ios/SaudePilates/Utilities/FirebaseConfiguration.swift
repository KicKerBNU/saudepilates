import Foundation
import FirebaseCore

enum FirebaseConfiguration {
    /// Replace this value after registering the iOS app in Firebase Console.
    /// Firebase Console → Project Settings → Your apps → Add iOS app → copy GOOGLE_APP_ID
    static let iosGoogleAppID = "1:311012649134:ios:replace_with_firebase_console_ios_app_id"

    static let apiKey = "AIzaSyB6xfteJelZLxiszYwiprugoSRSnZn4YGM"
    static let gcmSenderID = "311012649134"
    static let projectID = "saudepilates-170df"
    static let storageBucket = "saudepilates-170df.appspot.com"
    static let bundleID = "com.saudepilates.app"

    static var isConfigured: Bool {
        !iosGoogleAppID.contains("replace_with")
    }

    static func configure() {
        if let path = Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist"),
           let options = FirebaseOptions(contentsOfFile: path),
           options.googleAppID.contains(":ios:"),
           !options.googleAppID.contains("replace_with") {
            FirebaseApp.configure(options: options)
            return
        }

        guard isConfigured else {
            NSLog("Firebase not configured: set iosGoogleAppID in FirebaseConfiguration.swift")
            return
        }

        let options = FirebaseOptions(googleAppID: iosGoogleAppID, gcmSenderID: gcmSenderID)
        options.apiKey = apiKey
        options.projectID = projectID
        options.storageBucket = storageBucket
        options.bundleID = bundleID
        FirebaseApp.configure(options: options)
    }
}

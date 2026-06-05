package com.saudepilates.app.util

object FirebaseConfig {
    const val PROJECT_ID = "saudepilates-170df"
    const val PACKAGE_NAME = "com.saudepilates.app"

    fun isGoogleServicesConfigured(jsonContent: String?): Boolean {
        if (jsonContent.isNullOrBlank()) return false
        return jsonContent.contains(":android:") && !jsonContent.contains("replace_with_firebase_console")
    }
}

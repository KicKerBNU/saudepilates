package com.saudepilates.app

import android.app.Application
import com.google.firebase.FirebaseApp

class SaudePilatesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
    }
}

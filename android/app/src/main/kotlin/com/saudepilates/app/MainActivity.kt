package com.saudepilates.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.saudepilates.app.ui.auth.LoginScreen
import com.saudepilates.app.ui.components.ToastHost
import com.saudepilates.app.ui.navigation.AppNavigation
import com.saudepilates.app.ui.shared.FirebaseSetupScreen
import com.saudepilates.app.ui.theme.SaudePilatesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaudePilatesTheme {
                SaudePilatesRoot()
            }
        }
    }
}

@Composable
fun SaudePilatesRoot() {
    val auth = AppContainer.authRepository
    val userProfile by auth.userProfile.collectAsState()
    val firebaseConfigured = remember { isFirebaseConfigured() }

    LaunchedEffect(Unit) {
        if (firebaseConfigured) auth.loadCurrentUser()
    }

    Box(Modifier.fillMaxSize()) {
        when {
            !firebaseConfigured -> FirebaseSetupScreen()
            userProfile == null && !auth.isAuthenticated -> LoginScreen()
            else -> AppNavigation()
        }
        ToastHost(Modifier.align(Alignment.TopCenter))
    }
}

private fun isFirebaseConfigured(): Boolean {
    return try {
        val appId = FirebaseApp.getInstance().options.applicationId
        appId.contains(":android:") && !appId.contains("replace_with")
    } catch (_: Exception) {
        false
    }
}

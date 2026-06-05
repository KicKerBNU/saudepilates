package com.saudepilates.app.ui.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.saudepilates.app.ui.components.IosGroup
import com.saudepilates.app.ui.components.IosScreen
import com.saudepilates.app.ui.theme.IosColors

@Composable
fun FirebaseSetupScreen(modifier: Modifier = Modifier) {
    IosScreen(title = "Configuração", modifier = modifier) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.LocalFireDepartment, null, tint = IosColors.orange, modifier = Modifier.height(48.dp))
            Spacer(Modifier.height(16.dp))
            Text("Firebase não configurado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Registre o app Android no Firebase Console.", color = IosColors.secondaryLabel, modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(Modifier.height(24.dp))
            IosGroup {
                Text("1. Projeto saudepilates-170df", Modifier.padding(16.dp))
                Text("2. App Android: com.saudepilates.app", Modifier.padding(16.dp))
                Text("3. Baixe google-services.json", Modifier.padding(16.dp))
                Text("4. Substitua android/app/google-services.json", Modifier.padding(16.dp))
            }
        }
    }
}

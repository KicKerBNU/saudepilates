package com.saudepilates.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.saudepilates.app.ui.theme.IosColors
import com.saudepilates.app.util.ToastManager

@Composable
fun LoadingOverlay(isLoading: Boolean, modifier: Modifier = Modifier) = IosLoadingOverlay(isLoading)

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier, icon: ImageVector? = null, color: Color = IosColors.indigo) {
    IosStatCard(title, value, color, modifier)
}

@Composable
fun ToastHost(modifier: Modifier = Modifier) {
    val toast by ToastManager.toast.collectAsState()
    LaunchedEffect(toast) {
        toast?.let {
            kotlinx.coroutines.delay(2800)
            ToastManager.clear()
        }
    }
    ToastBannerHost(modifier)
}

@Composable
fun ConfirmDialog(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String = "Confirmar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = { TextButton(onClick = onConfirm) { Text(confirmText) } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
            containerColor = IosColors.secondaryGroupedBackground
        )
    }
}

@Composable
fun rememberConfirmState(): Pair<Boolean, (Boolean) -> Unit> {
    var visible by remember { mutableStateOf(false) }
    return visible to { visible = it }
}

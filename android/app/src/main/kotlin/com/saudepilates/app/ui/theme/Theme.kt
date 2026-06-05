package com.saudepilates.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val IosLightScheme = lightColorScheme(
    primary = IosColors.blue,
    onPrimary = IosColors.secondaryGroupedBackground,
    secondary = IosColors.indigo,
    background = IosColors.groupedBackground,
    onBackground = IosColors.label,
    surface = IosColors.secondaryGroupedBackground,
    onSurface = IosColors.label,
    surfaceVariant = IosColors.groupedBackground,
    onSurfaceVariant = IosColors.secondaryLabel,
    outline = IosColors.separator,
    error = IosColors.red
)

private val IosShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(14.dp)
)

@Composable
fun SaudePilatesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Match iOS light grouped style (iOS app is light-mode native)
    MaterialTheme(
        colorScheme = IosLightScheme,
        typography = Typography(),
        shapes = IosShapes,
        content = content
    )
}

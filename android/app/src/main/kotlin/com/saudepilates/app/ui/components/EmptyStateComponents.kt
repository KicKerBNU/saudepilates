package com.saudepilates.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.saudepilates.app.ui.theme.IosColors

enum class EmptyIllustrationStyle {
    ScheduleSelectProfessor,
    ScheduleNoClasses,
    StudentsActive,
    StudentsInactive,
    ProfessorsActive,
    ProfessorsInactive,
    Plans,
    PaymentSelectStudent,
    PaymentVisualization
}

@Composable
fun EmptyIllustration(style: EmptyIllustrationStyle, modifier: Modifier = Modifier) {
    val (primary, secondary) = illustrationIcons(style)
    Box(
        modifier
            .size(168.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        IosColors.indigo.copy(alpha = 0.18f),
                        IosColors.indigo.copy(alpha = 0.06f)
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(primary, contentDescription = null, tint = IosColors.indigo, modifier = Modifier.size(56.dp))
            Icon(secondary, contentDescription = null, tint = IosColors.indigo.copy(alpha = 0.55f), modifier = Modifier.size(22.dp))
        }
    }
}

private fun illustrationIcons(style: EmptyIllustrationStyle): Pair<ImageVector, ImageVector> = when (style) {
    EmptyIllustrationStyle.ScheduleSelectProfessor -> Icons.Default.PersonAdd to Icons.Default.CalendarMonth
    EmptyIllustrationStyle.ScheduleNoClasses -> Icons.Default.Schedule to Icons.Default.FitnessCenter
    EmptyIllustrationStyle.StudentsActive -> Icons.Default.Groups to Icons.Default.DirectionsWalk
    EmptyIllustrationStyle.StudentsInactive -> Icons.Default.PersonOff to Icons.Default.Archive
    EmptyIllustrationStyle.ProfessorsActive -> Icons.Default.VerifiedUser to Icons.Default.FitnessCenter
    EmptyIllustrationStyle.ProfessorsInactive -> Icons.Default.PersonRemove to Icons.Default.Archive
    EmptyIllustrationStyle.Plans -> Icons.Default.List to Icons.Default.CreditCard
    EmptyIllustrationStyle.PaymentSelectStudent -> Icons.Default.PersonAdd to Icons.Default.AttachMoney
    EmptyIllustrationStyle.PaymentVisualization -> Icons.Default.BarChart to Icons.Default.CalendarMonth
}

@Composable
fun IosEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    message: String? = null,
    illustrationStyle: EmptyIllustrationStyle? = null,
    actionTitle: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        illustrationStyle?.let { EmptyIllustration(it) }

        Spacer(Modifier.height(if (illustrationStyle != null) 20.dp else 0.dp))

        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Center
        )

        message?.let {
            Spacer(Modifier.height(8.dp))
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = IosColors.secondaryLabel,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        if (actionTitle != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = IosColors.indigo)
            ) {
                Text(actionTitle)
            }
        }
    }
}

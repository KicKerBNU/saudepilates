package com.saudepilates.app.ui.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saudepilates.app.AppContainer
import com.saudepilates.app.data.models.StudentPayment
import com.saudepilates.app.ui.components.IosEmptyState
import com.saudepilates.app.ui.components.IosGroup
import com.saudepilates.app.ui.components.IosListRow
import com.saudepilates.app.ui.components.IosScreen
import com.saudepilates.app.ui.theme.IosColors
import com.saudepilates.app.util.CurrencyUtils
import com.saudepilates.app.util.DateUtils

@Composable
fun StudentDashboardScreen(modifier: Modifier = Modifier) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()

    IosScreen(title = "Início", modifier = modifier) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Bem-vindo, ${profile?.displayName ?: "Aluno"}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Acompanhe suas aulas e pagamentos pelo app.",
                color = IosColors.secondaryLabel
            )
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun StudentPaymentsScreen(modifier: Modifier = Modifier) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    val company by auth.company.collectAsState()
    var payments by remember { mutableStateOf<List<StudentPayment>>(emptyList()) }

    LaunchedEffect(profile?.id) {
        val studentId = profile?.id ?: return@LaunchedEffect
        val companyId = auth.companyId ?: return@LaunchedEffect
        payments = AppContainer.paymentRepository.fetchStudentPayments(companyId, studentId)
    }

    IosScreen(title = "Meus pagamentos", modifier = modifier) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
            item {
                if (payments.isEmpty()) IosEmptyState("Nenhum pagamento")
                else IosGroup {
                    payments.forEachIndexed { i, payment ->
                        IosListRow(
                            CurrencyUtils.formatWithSymbol(payment.paidAmount, company?.language),
                            DateUtils.shortDate(payment.paymentDate),
                            payment.paymentPeriod,
                            showDivider = i < payments.lastIndex
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentScheduleScreen(modifier: Modifier = Modifier) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    var classes by remember { mutableStateOf(emptyList<com.saudepilates.app.data.models.ScheduledClass>()) }

    LaunchedEffect(profile?.id) {
        val studentId = profile?.id ?: return@LaunchedEffect
        classes = AppContainer.scheduleRepository.fetchStudentSchedule(studentId)
            .filter { it.date >= java.util.Calendar.getInstance().apply { set(java.util.Calendar.HOUR_OF_DAY, 0) }.time }
    }

    IosScreen(title = "Minha agenda", modifier = modifier) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
            item {
                if (classes.isEmpty()) IosEmptyState("Sem aulas agendadas")
                else IosGroup {
                    classes.forEachIndexed { i, item ->
                        IosListRow(DateUtils.shortDate(item.date), item.startTime ?: "--:--", showDivider = i < classes.lastIndex)
                    }
                }
            }
        }
    }
}

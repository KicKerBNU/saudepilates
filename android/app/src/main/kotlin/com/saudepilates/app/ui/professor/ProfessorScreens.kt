package com.saudepilates.app.ui.professor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.saudepilates.app.AppContainer
import com.saudepilates.app.data.models.ProfessorPayment
import com.saudepilates.app.data.models.UserProfile
import com.saudepilates.app.data.models.UserRole
import com.saudepilates.app.ui.admin.AnamnesisScreen
import com.saudepilates.app.ui.components.IosBadge
import com.saudepilates.app.ui.components.IosDropdownField
import com.saudepilates.app.ui.components.IosEmptyState
import com.saudepilates.app.ui.components.IosFormScroll
import com.saudepilates.app.ui.components.IosGroup
import com.saudepilates.app.ui.components.IosLabeledRow
import com.saudepilates.app.ui.components.IosListRow
import com.saudepilates.app.ui.components.IosLoadingOverlay
import com.saudepilates.app.ui.components.IosNavigationRow
import com.saudepilates.app.ui.components.IosScreen
import com.saudepilates.app.ui.components.IosSectionHeader
import com.saudepilates.app.ui.components.IosSegmented
import com.saudepilates.app.util.CurrencyUtils
import com.saudepilates.app.util.DateUtils
import com.saudepilates.app.util.ToastManager
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@Composable
fun ProfessorDashboardScreen(
    onAttendance: () -> Unit,
    onEvolution: () -> Unit,
    onEarnings: () -> Unit,
    onMessages: () -> Unit,
    onAnamnesis: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    val company by auth.company.collectAsState()
    var studentsCount by remember { mutableIntStateOf(0) }
    var monthEarnings by remember { mutableStateOf(0.0) }
    var upcomingClasses by remember { mutableIntStateOf(0) }

    LaunchedEffect(profile?.id) {
        val professorId = profile?.id ?: return@LaunchedEffect
        val companyId = auth.companyId ?: return@LaunchedEffect
        val students = auth.getUsersByCompany(UserRole.STUDENT)
        studentsCount = students.count { it.professorId == professorId && it.isUserActive }
        val payments = AppContainer.paymentRepository.fetchProfessorPayments(companyId, professorId)
        val cal = Calendar.getInstance()
        monthEarnings = payments.filter {
            val c = Calendar.getInstance().apply { time = it.paymentDate }
            c.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && c.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        }.sumOf { it.amount }
        val end = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }.time
        upcomingClasses = AppContainer.scheduleRepository.fetchProfessorSchedule(professorId, Date(), end).size
    }

    IosScreen(title = "Dashboard", modifier = modifier) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                IosSectionHeader(profile?.displayName ?: "Professor")
                IosGroup {
                    IosLabeledRow("Alunos", "$studentsCount")
                    IosLabeledRow("Ganhos do mês", CurrencyUtils.formatWithSymbol(monthEarnings, company?.language))
                    IosLabeledRow("Próximas aulas", "$upcomingClasses", showDivider = false)
                }
            }
            item {
                IosSectionHeader("Ações rápidas")
                IosGroup {
                    IosNavigationRow("Controle de presença", onAttendance)
                    IosNavigationRow("Evolução dos alunos", onEvolution)
                    IosNavigationRow("Histórico de ganhos", onEarnings)
                    IosNavigationRow("Mensagens", onMessages)
                    IosNavigationRow("Anamnese", onAnamnesis, showDivider = false)
                }
            }
        }
    }
}

@Composable
fun ProfessorStudentsScreen(modifier: Modifier = Modifier) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }

    LaunchedEffect(profile?.id) {
        val professorId = profile?.id ?: return@LaunchedEffect
        students = auth.getUsersByCompany(UserRole.STUDENT)
            .filter { it.professorId == professorId && it.isUserActive }
            .sortedBy { it.displayName.lowercase() }
    }

    IosScreen(title = "Meus alunos", modifier = modifier) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
            item {
                if (students.isEmpty()) IosEmptyState("Nenhum aluno")
                else IosGroup {
                    students.forEachIndexed { i, s -> IosListRow(s.displayName, s.email, showDivider = i < students.lastIndex) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorScheduleScreen(modifier: Modifier = Modifier) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    var selectedDate by remember { mutableStateOf(Date()) }
    var showPicker by remember { mutableStateOf(false) }
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var classes by remember { mutableStateOf(emptyList<com.saudepilates.app.data.models.ScheduledClass>()) }

    LaunchedEffect(profile?.id) { students = auth.getUsersByCompany(UserRole.STUDENT) }
    LaunchedEffect(profile?.id, selectedDate) {
        val professorId = profile?.id ?: return@LaunchedEffect
        val start = Calendar.getInstance().apply { time = selectedDate; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0) }.time
        val end = Calendar.getInstance().apply { time = start; add(Calendar.DAY_OF_YEAR, 1) }.time
        classes = AppContainer.scheduleRepository.fetchProfessorSchedule(professorId, start, end)
            .map { c -> c.copy(studentName = students.find { it.id == c.studentId }?.displayName) }
    }

    if (showPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(onDismissRequest = { showPicker = false }, confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { selectedDate = Date(it) }
                showPicker = false
            }) { Text("OK") }
        }) { DatePicker(state) }
    }

    IosScreen(title = "Agenda", modifier = modifier) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                IosGroup {
                    IosListRow("Data selecionada", DateUtils.shortDate(selectedDate), showDivider = false, onClick = { showPicker = true })
                }
            }
            item {
                if (classes.isEmpty()) IosEmptyState(
                    title = "Nenhuma aula neste dia",
                    message = "Você não possui alunos agendados para ${DateUtils.shortDate(selectedDate)}.",
                    illustrationStyle = com.saudepilates.app.ui.components.EmptyIllustrationStyle.ScheduleNoClasses
                )
                else IosGroup {
                    classes.forEachIndexed { i, item ->
                        IosListRow(item.studentName ?: "Aluno", item.startTime ?: "--:--", showDivider = i < classes.lastIndex)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessorMoreHub(
    onEarnings: () -> Unit,
    onAttendance: () -> Unit,
    onEvolution: () -> Unit,
    onMessages: () -> Unit,
    onAnamnesis: () -> Unit,
    onLogout: () -> Unit
) {
    IosScreen(title = "Mais") { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            item {
                IosGroup {
                    IosNavigationRow("Ganhos", onEarnings)
                    IosNavigationRow("Presença", onAttendance)
                    IosNavigationRow("Evolução", onEvolution)
                    IosNavigationRow("Mensagens", onMessages)
                    IosNavigationRow("Anamnese", onAnamnesis, showDivider = false)
                }
            }
            item {
                IosGroup {
                    IosNavigationRow("Sair", onLogout, showDivider = false, destructive = true)
                }
            }
        }
    }
}

@Composable fun ProfessorEarningsScreen(onBack: () -> Unit) = ProfessorPaymentsListScreen("Ganhos", onBack)
@Composable fun ProfessorMoreAnamnesisScreen(onBack: () -> Unit) = AnamnesisScreen(onBack)

@Composable
private fun ProfessorPaymentsListScreen(title: String, onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    val profile by auth.userProfile.collectAsState()
    var payments by remember { mutableStateOf<List<ProfessorPayment>>(emptyList()) }

    LaunchedEffect(profile?.id) {
        val companyId = auth.companyId ?: return@LaunchedEffect
        val professorId = profile?.id ?: return@LaunchedEffect
        payments = AppContainer.paymentRepository.fetchProfessorPayments(companyId, professorId)
    }

    IosScreen(title = title, onBack = onBack) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
            item {
                if (payments.isEmpty()) IosEmptyState("Nenhuma comissão")
                else IosGroup {
                    payments.forEachIndexed { i, p ->
                        IosListRow(CurrencyUtils.formatWithSymbol(p.amount, company?.language), DateUtils.shortDate(p.paymentDate), showDivider = i < payments.lastIndex)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorAttendanceScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var selectedStudentId by remember { mutableStateOf("") }
    var present by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(profile?.id) {
        val professorId = profile?.id ?: return@LaunchedEffect
        students = auth.getUsersByCompany(UserRole.STUDENT).filter { it.professorId == professorId && it.isUserActive }
    }

    IosScreen(title = "Presença", onBack = onBack) { padding ->
        IosFormScroll(Modifier.padding(padding)) {
            IosGroup {
                IosDropdownField("Aluno", listOf("Selecione") + students.map { it.displayName }, listOf("") + students.map { it.id }, selectedStudentId, { selectedStudentId = it })
                androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Presente")
                    Switch(present, { present = it })
                }
                OutlinedTextField(notes, { notes = it }, label = { Text("Observações") }, modifier = Modifier.fillMaxWidth().padding(12.dp), minLines = 2)
                Button(onClick = {
                    scope.launch {
                        val companyId = auth.companyId ?: return@launch
                        AppContainer.attendanceRepository.saveAttendance(mapOf(
                            "studentId" to selectedStudentId, "professorId" to profile?.id, "companyId" to companyId,
                            "date" to Date(), "present" to present, "notes" to notes
                        ))
                        ToastManager.success("Presença registrada")
                    }
                }, modifier = Modifier.fillMaxWidth().padding(12.dp), enabled = selectedStudentId.isNotBlank()) { Text("Salvar presença") }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorEvolutionScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var selectedStudentId by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var exercises by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(profile?.id) {
        val professorId = profile?.id ?: return@LaunchedEffect
        students = auth.getUsersByCompany(UserRole.STUDENT).filter { it.professorId == professorId && it.isUserActive }
    }

    IosScreen(title = "Evolução", onBack = onBack) { padding ->
        IosFormScroll(Modifier.padding(padding)) {
            IosGroup {
                IosDropdownField("Aluno", listOf("Selecione") + students.map { it.displayName }, listOf("") + students.map { it.id }, selectedStudentId, { selectedStudentId = it })
            }
            IosSectionHeader("Novo registro")
            IosGroup {
                OutlinedTextField(exercises, { exercises = it }, label = { Text("Exercícios") }, modifier = Modifier.fillMaxWidth().padding(12.dp), minLines = 2)
                OutlinedTextField(notes, { notes = it }, label = { Text("Observações") }, modifier = Modifier.fillMaxWidth().padding(12.dp), minLines = 2)
                Button(onClick = {
                    scope.launch {
                        val companyId = auth.companyId ?: return@launch
                        AppContainer.evolutionRepository.saveEvolution(mapOf(
                            "studentId" to selectedStudentId, "professorId" to profile?.id, "companyId" to companyId,
                            "date" to Date(), "notes" to notes, "exercises" to exercises
                        ))
                        ToastManager.success("Evolução salva")
                    }
                }, modifier = Modifier.fillMaxWidth().padding(12.dp), enabled = selectedStudentId.isNotBlank()) { Text("Salvar evolução") }
            }
        }
    }
}

@Composable
fun ProfessorMessagesScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    var messages by remember { mutableStateOf(emptyList<com.saudepilates.app.data.models.StudentMessage>()) }
    var filter by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(profile?.id) {
        val professorId = profile?.id ?: return@LaunchedEffect
        messages = AppContainer.messageRepository.fetchMessages(professorId)
    }

    val filtered = when (filter) {
        1 -> messages.filter { it.isRead != true }
        2 -> messages.filter { it.isRead == true }
        else -> messages
    }

    IosScreen(title = "Mensagens", onBack = onBack) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                IosGroup {
                    IosSegmented(listOf("Todas", "Não lidas", "Lidas"), filter, { filter = it }, Modifier.padding(12.dp))
                }
            }
            item {
                if (filtered.isEmpty()) IosEmptyState("Nenhuma mensagem")
                else IosGroup {
                    filtered.forEachIndexed { i, msg ->
                        IosListRow(
                            title = msg.subject,
                            subtitle = msg.message,
                            showDivider = i < filtered.lastIndex,
                            onClick = { scope.launch { AppContainer.messageRepository.markAsRead(msg.id) } },
                            trailing = { if (msg.isRead != true) IosBadge("Nova", com.saudepilates.app.ui.theme.IosColors.blue) }
                        )
                    }
                }
            }
        }
    }
}

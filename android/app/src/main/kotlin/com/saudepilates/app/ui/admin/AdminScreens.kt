package com.saudepilates.app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale
import com.saudepilates.app.AppContainer
import com.saudepilates.app.BuildConfig
import com.saudepilates.app.data.models.AnamnesisRecord
import com.saudepilates.app.data.models.DashboardStats
import com.saudepilates.app.data.models.Plan
import com.saudepilates.app.data.models.ProfessorPayment
import com.saudepilates.app.data.models.RotationValue
import com.saudepilates.app.data.models.StudentPayment
import com.saudepilates.app.data.models.SubscriptionInfo
import com.saudepilates.app.data.models.UserProfile
import com.saudepilates.app.data.models.UserRole
import com.saudepilates.app.ui.components.ConfirmDialog
import com.saudepilates.app.ui.components.IosBadge
import com.saudepilates.app.ui.components.IosDropdownField
import com.saudepilates.app.ui.components.EmptyIllustrationStyle
import com.saudepilates.app.ui.components.IosEmptyState
import com.saudepilates.app.ui.components.IosNavigationRow
import com.saudepilates.app.ui.components.IosFormScroll
import com.saudepilates.app.ui.components.IosGroup
import com.saudepilates.app.ui.components.IosHubList
import com.saudepilates.app.ui.components.IosLabeledRow
import com.saudepilates.app.ui.components.IosListRow
import com.saudepilates.app.ui.components.IosLoadingOverlay
import com.saudepilates.app.ui.components.IosQuickActionRow
import com.saudepilates.app.ui.components.IosScreen
import com.saudepilates.app.ui.components.IosSectionHeader
import com.saudepilates.app.ui.components.IosSegmented
import com.saudepilates.app.ui.components.IosStatCard
import com.saudepilates.app.ui.theme.IosColors
import com.saudepilates.app.util.CurrencyUtils
import com.saudepilates.app.util.DateUtils
import com.saudepilates.app.util.ToastManager
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@Composable
fun SubscriptionGateScreen(subscription: SubscriptionInfo, modifier: Modifier = Modifier) {
    IosScreen(title = "Assinatura", modifier = modifier) { padding ->
        Column(Modifier.padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Assinatura expirada", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(if (subscription.isSubscribed) "Renove sua assinatura para continuar." else "Ative sua assinatura para continuar.", color = IosColors.secondaryLabel)
            subscription.expirationDate?.let { IosLabeledRow("Expiração", DateUtils.shortDate(it), showDivider = false) }
            Button(onClick = { AppContainer.authRepository.logout() }, modifier = Modifier.fillMaxWidth()) { Text("Sair") }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    onRegisterPayment: () -> Unit,
    onMonthlyPayments: () -> Unit,
    onProfessorPayments: () -> Unit,
    onSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var stats by remember { mutableStateOf(DashboardStats()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        loading = true
        val companyId = auth.companyId ?: return@LaunchedEffect
        try {
            val students = auth.getUsersByCompany(UserRole.STUDENT).filter { it.isUserActive }
            val professors = auth.getUsersByCompany(UserRole.PROFESSOR).filter { it.isUserActive }
            val plans = AppContainer.planRepository.fetchPlans(companyId)
            val payments = AppContainer.paymentRepository.fetchStudentPayments(companyId)
            val cal = Calendar.getInstance()
            val monthly = payments.filter {
                val c = Calendar.getInstance().apply { time = it.paymentDate }
                c.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && c.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
            }.sumOf { it.paidAmount }
            stats = DashboardStats(students.size, professors.size, monthly, plans.size)
        } finally { loading = false }
    }

    IosScreen(title = "Dashboard", modifier = modifier) { padding ->
        BoxWithOverlay(loading) {
            Column(
                Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(company?.name ?: "Painel Admin", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("v${BuildConfig.VERSION_NAME}", color = IosColors.secondaryLabel)
                LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.height(220.dp)) {
                    item { IosStatCard("Alunos ativos", "${stats.totalStudents}", IosColors.indigo) }
                    item { IosStatCard("Professores", "${stats.totalProfessors}", IosColors.green) }
                    item { IosStatCard("Receita do mês", CurrencyUtils.formatWithSymbol(stats.monthlyRevenue, company?.language), IosColors.blue) }
                    item { IosStatCard("Planos", "${stats.totalPlans}", IosColors.orange) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    IosQuickActionRow("Registrar pagamento", Icons.Default.Payments, onRegisterPayment)
                    IosQuickActionRow("Pagamentos do mês", Icons.Default.CalendarMonth, onMonthlyPayments)
                    IosQuickActionRow("Pagamentos de professores", Icons.Default.Person, onProfessorPayments)
                    IosQuickActionRow("Agenda", Icons.Default.CalendarMonth, onSchedule)
                }
            }
        }
    }
}

@Composable
fun AdminMoreHub(onSchedule: () -> Unit, onAnamnesis: () -> Unit, onSettings: () -> Unit, onLogout: () -> Unit) {
    IosScreen(title = "Mais") { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            item {
                IosGroup {
                    IosNavigationRow("Agenda", onSchedule)
                    IosNavigationRow("Anamnese", onAnamnesis)
                    IosNavigationRow("Configurações", onSettings, showDivider = false)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var plans by remember { mutableStateOf<List<Plan>>(emptyList()) }
    var professors by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var search by remember { mutableStateOf("") }
    var showInactive by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<UserProfile?>(null) }
    var selectedStudent by remember { mutableStateOf<UserProfile?>(null) }
    val scope = rememberCoroutineScope()

    fun reload() {
        scope.launch {
            loading = true
            try {
                val companyId = auth.companyId ?: return@launch
                students = auth.getUsersByCompany(UserRole.STUDENT)
                plans = AppContainer.planRepository.fetchPlans(companyId)
                professors = auth.getUsersByCompany(UserRole.PROFESSOR)
            } finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { reload() }

    val filtered = students.filter { if (showInactive) it.isActive == false else it.isActive != false }
        .filter { search.isBlank() || it.displayName.contains(search, true) }
        .sortedBy { it.displayName.lowercase() }

    when {
        selectedStudent != null -> StudentPaymentHistoryScreen(selectedStudent!!, company?.language) { selectedStudent = null; reload() }
        showForm -> StudentFormScreen(editing, plans, professors, onClose = { showForm = false; reload() })
        else -> IosScreen(title = "Alunos", onBack = onBack, actions = {
            IconButton(onClick = { editing = null; showForm = true }) { Icon(Icons.Default.Add, contentDescription = "Novo") }
        }) { padding ->
            BoxWithOverlay(loading) {
                LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        IosGroup {
                            IosSegmented(listOf("Ativos", "Inativos"), if (showInactive) 1 else 0, { showInactive = it == 1 }, Modifier.padding(12.dp))
                        }
                    }
                    item {
                        IosGroup {
                            OutlinedTextField(search, { search = it }, label = { Text("Buscar aluno") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true)
                        }
                    }
                    item {
                        if (filtered.isEmpty() && !loading) {
                            when {
                                search.isNotBlank() -> IosEmptyState(
                                    title = "Nenhum resultado",
                                    message = "Não encontramos alunos para \"$search\".",
                                    illustrationStyle = if (showInactive) EmptyIllustrationStyle.StudentsInactive else EmptyIllustrationStyle.StudentsActive
                                )
                                showInactive -> IosEmptyState(
                                    title = "Nenhum aluno inativo",
                                    message = "Alunos desativados aparecerão nesta lista. Você pode reativá-los quando necessário.",
                                    illustrationStyle = EmptyIllustrationStyle.StudentsInactive
                                )
                                else -> IosEmptyState(
                                    title = "Nenhum aluno ativo",
                                    message = "Cadastre seu primeiro aluno para começar a gerenciar planos, pagamentos e agenda.",
                                    illustrationStyle = EmptyIllustrationStyle.StudentsActive,
                                    actionTitle = "Cadastrar aluno",
                                    onAction = { editing = null; showForm = true }
                                )
                            }
                        } else if (filtered.isNotEmpty()) IosGroup {
                            filtered.forEachIndexed { index, student ->
                                val planName = plans.find { it.id == student.planId }?.title
                                Row(Modifier.fillMaxWidth()) {
                                    Column(Modifier.weight(1f)) {
                                        IosListRow(
                                            title = student.displayName,
                                            subtitle = student.email,
                                            tertiary = planName,
                                            showDivider = index < filtered.lastIndex,
                                            onClick = { selectedStudent = student }
                                        )
                                    }
                                    if (student.isActive == false) IosBadge("Inativo", IosColors.gray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFormScreen(student: UserProfile?, plans: List<Plan>, professors: List<UserProfile>, onClose: () -> Unit) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    var password by remember { mutableStateOf("") }
    var planId by remember { mutableStateOf(student?.planId ?: "") }
    var professorId by remember { mutableStateOf(student?.professorId ?: "") }
    var loading by remember { mutableStateOf(false) }
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    val scope = rememberCoroutineScope()

    IosScreen(
        title = if (student == null) "Novo aluno" else "Editar aluno",
        onBack = onClose,
        actions = {
            TextButton(onClick = {
                scope.launch {
                    loading = true
                    try {
                        if (student != null) {
                            auth.updateUser(student.id, mapOf("name" to name, "phone" to phone, "planId" to planId, "professorId" to professorId))
                            ToastManager.success("Aluno atualizado")
                        } else {
                            auth.createUserForCompany(email, password, UserRole.STUDENT, mapOf("name" to name, "phone" to phone, "planId" to planId, "professorId" to professorId))
                            ToastManager.success("Aluno criado")
                        }
                        onClose()
                    } catch (e: Exception) { ToastManager.error(e.message ?: "Erro") }
                    finally { loading = false }
                }
            }) { Text("Salvar") }
        }
    ) { padding ->
        BoxWithOverlay(loading) {
            IosFormScroll(Modifier.padding(padding)) {
                IosSectionHeader("Dados")
                IosGroup {
                    OutlinedTextField(name, { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth().padding(12.dp))
                    if (student == null) {
                        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(12.dp))
                        OutlinedTextField(password, { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth().padding(12.dp), visualTransformation = PasswordVisualTransformation())
                    }
                    OutlinedTextField(phone, { phone = it }, label = { Text("Telefone") }, modifier = Modifier.fillMaxWidth().padding(12.dp))
                }
                IosSectionHeader("Plano")
                IosGroup {
                    IosDropdownField("Plano", listOf("Selecione") + plans.map { "${it.title} - ${CurrencyUtils.formatWithSymbol(it.price, company?.language)}" }, listOf("") + plans.map { it.id }, planId, { planId = it })
                }
                IosSectionHeader("Professor")
                IosGroup {
                    val profOptions = listOf("Selecione", "Rodízio") + professors.filter { it.isUserActive }.map { it.displayName }
                    val profValues = listOf("", RotationValue.ROTATION) + professors.filter { it.isUserActive }.map { it.id }
                    IosDropdownField("Professor", profOptions, profValues, professorId, { professorId = it })
                }
            }
        }
    }
}

@Composable
fun StudentPaymentHistoryScreen(student: UserProfile, language: String?, onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    var payments by remember { mutableStateOf<List<StudentPayment>>(emptyList()) }
    var paymentToDelete by remember { mutableStateOf<StudentPayment?>(null) }
    var loading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(student.id) {
        loading = true
        try {
            val companyId = auth.companyId ?: return@LaunchedEffect
            payments = AppContainer.paymentRepository.fetchStudentPayments(companyId, student.id)
        } finally { loading = false }
    }

    ConfirmDialog(paymentToDelete != null, "Remover pagamento", "Tem certeza que deseja remover este pagamento?", "Remover",
        onConfirm = {
            scope.launch {
                paymentToDelete?.let { p ->
                    auth.companyId?.let { AppContainer.paymentRepository.deleteStudentPayment(p.id, it) }
                    payments = payments.filter { it.id != p.id }
                    ToastManager.success("Pagamento removido")
                }
                paymentToDelete = null
            }
        }, onDismiss = { paymentToDelete = null })

    IosScreen(title = "Histórico", onBack = onBack) { padding ->
        BoxWithOverlay(loading) {
            LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                item {
                    IosGroup {
                        IosListRow(student.displayName, student.email, showDivider = false)
                    }
                }
                item {
                    Spacer(Modifier.height(8.dp))
                    if (payments.isEmpty()) IosEmptyState("Nenhum pagamento encontrado")
                    else IosGroup {
                        payments.forEachIndexed { index, payment ->
                            IosListRow(
                                CurrencyUtils.formatWithSymbol(payment.paidAmount, language),
                                DateUtils.shortDate(payment.paymentDate),
                                showDivider = index < payments.lastIndex,
                                trailing = {
                                    IconButton(onClick = { paymentToDelete = payment }) {
                                        Icon(Icons.Default.Delete, null, tint = IosColors.red)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorsScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    var professors by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var search by remember { mutableStateOf("") }
    var showInactive by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<UserProfile?>(null) }
    val scope = rememberCoroutineScope()

    fun reload() {
        scope.launch {
            loading = true
            try { professors = auth.getUsersByCompany(UserRole.PROFESSOR) }
            finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { reload() }

    val filtered = professors
        .filter { if (showInactive) it.isActive == false else it.isActive != false }
        .filter { search.isBlank() || it.displayName.contains(search, true) }
        .sortedBy { it.displayName.lowercase() }

    when {
        showForm -> ProfessorFormScreen(editing, onClose = { showForm = false; reload() })
        else -> IosScreen(title = "Professores", onBack = onBack, actions = {
            IconButton(onClick = { editing = null; showForm = true }) { Icon(Icons.Default.Add, contentDescription = "Novo") }
        }) { padding ->
            BoxWithOverlay(loading) {
                LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        IosGroup {
                            IosSegmented(listOf("Ativos", "Inativos"), if (showInactive) 1 else 0, { showInactive = it == 1 }, Modifier.padding(12.dp))
                        }
                    }
                    item {
                        IosGroup {
                            OutlinedTextField(search, { search = it }, label = { Text("Buscar professor") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true)
                        }
                    }
                    item {
                        if (filtered.isEmpty() && !loading) {
                            when {
                                search.isNotBlank() -> IosEmptyState(
                                    title = "Nenhum resultado",
                                    message = "Não encontramos professores para \"$search\".",
                                    illustrationStyle = if (showInactive) EmptyIllustrationStyle.ProfessorsInactive else EmptyIllustrationStyle.ProfessorsActive
                                )
                                showInactive -> IosEmptyState(
                                    title = "Nenhum professor inativo",
                                    message = "Professores desativados aparecerão nesta lista. Você pode reativá-los quando necessário.",
                                    illustrationStyle = EmptyIllustrationStyle.ProfessorsInactive
                                )
                                else -> IosEmptyState(
                                    title = "Nenhum professor ativo",
                                    message = "Cadastre professores para vincular alunos, agenda e comissões.",
                                    illustrationStyle = EmptyIllustrationStyle.ProfessorsActive,
                                    actionTitle = "Cadastrar professor",
                                    onAction = { editing = null; showForm = true }
                                )
                            }
                        } else if (filtered.isNotEmpty()) {
                            IosGroup {
                                filtered.forEachIndexed { i, p ->
                                    IosListRow(p.displayName, p.email, showDivider = i < filtered.lastIndex, onClick = { editing = p; showForm = true })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlansScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var plans by remember { mutableStateOf<List<Plan>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Plan?>(null) }
    val scope = rememberCoroutineScope()

    fun reload() {
        scope.launch {
            loading = true
            try { auth.companyId?.let { plans = AppContainer.planRepository.fetchPlans(it) } }
            finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { reload() }

    when {
        showForm -> PlanFormScreen(editing, onClose = { showForm = false; reload() })
        else -> IosScreen(title = "Planos", onBack = onBack, actions = {
            IconButton(onClick = { editing = null; showForm = true }) { Icon(Icons.Default.Add, contentDescription = "Novo") }
        }) { padding ->
            BoxWithOverlay(loading) {
                LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                    item {
                        if (plans.isEmpty() && !loading) {
                            IosEmptyState(
                                title = "Nenhum plano cadastrado",
                                message = "Crie planos para vincular aos alunos e registrar pagamentos com valores corretos.",
                                illustrationStyle = EmptyIllustrationStyle.Plans,
                                actionTitle = "Criar plano",
                                onAction = { editing = null; showForm = true }
                            )
                        } else if (plans.isNotEmpty()) {
                            IosGroup {
                                plans.forEachIndexed { i, plan ->
                                    IosListRow(
                                        plan.title,
                                        CurrencyUtils.formatWithSymbol(plan.price, company?.language),
                                        showDivider = i < plans.lastIndex,
                                        onClick = { editing = plan; showForm = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentRegistrationScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var plans by remember { mutableStateOf<List<Plan>>(emptyList()) }
    var payments by remember { mutableStateOf<List<StudentPayment>>(emptyList()) }
    var selectedStudentId by remember { mutableStateOf("") }
    var periodMonths by remember { mutableIntStateOf(1) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val companyId = auth.companyId ?: return@LaunchedEffect
        students = auth.getUsersByCompany(UserRole.STUDENT)
        plans = AppContainer.planRepository.fetchPlans(companyId)
        payments = AppContainer.paymentRepository.fetchStudentPayments(companyId)
    }

    val cal = Calendar.getInstance()
    val paidIds = payments.filter {
        val c = Calendar.getInstance().apply { time = it.paymentDate }
        c.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && c.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
    }.map { it.studentId }.toSet()
    val available = students.filter { it.isUserActive && it.id !in paidIds }
    val student = students.find { it.id == selectedStudentId }
    val plan = plans.find { it.id == student?.planId }
    val original = (plan?.price ?: 0.0) * periodMonths
    val discount = when { periodMonths >= 12 -> 10.0; periodMonths >= 6 -> 5.0; periodMonths >= 3 -> 3.0; else -> 0.0 }
    val finalAmount = original - original * discount / 100
    val commission = finalAmount * 0.4

    IosScreen(title = "Registrar pagamento", onBack = onBack) { padding ->
        BoxWithOverlay(loading) {
            IosFormScroll(Modifier.padding(padding)) {
                IosSectionHeader("Aluno")
                IosGroup {
                    IosDropdownField("Aluno", listOf("Selecione") + available.map { it.displayName }, listOf("") + available.map { it.id }, selectedStudentId, { selectedStudentId = it })
                }
                if (selectedStudentId.isBlank()) {
                    IosEmptyState(
                        title = "Selecione um aluno",
                        message = "Escolha um aluno acima para registrar o pagamento do plano.",
                        illustrationStyle = EmptyIllustrationStyle.PaymentSelectStudent
                    )
                } else if (plan != null && student != null) {
                    IosSectionHeader("Detalhes")
                    IosGroup {
                        IosLabeledRow("Plano", plan.title)
                        IosLabeledRow("Professor", student.professorId ?: "-", showDivider = false)
                    }
                    IosSectionHeader("Pagamento")
                    IosGroup {
                        IosDropdownField("Período", listOf("1 mês", "3 meses", "6 meses", "12 meses"), listOf(1, 3, 6, 12).map { it.toString() }, periodMonths.toString(), { periodMonths = it.toIntOrNull() ?: 1 })
                        IosLabeledRow("Valor final", CurrencyUtils.formatWithSymbol(finalAmount, company?.language))
                        IosLabeledRow("Comissão", CurrencyUtils.formatWithSymbol(commission, company?.language), showDivider = false)
                        Button(onClick = {
                            scope.launch {
                                loading = true
                                try {
                                    val companyId = auth.companyId!!
                                    val professorId = student.professorId!!
                                    val paymentId = AppContainer.paymentRepository.addStudentPayment(mapOf(
                                        "studentId" to student.id, "professorId" to professorId, "planId" to plan.id,
                                        "companyId" to companyId, "amount" to finalAmount, "finalAmount" to finalAmount,
                                        "originalAmount" to original, "discountPercent" to discount,
                                        "commissionPercent" to 40.0, "commissionAmount" to commission,
                                        "paymentDate" to Date(), "periodMonths" to periodMonths,
                                        "paymentPeriod" to DateUtils.monthNames[cal.get(Calendar.MONTH)],
                                        "createdBy" to auth.userId
                                    ))
                                    if (professorId != RotationValue.ROTATION && commission > 0) {
                                        AppContainer.paymentRepository.addProfessorPayment(mapOf(
                                            "professorId" to professorId, "studentId" to student.id, "studentPaymentId" to paymentId,
                                            "planId" to plan.id, "companyId" to companyId, "amount" to commission,
                                            "commissionPercent" to 40.0, "originalStudentPayment" to finalAmount,
                                            "paymentDate" to Date(), "notes" to "Comissão do aluno ${student.displayName}"
                                        ))
                                    }
                                    ToastManager.success("Pagamento registrado")
                                    onBack()
                                } catch (e: Exception) { ToastManager.error(e.message ?: "Erro") }
                                finally { loading = false }
                            }
                        }, modifier = Modifier.fillMaxWidth().padding(12.dp)) { Text("Registrar") }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyPaymentsScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var payments by remember { mutableStateOf<List<StudentPayment>>(emptyList()) }
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var paymentToDelete by remember { mutableStateOf<StudentPayment?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val companyId = auth.companyId ?: return@LaunchedEffect
        payments = AppContainer.paymentRepository.fetchStudentPayments(companyId)
        students = auth.getUsersByCompany(UserRole.STUDENT)
    }

    val cal = Calendar.getInstance()
    val monthPayments = payments.filter {
        val c = Calendar.getInstance().apply { time = it.paymentDate }
        c.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && c.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
    }

    ConfirmDialog(paymentToDelete != null, "Remover pagamento", "Tem certeza?", "Remover", onConfirm = {
        scope.launch {
            paymentToDelete?.let { p ->
                auth.companyId?.let { AppContainer.paymentRepository.deleteStudentPayment(p.id, it) }
                payments = payments.filter { it.id != p.id }
                ToastManager.success("Pagamento removido")
            }
            paymentToDelete = null
        }
    }, onDismiss = { paymentToDelete = null })

    IosScreen(title = "Pagamentos do mês", onBack = onBack) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
            item {
                if (monthPayments.isEmpty()) IosEmptyState("Nenhum pagamento neste mês")
                else IosGroup {
                    monthPayments.forEachIndexed { i, payment ->
                        val name = students.find { it.id == payment.studentId }?.displayName ?: "Aluno"
                        IosListRow(name, CurrencyUtils.formatWithSymbol(payment.paidAmount, company?.language), showDivider = i < monthPayments.lastIndex, trailing = {
                            IconButton(onClick = { paymentToDelete = payment }) { Icon(Icons.Default.Delete, null, tint = IosColors.red) }
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessorPaymentsScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var professors by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var payments by remember { mutableStateOf<List<ProfessorPayment>>(emptyList()) }
    var selectedProfessorId by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val companyId = auth.companyId ?: return@LaunchedEffect
        professors = auth.getUsersByCompany(UserRole.PROFESSOR)
        students = auth.getUsersByCompany(UserRole.STUDENT)
        payments = AppContainer.paymentRepository.fetchProfessorPayments(companyId)
    }

    val cal = Calendar.getInstance()
    val monthPayments = payments.filter {
        it.professorId == selectedProfessorId && Calendar.getInstance().apply { time = it.paymentDate }.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
    }.map { p -> p.copy(studentName = students.find { it.id == p.studentId }?.displayName) }

    IosScreen(title = "Pagamentos de professores", onBack = onBack) { padding ->
        IosFormScroll(Modifier.padding(padding)) {
            IosGroup {
                IosDropdownField("Professor", listOf("Selecione") + professors.filter { it.isUserActive }.map { it.displayName }, listOf("") + professors.filter { it.isUserActive }.map { it.id }, selectedProfessorId, { selectedProfessorId = it })
            }
            if (selectedProfessorId.isBlank()) {
                IosEmptyState(
                    title = "Selecione um professor",
                    message = "Escolha um professor acima para visualizar as comissões do mês.",
                    illustrationStyle = EmptyIllustrationStyle.ScheduleSelectProfessor
                )
            } else if (monthPayments.isEmpty()) {
                IosEmptyState(
                    title = "Nenhuma comissão neste mês",
                    message = "Não há comissões registradas para este professor no mês atual.",
                    illustrationStyle = EmptyIllustrationStyle.PaymentVisualization
                )
            } else IosGroup {
                monthPayments.forEachIndexed { i, payment ->
                    IosListRow(payment.studentName ?: "Aluno", CurrencyUtils.formatWithSymbol(payment.amount, company?.language), showDivider = i < monthPayments.lastIndex)
                }
            }
        }
    }
}

@Composable
fun AdminScheduleScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    var professors by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var selectedProfessorId by remember { mutableStateOf("") }
    var classes by remember { mutableStateOf(emptyList<com.saudepilates.app.data.models.ScheduledClass>()) }
    var loading by remember { mutableStateOf(false) }
    val selectedDate = remember { Date() }
    val formattedDate = remember(selectedDate) { DateUtils.shortDate(selectedDate) }
    val selectedProfessorName = professors.find { it.id == selectedProfessorId }?.displayName ?: "Professor"

    LaunchedEffect(selectedProfessorId, students) {
        if (selectedProfessorId.isBlank()) {
            classes = emptyList()
            return@LaunchedEffect
        }
        loading = true
        try {
            val start = DateUtils.startOfDay(selectedDate)
            val end = Calendar.getInstance().apply { time = start; add(Calendar.DAY_OF_YEAR, 1) }.time
            classes = AppContainer.scheduleRepository.fetchProfessorSchedule(selectedProfessorId, start, end)
                .map { c -> c.copy(studentName = students.find { it.id == c.studentId }?.displayName) }
        } finally {
            loading = false
        }
    }
    LaunchedEffect(Unit) {
        professors = auth.getUsersByCompany(UserRole.PROFESSOR).filter { it.isUserActive }
        students = auth.getUsersByCompany(UserRole.STUDENT)
    }

    IosScreen(title = "Agenda", onBack = onBack) { padding ->
        BoxWithOverlay(loading) {
            LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    IosGroup {
                        IosDropdownField(
                            "Professor",
                            listOf("Selecione") + professors.map { it.displayName },
                            listOf("") + professors.map { it.id },
                            selectedProfessorId,
                            { selectedProfessorId = it }
                        )
                    }
                }
                item {
                    when {
                        selectedProfessorId.isBlank() && !loading -> IosEmptyState(
                            title = "Selecione um professor",
                            message = "Escolha um professor acima para visualizar a agenda e as aulas do dia.",
                            illustrationStyle = EmptyIllustrationStyle.ScheduleSelectProfessor
                        )
                        classes.isEmpty() && !loading -> IosEmptyState(
                            title = "Nenhuma aula neste dia",
                            message = "$selectedProfessorName não possui alunos agendados para $formattedDate.",
                            illustrationStyle = EmptyIllustrationStyle.ScheduleNoClasses
                        )
                        classes.isNotEmpty() -> IosGroup {
                            classes.forEachIndexed { i, item ->
                                IosListRow(item.studentName ?: "Aluno", item.startTime ?: "", showDivider = i < classes.lastIndex)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnamnesisScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var selectedStudentId by remember { mutableStateOf("") }
    var records by remember { mutableStateOf<List<AnamnesisRecord>>(emptyList()) }
    var editingId by remember { mutableStateOf<String?>(null) }
    var mainComplaint by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    var recordToDelete by remember { mutableStateOf<AnamnesisRecord?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun clearForm() {
        editingId = null
        mainComplaint = ""
        observations = ""
    }

    fun loadRecord(record: AnamnesisRecord) {
        editingId = record.id
        mainComplaint = record.mainComplaint ?: ""
        observations = record.observations ?: ""
    }

    LaunchedEffect(Unit) { students = auth.getUsersByCompany(UserRole.STUDENT) }

    LaunchedEffect(selectedStudentId) {
        if (selectedStudentId.isBlank()) {
            records = emptyList()
            clearForm()
            return@LaunchedEffect
        }
        loading = true
        try {
            records = AppContainer.anamnesisRepository.fetchByStudent(selectedStudentId)
        } finally {
            loading = false
        }
    }

    ConfirmDialog(
        visible = recordToDelete != null,
        title = "Excluir anamnese",
        message = "Tem certeza que deseja excluir esta anamnese? Esta ação não pode ser desfeita.",
        confirmText = "Excluir",
        onConfirm = {
            scope.launch {
                recordToDelete?.let { record ->
                    AppContainer.anamnesisRepository.delete(record.id)
                    records = records.filter { it.id != record.id }
                    if (editingId == record.id) clearForm()
                    ToastManager.success("Anamnese excluída")
                }
                recordToDelete = null
            }
        },
        onDismiss = { recordToDelete = null }
    )

    IosScreen(title = "Anamnese", onBack = onBack) { padding ->
        BoxWithOverlay(loading) {
            IosFormScroll(Modifier.padding(padding)) {
                IosGroup {
                    IosDropdownField(
                        "Aluno",
                        listOf("Selecione") + students.filter { it.isUserActive }.map { it.displayName },
                        listOf("") + students.filter { it.isUserActive }.map { it.id },
                        selectedStudentId,
                        {
                            selectedStudentId = it
                            clearForm()
                        }
                    )
                }

                if (selectedStudentId.isNotBlank()) {
                    IosSectionHeader("Histórico")
                    if (records.isEmpty()) {
                        IosEmptyState("Nenhuma anamnese cadastrada")
                    } else {
                        IosGroup {
                            records.forEachIndexed { index, record ->
                                IosListRow(
                                    DateUtils.toDate(record.performedAt)?.let { DateUtils.shortDate(it) }
                                        ?: DateUtils.toDate("${record.performedAt?.take(10)}T00:00:00")?.let { DateUtils.shortDate(it) }
                                        ?: "—",
                                    record.mainComplaint?.takeIf { it.isNotBlank() } ?: "Anamnese",
                                    showDivider = index < records.lastIndex,
                                    onClick = { loadRecord(record) },
                                    trailing = {
                                        IconButton(onClick = { recordToDelete = record }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = IosColors.red)
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = { clearForm() }) { Text("Nova anamnese") }
                    }

                    IosGroup {
                        OutlinedTextField(
                            mainComplaint,
                            { mainComplaint = it },
                            label = { Text("Queixa principal") },
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            minLines = 2
                        )
                        OutlinedTextField(
                            observations,
                            { observations = it },
                            label = { Text("Observações") },
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            minLines = 3
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val companyId = auth.companyId ?: return@launch
                                    if (selectedStudentId.isBlank()) return@launch
                                    val savedId = AppContainer.anamnesisRepository.save(
                                        selectedStudentId,
                                        companyId,
                                        mapOf("mainComplaint" to mainComplaint, "observations" to observations),
                                        editingId
                                    )
                                    editingId = savedId
                                    records = AppContainer.anamnesisRepository.fetchByStudent(selectedStudentId)
                                    ToastManager.success("Anamnese salva")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(if (editingId == null) "Salvar anamnese" else "Atualizar anamnese") }

                        if (editingId != null) {
                            Button(
                                onClick = {
                                    records.find { it.id == editingId }?.let { recordToDelete = it }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = IosColors.red
                                )
                            ) { Text("Excluir anamnese") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var name by remember { mutableStateOf(company?.name ?: "") }
    val scope = rememberCoroutineScope()
    LaunchedEffect(company?.name) { name = company?.name ?: "" }

    IosScreen(title = "Configurações", onBack = onBack) { padding ->
        IosFormScroll(Modifier.padding(padding)) {
            IosGroup {
                OutlinedTextField(name, { name = it }, label = { Text("Nome da empresa") }, modifier = Modifier.fillMaxWidth().padding(12.dp))
            }
            Button(onClick = {
                scope.launch {
                    auth.companyId?.let { auth.updateCompany(it, name, company?.language ?: "pt") }
                    ToastManager.success("Configurações salvas")
                }
            }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) { Text("Salvar") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanFormScreen(plan: Plan?, onClose: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var title by remember { mutableStateOf(plan?.title ?: "") }
    var price by remember { mutableStateOf(plan?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(plan?.description ?: "") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    IosScreen(
        title = if (plan == null) "Novo plano" else "Editar plano",
        onBack = onClose,
        actions = {
            TextButton(onClick = {
                scope.launch {
                    val companyId = auth.companyId ?: return@launch
                    val priceValue = price.replace(",", ".").toDoubleOrNull() ?: return@launch
                    loading = true
                    try {
                        AppContainer.planRepository.savePlan(
                            Plan(id = plan?.id ?: "", title = title, price = priceValue, companyId = companyId, description = description),
                            companyId
                        )
                        ToastManager.success("Plano salvo")
                        onClose()
                    } catch (e: Exception) { ToastManager.error(e.message ?: "Erro") }
                    finally { loading = false }
                }
            }) { Text("Salvar") }
        }
    ) { padding ->
        BoxWithOverlay(loading) {
            IosFormScroll(Modifier.padding(padding)) {
                IosGroup {
                    OutlinedTextField(title, { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth().padding(12.dp))
                    OutlinedTextField(price, { price = it }, label = { Text("Preço") }, modifier = Modifier.fillMaxWidth().padding(12.dp), singleLine = true)
                    OutlinedTextField(description, { description = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth().padding(12.dp), minLines = 2)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorFormScreen(professor: UserProfile?, onClose: () -> Unit) {
    val auth = AppContainer.authRepository
    var name by remember { mutableStateOf(professor?.name ?: "") }
    var email by remember { mutableStateOf(professor?.email ?: "") }
    var phone by remember { mutableStateOf(professor?.phone ?: "") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    IosScreen(
        title = if (professor == null) "Novo professor" else "Editar professor",
        onBack = onClose,
        actions = {
            TextButton(onClick = {
                scope.launch {
                    loading = true
                    try {
                        if (professor != null) {
                            auth.updateUser(professor.id, mapOf("name" to name, "phone" to phone))
                            ToastManager.success("Professor atualizado")
                        } else {
                            auth.createUserForCompany(email, password, UserRole.PROFESSOR, mapOf("name" to name, "phone" to phone))
                            ToastManager.success("Professor criado")
                        }
                        onClose()
                    } catch (e: Exception) { ToastManager.error(e.message ?: "Erro") }
                    finally { loading = false }
                }
            }) { Text("Salvar") }
        }
    ) { padding ->
        BoxWithOverlay(loading) {
            IosFormScroll(Modifier.padding(padding)) {
                IosGroup {
                    OutlinedTextField(name, { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth().padding(12.dp))
                    if (professor == null) {
                        OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth().padding(12.dp))
                        OutlinedTextField(password, { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth().padding(12.dp), visualTransformation = PasswordVisualTransformation())
                    }
                    OutlinedTextField(phone, { phone = it }, label = { Text("Telefone") }, modifier = Modifier.fillMaxWidth().padding(12.dp))
                }
            }
        }
    }
}

private enum class PeopleOnboardingStep { NeedsPlan, NeedsProfessor, NeedsStudent }

@Composable
fun AdminPeopleHubScreen(
    modifier: Modifier = Modifier,
    onStudents: () -> Unit,
    onProfessors: () -> Unit,
    onPlans: () -> Unit
) {
    val auth = AppContainer.authRepository
    var plans by remember { mutableStateOf<List<Plan>>(emptyList()) }
    var professors by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var students by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showPlanForm by remember { mutableStateOf(false) }
    var showProfessorForm by remember { mutableStateOf(false) }
    var showStudentForm by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun reload() {
        scope.launch {
            loading = true
            try {
                val companyId = auth.companyId ?: return@launch
                plans = AppContainer.planRepository.fetchPlans(companyId)
                professors = auth.getUsersByCompany(UserRole.PROFESSOR)
                students = auth.getUsersByCompany(UserRole.STUDENT)
            } finally { loading = false }
        }
    }
    LaunchedEffect(Unit) { reload() }

    val onboardingStep = when {
        plans.isEmpty() -> PeopleOnboardingStep.NeedsPlan
        professors.none { it.isUserActive } -> PeopleOnboardingStep.NeedsProfessor
        students.none { it.isUserActive } -> PeopleOnboardingStep.NeedsStudent
        else -> null
    }

    when {
        showPlanForm -> PlanFormScreen(null) { showPlanForm = false; reload() }
        showProfessorForm -> ProfessorFormScreen(null) { showProfessorForm = false; reload() }
        showStudentForm -> StudentFormScreen(null, plans, professors) { showStudentForm = false; reload() }
        else -> IosScreen(title = "Pessoas", modifier = modifier) { padding ->
            BoxWithOverlay(loading) {
                LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                    item {
                        if (onboardingStep != null && !loading) {
                            when (onboardingStep) {
                                PeopleOnboardingStep.NeedsPlan -> IosEmptyState(
                                    title = "Crie seu primeiro plano",
                                    message = "Antes de cadastrar professores e alunos, defina um plano com o valor das aulas do seu estúdio.",
                                    illustrationStyle = EmptyIllustrationStyle.Plans,
                                    actionTitle = "Criar plano",
                                    onAction = { showPlanForm = true }
                                )
                                PeopleOnboardingStep.NeedsProfessor -> IosEmptyState(
                                    title = "Cadastre um professor",
                                    message = "Com o plano criado, adicione um professor para vincular alunos, agenda e comissões.",
                                    illustrationStyle = EmptyIllustrationStyle.ProfessorsActive,
                                    actionTitle = "Cadastrar professor",
                                    onAction = { showProfessorForm = true }
                                )
                                PeopleOnboardingStep.NeedsStudent -> IosEmptyState(
                                    title = "Cadastre seu primeiro aluno",
                                    message = "Tudo pronto para começar. Adicione um aluno para gerenciar planos, pagamentos e agenda.",
                                    illustrationStyle = EmptyIllustrationStyle.StudentsActive,
                                    actionTitle = "Cadastrar aluno",
                                    onAction = { showStudentForm = true }
                                )
                            }
                        } else if (!loading) {
                            IosGroup {
                                IosNavigationRow("Alunos", onStudents, showDivider = true)
                                IosNavigationRow("Professores", onProfessors, showDivider = true)
                                IosNavigationRow("Planos", onPlans, showDivider = false)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentVisualizationScreen(onBack: () -> Unit, onRegisterPayment: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var payments by remember { mutableStateOf<List<StudentPayment>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        loading = true
        try {
            auth.companyId?.let { payments = AppContainer.paymentRepository.fetchStudentPayments(it) }
        } finally { loading = false }
    }

    val chartData = remember(payments) {
        val cal = Calendar.getInstance()
        val formatter = SimpleDateFormat("MMM/yy", Locale("pt", "BR"))
        val totals = linkedMapOf<String, Triple<String, Double, Int>>()
        payments.forEach { payment ->
            cal.time = payment.paymentDate
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH) + 1
            val key = "%04d-%02d".format(year, month)
            val sortKey = year * 100 + month
            val label = formatter.format(payment.paymentDate)
            val existing = totals[key]
            totals[key] = Triple(label, (existing?.second ?: 0.0) + payment.paidAmount, sortKey)
        }
        totals.entries.sortedBy { it.value.third }.map { it.value.first to it.value.second }
    }
    val totalRevenue = payments.sumOf { it.paidAmount }

    IosScreen(title = "Visualização", onBack = onBack) { padding ->
        BoxWithOverlay(loading) {
            if (!loading && payments.isEmpty()) {
                IosEmptyState(
                    title = "Nenhum pagamento registrado",
                    message = "Registre pagamentos de alunos para visualizar aqui a evolução do valor recebido mês a mês no seu estúdio.",
                    illustrationStyle = EmptyIllustrationStyle.PaymentVisualization,
                    actionTitle = "Registrar pagamento",
                    onAction = onRegisterPayment,
                    modifier = Modifier.padding(padding).fillMaxSize()
                )
            } else {
                IosFormScroll(Modifier.padding(padding)) {
                    if (chartData.isNotEmpty()) {
                        val maxTotal = chartData.maxOf { it.second }.coerceAtLeast(1.0)
                        IosSectionHeader("Receita por mês")
                        IosGroup {
                            chartData.forEachIndexed { index, (month, total) ->
                                Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(month, fontWeight = FontWeight.Medium)
                                        Text(CurrencyUtils.formatWithSymbol(total, company?.language))
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Box(
                                        Modifier
                                            .fillMaxWidth((total / maxTotal).toFloat().coerceIn(0.05f, 1f))
                                            .height(12.dp)
                                            .background(IosColors.indigo, RoundedCornerShape(6.dp))
                                    )
                                }
                                if (index < chartData.lastIndex) {
                                    androidx.compose.material3.HorizontalDivider(color = IosColors.separator.copy(alpha = 0.4f))
                                }
                            }
                        }
                    }
                    IosSectionHeader("Resumo")
                    IosGroup {
                        IosLabeledRow("Total geral", CurrencyUtils.formatWithSymbol(totalRevenue, company?.language), showDivider = false)
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxWithOverlay(loading: Boolean, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box {
        content()
        IosLoadingOverlay(loading)
    }
}

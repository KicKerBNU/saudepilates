package com.saudepilates.app.ui.admin

import androidx.compose.foundation.layout.Arrangement
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
import com.saudepilates.app.AppContainer
import com.saudepilates.app.BuildConfig
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
import com.saudepilates.app.ui.components.IosEmptyState
import com.saudepilates.app.ui.components.IosFormScroll
import com.saudepilates.app.ui.components.IosGroup
import com.saudepilates.app.ui.components.IosHubList
import com.saudepilates.app.ui.components.IosLabeledRow
import com.saudepilates.app.ui.components.IosListRow
import com.saudepilates.app.ui.components.IosLoadingOverlay
import com.saudepilates.app.ui.components.IosNavigationRow
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
                        if (filtered.isEmpty()) IosEmptyState(if (showInactive) "Nenhum aluno inativo" else "Nenhum aluno ativo")
                        else IosGroup {
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

@Composable
fun ProfessorsScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    var professors by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) { loading = true; professors = auth.getUsersByCompany(UserRole.PROFESSOR); loading = false }

    IosScreen(title = "Professores", onBack = onBack) { padding ->
        BoxWithOverlay(loading) {
            LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
                item {
                    IosGroup {
                        professors.filter { it.isUserActive }.forEachIndexed { i, p ->
                            IosListRow(p.displayName, p.email, showDivider = i < professors.lastIndex)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlansScreen(onBack: () -> Unit) {
    val auth = AppContainer.authRepository
    val company by auth.company.collectAsState()
    var plans by remember { mutableStateOf<List<Plan>>(emptyList()) }
    LaunchedEffect(Unit) { auth.companyId?.let { plans = AppContainer.planRepository.fetchPlans(it) } }

    IosScreen(title = "Planos", onBack = onBack) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp)) {
            item {
                IosGroup {
                    plans.forEachIndexed { i, plan ->
                        IosListRow(plan.title, CurrencyUtils.formatWithSymbol(plan.price, company?.language), showDivider = i < plans.lastIndex)
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
                if (plan != null && student != null) {
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
            if (monthPayments.isEmpty()) IosEmptyState("Nenhuma comissão neste mês")
            else IosGroup {
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

    LaunchedEffect(selectedProfessorId) {
        if (selectedProfessorId.isBlank()) return@LaunchedEffect
        val start = Date()
        val end = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time
        classes = AppContainer.scheduleRepository.fetchProfessorSchedule(selectedProfessorId, start, end)
            .map { c -> c.copy(studentName = students.find { it.id == c.studentId }?.displayName) }
    }
    LaunchedEffect(Unit) {
        professors = auth.getUsersByCompany(UserRole.PROFESSOR)
        students = auth.getUsersByCompany(UserRole.STUDENT)
        selectedProfessorId = professors.firstOrNull()?.id ?: ""
    }

    IosScreen(title = "Agenda", onBack = onBack) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                IosGroup {
                    IosDropdownField("Professor", professors.map { it.displayName }, professors.map { it.id }, selectedProfessorId, { selectedProfessorId = it })
                }
            }
            item {
                if (classes.isEmpty()) IosEmptyState("Nenhuma aula agendada")
                else IosGroup {
                    classes.forEachIndexed { i, item ->
                        IosListRow(item.studentName ?: "Aluno", item.startTime ?: "", showDivider = i < classes.lastIndex)
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
    var mainComplaint by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { students = auth.getUsersByCompany(UserRole.STUDENT) }

    IosScreen(title = "Anamnese", onBack = onBack) { padding ->
        IosFormScroll(Modifier.padding(padding)) {
            IosGroup {
                IosDropdownField("Aluno", listOf("Selecione") + students.filter { it.isUserActive }.map { it.displayName }, listOf("") + students.filter { it.isUserActive }.map { it.id }, selectedStudentId, { selectedStudentId = it })
            }
            IosGroup {
                OutlinedTextField(mainComplaint, { mainComplaint = it }, label = { Text("Queixa principal") }, modifier = Modifier.fillMaxWidth().padding(12.dp), minLines = 2)
                OutlinedTextField(observations, { observations = it }, label = { Text("Observações") }, modifier = Modifier.fillMaxWidth().padding(12.dp), minLines = 3)
            }
            Button(onClick = {
                scope.launch {
                    val companyId = auth.companyId ?: return@launch
                    AppContainer.anamnesisRepository.save(selectedStudentId, companyId, mapOf("mainComplaint" to mainComplaint, "observations" to observations), null)
                    ToastManager.success("Anamnese salva")
                }
            }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) { Text("Salvar anamnese") }
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

@Composable
private fun BoxWithOverlay(loading: Boolean, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Box {
        content()
        IosLoadingOverlay(loading)
    }
}

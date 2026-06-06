package com.saudepilates.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.saudepilates.app.AppContainer
import com.saudepilates.app.data.models.SubscriptionInfo
import com.saudepilates.app.data.models.UserRole
import com.saudepilates.app.ui.admin.AdminDashboardScreen
import com.saudepilates.app.ui.admin.AdminScheduleScreen
import com.saudepilates.app.ui.admin.AnamnesisScreen
import com.saudepilates.app.ui.admin.MonthlyPaymentsScreen
import com.saudepilates.app.ui.admin.AdminPeopleHubScreen
import com.saudepilates.app.ui.admin.PaymentRegistrationScreen
import com.saudepilates.app.ui.admin.PaymentVisualizationScreen
import com.saudepilates.app.ui.admin.PlansScreen
import com.saudepilates.app.ui.admin.ProfessorPaymentsScreen
import com.saudepilates.app.ui.admin.ProfessorsScreen
import com.saudepilates.app.ui.admin.AdminMoreHub
import com.saudepilates.app.ui.admin.SettingsScreen
import com.saudepilates.app.ui.admin.StudentsScreen
import com.saudepilates.app.ui.admin.SubscriptionGateScreen
import com.saudepilates.app.ui.components.IosHubList
import com.saudepilates.app.ui.professor.ProfessorAttendanceScreen
import com.saudepilates.app.ui.professor.ProfessorDashboardScreen
import com.saudepilates.app.ui.professor.ProfessorEarningsScreen
import com.saudepilates.app.ui.professor.ProfessorEvolutionScreen
import com.saudepilates.app.ui.professor.ProfessorMessagesScreen
import com.saudepilates.app.ui.professor.ProfessorMoreAnamnesisScreen
import com.saudepilates.app.ui.professor.ProfessorMoreHub
import com.saudepilates.app.ui.professor.ProfessorScheduleScreen
import com.saudepilates.app.ui.professor.ProfessorStudentsScreen
import com.saudepilates.app.ui.student.StudentDashboardScreen
import com.saudepilates.app.ui.student.StudentPaymentsScreen
import com.saudepilates.app.ui.student.StudentScheduleScreen
import com.saudepilates.app.ui.theme.IosColors

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val auth = AppContainer.authRepository
    val profile by auth.userProfile.collectAsState()
    var subscription by remember { mutableStateOf<SubscriptionInfo?>(null) }
    var checkingSubscription by remember { mutableStateOf(false) }

    LaunchedEffect(profile?.role, auth.companyId) {
        if (profile?.role != UserRole.ADMIN) return@LaunchedEffect
        val companyId = auth.companyId ?: return@LaunchedEffect
        checkingSubscription = true
        try { subscription = AppContainer.subscriptionRepository.fetchSubscription(companyId) }
        finally { checkingSubscription = false }
    }

    if (profile?.role == UserRole.ADMIN && subscription != null && !subscription!!.isValid) {
        SubscriptionGateScreen(subscription!!, modifier)
        return
    }
    if (profile?.role == UserRole.ADMIN && checkingSubscription) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = IosColors.blue) }
        return
    }

    when (profile?.role) {
        UserRole.ADMIN -> AdminRoot(modifier)
        UserRole.PROFESSOR -> ProfessorRoot(modifier)
        UserRole.STUDENT -> StudentRoot(modifier)
        null -> Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = IosColors.blue) }
    }
}

@Composable
private fun AdminRoot(modifier: Modifier = Modifier) {
    var tab by remember { mutableIntStateOf(0) }
    Scaffold(
        modifier = modifier,
        containerColor = IosColors.groupedBackground,
        bottomBar = {
            NavigationBar(containerColor = IosColors.secondaryGroupedBackground) {
                NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Início") })
                NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.People, null) }, label = { Text("Pessoas") })
                NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.CreditCard, null) }, label = { Text("Pagamentos") })
                NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.MoreHoriz, null) }, label = { Text("Mais") })
            }
        }
    ) { padding ->
        when (tab) {
            0 -> AdminHomeNav(Modifier.padding(padding))
            1 -> AdminPeopleNav(Modifier.padding(padding))
            2 -> AdminPaymentsNav(Modifier.padding(padding))
            3 -> AdminMoreNav(Modifier.padding(padding))
        }
    }
}

@Composable
private fun AdminHomeNav(modifier: Modifier) {
    val nav = rememberNavController()
    NavHost(nav, "dashboard", modifier) {
        composable("dashboard") {
            AdminDashboardScreen(
                onRegisterPayment = { nav.navigate("register") },
                onMonthlyPayments = { nav.navigate("monthly") },
                onProfessorPayments = { nav.navigate("professor_payments") },
                onSchedule = { nav.navigate("schedule") }
            )
        }
        composable("register") { PaymentRegistrationScreen { nav.popBackStack() } }
        composable("monthly") { MonthlyPaymentsScreen { nav.popBackStack() } }
        composable("professor_payments") { ProfessorPaymentsScreen { nav.popBackStack() } }
        composable("schedule") { AdminScheduleScreen { nav.popBackStack() } }
    }
}

@Composable
private fun AdminPeopleNav(modifier: Modifier) {
    val nav = rememberNavController()
    NavHost(nav, "hub", modifier) {
        composable("hub") {
            AdminPeopleHubScreen(
                onStudents = { nav.navigate("students") },
                onProfessors = { nav.navigate("professors") },
                onPlans = { nav.navigate("plans") }
            )
        }
        composable("students") { StudentsScreen { nav.popBackStack() } }
        composable("professors") { ProfessorsScreen { nav.popBackStack() } }
        composable("plans") { PlansScreen { nav.popBackStack() } }
    }
}

@Composable
private fun AdminPaymentsNav(modifier: Modifier) {
    val nav = rememberNavController()
    NavHost(nav, "hub", modifier) {
        composable("hub") {
            IosHubList("Pagamentos", listOf(
                "Registrar pagamento" to { nav.navigate("register") },
                "Pagamentos do mês" to { nav.navigate("monthly") },
                "Visualização" to { nav.navigate("visualization") },
                "Pagamentos de professores" to { nav.navigate("professor_payments") }
            ))
        }
        composable("register") { PaymentRegistrationScreen { nav.popBackStack() } }
        composable("monthly") { MonthlyPaymentsScreen { nav.popBackStack() } }
        composable("visualization") {
            PaymentVisualizationScreen(
                onBack = { nav.popBackStack() },
                onRegisterPayment = { nav.navigate("register") }
            )
        }
        composable("professor_payments") { ProfessorPaymentsScreen { nav.popBackStack() } }
    }
}

@Composable
private fun AdminMoreNav(modifier: Modifier) {
    val nav = rememberNavController()
    val auth = AppContainer.authRepository
    NavHost(nav, "hub", modifier) {
        composable("hub") {
            AdminMoreHub(
                onSchedule = { nav.navigate("schedule") },
                onAnamnesis = { nav.navigate("anamnesis") },
                onSettings = { nav.navigate("settings") },
                onLogout = { auth.logout() }
            )
        }
        composable("schedule") { AdminScheduleScreen { nav.popBackStack() } }
        composable("anamnesis") { AnamnesisScreen { nav.popBackStack() } }
        composable("settings") { SettingsScreen { nav.popBackStack() } }
    }
}

@Composable
private fun ProfessorRoot(modifier: Modifier = Modifier) {
    var tab by remember { mutableIntStateOf(0) }
    Scaffold(modifier, containerColor = IosColors.groupedBackground, bottomBar = {
        NavigationBar(containerColor = IosColors.secondaryGroupedBackground) {
            NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Início") })
            NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.People, null) }, label = { Text("Alunos") })
            NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.CalendarMonth, null) }, label = { Text("Agenda") })
            NavigationBarItem(selected = tab == 3, onClick = { tab = 3 }, icon = { Icon(Icons.Default.MoreHoriz, null) }, label = { Text("Mais") })
        }
    }) { padding ->
        when (tab) {
            0 -> ProfessorHomeNav(Modifier.padding(padding))
            1 -> ProfessorStudentsNav(Modifier.padding(padding))
            2 -> ProfessorScheduleNav(Modifier.padding(padding))
            3 -> ProfessorMoreNav(Modifier.padding(padding))
        }
    }
}

@Composable
private fun ProfessorHomeNav(modifier: Modifier) {
    val nav = rememberNavController()
    NavHost(nav, "dashboard", modifier) {
        composable("dashboard") {
            ProfessorDashboardScreen(
                onAttendance = { nav.navigate("attendance") },
                onEvolution = { nav.navigate("evolution") },
                onEarnings = { nav.navigate("earnings") },
                onMessages = { nav.navigate("messages") },
                onAnamnesis = { nav.navigate("anamnesis") }
            )
        }
        composable("attendance") { ProfessorAttendanceScreen { nav.popBackStack() } }
        composable("evolution") { ProfessorEvolutionScreen { nav.popBackStack() } }
        composable("earnings") { ProfessorEarningsScreen { nav.popBackStack() } }
        composable("messages") { ProfessorMessagesScreen { nav.popBackStack() } }
        composable("anamnesis") { ProfessorMoreAnamnesisScreen { nav.popBackStack() } }
    }
}

@Composable
private fun ProfessorStudentsNav(modifier: Modifier) {
    val nav = rememberNavController()
    NavHost(nav, "students", modifier) { composable("students") { ProfessorStudentsScreen() } }
}

@Composable
private fun ProfessorScheduleNav(modifier: Modifier) {
    val nav = rememberNavController()
    NavHost(nav, "schedule", modifier) { composable("schedule") { ProfessorScheduleScreen() } }
}

@Composable
private fun ProfessorMoreNav(modifier: Modifier) {
    val nav = rememberNavController()
    val auth = AppContainer.authRepository
    NavHost(nav, "hub", modifier) {
        composable("hub") {
            ProfessorMoreHub(
                onEarnings = { nav.navigate("earnings") },
                onAttendance = { nav.navigate("attendance") },
                onEvolution = { nav.navigate("evolution") },
                onMessages = { nav.navigate("messages") },
                onAnamnesis = { nav.navigate("anamnesis") },
                onLogout = { auth.logout() }
            )
        }
        composable("earnings") { ProfessorEarningsScreen { nav.popBackStack() } }
        composable("attendance") { ProfessorAttendanceScreen { nav.popBackStack() } }
        composable("evolution") { ProfessorEvolutionScreen { nav.popBackStack() } }
        composable("messages") { ProfessorMessagesScreen { nav.popBackStack() } }
        composable("anamnesis") { ProfessorMoreAnamnesisScreen { nav.popBackStack() } }
    }
}

@Composable
private fun StudentRoot(modifier: Modifier = Modifier) {
    var tab by remember { mutableIntStateOf(0) }
    Scaffold(modifier, containerColor = IosColors.groupedBackground, bottomBar = {
        NavigationBar(containerColor = IosColors.secondaryGroupedBackground) {
            NavigationBarItem(selected = tab == 0, onClick = { tab = 0 }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Início") })
            NavigationBarItem(selected = tab == 1, onClick = { tab = 1 }, icon = { Icon(Icons.Default.CreditCard, null) }, label = { Text("Pagamentos") })
            NavigationBarItem(selected = tab == 2, onClick = { tab = 2 }, icon = { Icon(Icons.Default.CalendarMonth, null) }, label = { Text("Agenda") })
        }
    }) { padding ->
        when (tab) {
            0 -> StudentDashboardScreen(Modifier.padding(padding))
            1 -> StudentPaymentsScreen(Modifier.padding(padding))
            2 -> StudentScheduleScreen(Modifier.padding(padding))
        }
    }
}

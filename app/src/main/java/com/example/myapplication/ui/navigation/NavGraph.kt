package com.example.myapplication.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.auth.LoginScreen
import com.example.myapplication.ui.screens.auth.RegisterScreen
import com.example.myapplication.ui.screens.calendar.CalendarScreen
import com.example.myapplication.ui.screens.joincalendar.JoinCalendarScreen
import com.example.myapplication.ui.screens.plans.PlansScreen
import com.example.myapplication.ui.screens.profile.ProfileScreen
import com.example.myapplication.viewmodel.AuthViewModel
import com.example.myapplication.viewmodel.CalendarViewModel
import com.example.myapplication.viewmodel.PlansViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val CALENDAR = "calendar"
    const val PLANS = "plans"
    const val JOIN_CALENDAR = "join_calendar"
    const val PROFILE = "profile"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    calendarViewModel: CalendarViewModel,
    plansViewModel: PlansViewModel
) {
    val authState by authViewModel.uiState.collectAsState()

    if (authState.isCheckingSession) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (authState.isLoggedIn) Routes.CALENDAR else Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.CALENDAR) {
            CalendarScreen(
                calendarViewModel = calendarViewModel,
                onNavigateToPlans = { navController.navigate(Routes.PLANS) },
                onNavigateToJoin = { navController.navigate(Routes.JOIN_CALENDAR) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }
        composable(Routes.PLANS) {
            PlansScreen(
                plansViewModel = plansViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.JOIN_CALENDAR) {
            JoinCalendarScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

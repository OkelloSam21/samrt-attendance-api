package com.smartattendance.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.smartattendance.android.feature.admin.dashboard.adminDashboardScreen
import com.smartattendance.android.feature.auth.login.loginScreen
import com.smartattendance.android.feature.auth.signup.signUpScreen
import com.smartattendance.android.feature.lecturer.dashboard.lecturerDashboardScreen
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeDestination
import com.smartattendance.android.feature.onboarding.selectusertype.selectUserTypeScreen
import com.smartattendance.android.feature.student.dashboard.studentDashboardScreen

@Composable
fun MainNavHost() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SelectUserTypeDestination
    ) {
        // Authentication Flows
        selectUserTypeScreen(navController)
        loginScreen()
        signUpScreen()

        // User-specific Dashboard Screens
        adminDashboardScreen(navController)
        lecturerDashboardScreen(navController)
        studentDashboardScreen(
            navController,
            onNavigateToScanQr = { },
            onNavigateToHistory = {  }
        )
    }
}
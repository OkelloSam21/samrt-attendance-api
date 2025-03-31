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
fun MainNavHost(
    initialStartDestination: String = SelectUserTypeDestination.route,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = SelectUserTypeDestination
    ) {
        // Authentication Flows
        composable(SelectUserTypeDestination.route) {
            SelectUserTypeScreen(
                viewModel = hiltViewModel(),
                onNextClicked = { it->
                    navController.navigateToSignUp(it)
                }
            )
        }

        composable(
            route = LoginDestination.route,
            arguments = listOf(
                navArgument(LoginDestination.userTypeParam) {
                    defaultValue = LoginDestination.defaultUserType
                }
            )
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getString(LoginDestination.userTypeParam)
            LoginScreen()
        }

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
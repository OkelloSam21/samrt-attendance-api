package com.smartattendance.android.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.smartattendance.android.feature.admin.dashboard.AdminDashboardDestination
import com.smartattendance.android.feature.admin.dashboard.AdminDashboardScreen
import com.smartattendance.android.feature.admin.dashboard.navigateToAdminDashboard
import com.smartattendance.android.feature.auth.login.LoginDestination
import com.smartattendance.android.feature.auth.login.LoginScreen
import com.smartattendance.android.feature.auth.login.navigateToLogin
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeScreen
import com.smartattendance.android.feature.auth.signup.SignUpScreen
import com.smartattendance.android.feature.auth.signup.SignUpDestination
import com.smartattendance.android.feature.auth.signup.SignUpViewModel
import com.smartattendance.android.feature.auth.signup.navigateToSignUp
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeDestination
import com.smartattendance.android.feature.lecturer.dashboard.LecturerDashboardDestination
import com.smartattendance.android.feature.lecturer.dashboard.LecturerDashboardScreen
import com.smartattendance.android.feature.lecturer.dashboard.navigateToLecturerDashboard
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import com.smartattendance.android.feature.student.dashboard.StudentDashboardDestination
import com.smartattendance.android.feature.student.dashboard.StudentDashboardScreen
import com.smartattendance.android.feature.student.dashboard.navigateToStudentDashboard

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun MainNavHost(
    initialStartDestination: String = SelectUserTypeDestination.route,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = initialStartDestination
    ) {
        // Authentication Flows
        composable(SelectUserTypeDestination.route) {
            SelectUserTypeScreen(
                viewModel = hiltViewModel(),
                onNextClicked = { userType ->
                    // Save the user type in SavedStateHandle before navigating
                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedUserType", userType)
                    navController.navigateToSignUp(userType)
                }
            )
        }

        composable(
            route = LoginDestination.route,
        ) { backStackEntry ->
            LoginScreen(
                onNavigateToSignUp = {
                    // Retrieve the user type from previous back stack entry
                    val userType = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<UserType>("selectedUserType")

                    userType?.let { navController.navigateToSignUp(it) }
                },
                onNavigateToDashboard = { userType ->
                    when (userType) {
                        UserType.STUDENT -> navController.navigateToStudentDashboard()
                        UserType.LECTURER -> navController.navigateToLecturerDashboard()
                        UserType.ADMIN -> navController.navigateToAdminDashboard()
                        else -> {}
                    }
                },
                onNavigateToForgotPassword = {}
            )
        }

        composable(route = SignUpDestination.route) {
            val signUpViewModel: SignUpViewModel = hiltViewModel()

            SignUpScreen(
                viewModel = signUpViewModel,
                onSignUpSuccess = {
                    val userType = signUpViewModel.uiState.value.userType
                    when (userType) {
                        UserType.STUDENT -> navController.navigateToStudentDashboard()
                        UserType.LECTURER -> navController.navigateToLecturerDashboard()
                        UserType.ADMIN -> navController.navigateToAdminDashboard()
                        null -> {
                            // Handle error case
                            Log.e("SignUp", "No user type selected")
                        }
                    }
                },
                onLoginClicked = {
                    navController.navigateToLogin()
                },
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        // Dashboard Screens
        composable(StudentDashboardDestination.route) {
            StudentDashboardScreen(
                onNavigateToScanQr = { },
                onNavigateToHistory = { }
            )
        }

        composable(AdminDashboardDestination.route) {
            AdminDashboardScreen(
                onNavigateBack = {}
            )
        }

        composable(LecturerDashboardDestination.route) {
            LecturerDashboardScreen(
                onNavigateBack = {}
            )
        }
    }
}
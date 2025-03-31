package com.smartattendance.android.navigation

import android.annotation.SuppressLint
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
import com.smartattendance.android.feature.auth.login.navigateToDashBoard
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
                onNextClicked = { it ->
                    navController.navigateToSignUp(it)
                }
            )
        }

        composable(
            route = LoginDestination.route,
        ) { backStackEntry ->
            val selectUserTypeEntry = remember {
                navController.getBackStackEntry(SelectUserTypeDestination.route)
            }
            val selectedUserType =
                selectUserTypeEntry.savedStateHandle.get<UserType>("selectedUserType")
            LoginScreen(
                onNavigateToSignUp = {
                    when (selectedUserType) {
                        UserType.STUDENT -> navController.navigateToSignUp(UserType.STUDENT)
                        UserType.LECTURER -> navController.navigateToSignUp(UserType.LECTURER)
                        UserType.ADMIN -> navController.navigateToSignUp(UserType.ADMIN)
                        else -> {}
                    }
                },
                onNavigateToDashboard = {

                },
                onNavigateToForgotPassword = {

                }
            )
        }

        composable(route = SignUpDestination.route) {
            val signUpViewModel: SignUpViewModel = hiltViewModel()
            val selectUserTypeEntry = remember {
                navController.getBackStackEntry(SelectUserTypeDestination.route)
            }

            val selectedUserType =
                selectUserTypeEntry.savedStateHandle.get<UserType>("selectedUserType")
            SignUpScreen(
                viewModel = signUpViewModel,
                onSignUpSuccess = {
                    if (selectedUserType != null) {
                        when (selectedUserType) {
                            UserType.STUDENT -> navController.navigateToStudentDashboard()
                            UserType.LECTURER -> navController.navigateToLecturerDashboard()
                            UserType.ADMIN -> navController.navigateToAdminDashboard()
                        }
                    } else {
                        // Handle the case where userType is null
                        // You can show an error message or navigate back

                    }
                },
                onLoginClicked = {
                    navController.navigateToLogin()
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
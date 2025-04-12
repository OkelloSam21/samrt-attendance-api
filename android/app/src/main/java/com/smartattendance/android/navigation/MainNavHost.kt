package com.smartattendance.android.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartattendance.android.MainViewModel
import com.smartattendance.android.NavigationEvent
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import com.smartattendance.android.feature.admin.dashboard.AdminDashboardDestination
import com.smartattendance.android.feature.admin.dashboard.AdminDashboardScreen
import com.smartattendance.android.feature.auth.login.LoginScreen
import com.smartattendance.android.feature.auth.signup.SignUpScreen
import com.smartattendance.android.feature.auth.signup.SignUpViewModel
import com.smartattendance.android.feature.lecturer.dashboard.LecturerDashboardScreen
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeDestination
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeScreen
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import com.smartattendance.android.feature.student.dashboard.StudentDashboardScreen
import com.smartattendance.android.feature.student.history.StudentAttendanceHistoryScreen
import com.smartattendance.android.feature.student.scanqr.StudentScanQrScreen
import com.smartattendance.android.navigation.helper.navigateToAttendanceHistory
import com.smartattendance.android.navigation.helper.navigateToDashboardIfAuthorized
import com.smartattendance.android.navigation.helper.navigateToLogin
import com.smartattendance.android.navigation.helper.navigateToScanQr
import com.smartattendance.android.navigation.helper.navigateToSelectUserType
import com.smartattendance.android.navigation.helper.navigateToSignUp
import kotlinx.coroutines.launch

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun MainNavHost(
    startDestinationEvent: NavigationEvent?,
    navController: NavHostController = rememberNavController(),
    userPreferencesRepository: UserPreferencesRepository
) {
    val coroutineScope = rememberCoroutineScope()
    val mainViewModel: MainViewModel = hiltViewModel()

    val startDestination = mainViewModel.startDestination.collectAsStateWithLifecycle()

    LaunchedEffect(startDestinationEvent) {
        when (val event = startDestination.value) {
            NavigationEvent.NavigateToSelectUserType -> navController.navigateToSelectUserType()
            is NavigationEvent.NavigateToLogin -> {
                navController.navigateToLogin(event.userType)
            }
            NavigationEvent.NavigateToStudentDashboard -> {
                navController.navigateToDashboardIfAuthorized(
                    userPreferencesRepository,
                    StudentDashboardDestination.route
                )
            }
            NavigationEvent.NavigateToAdminDashboard -> {
                navController.navigateToDashboardIfAuthorized(
                    userPreferencesRepository,
                    AdminDashboardDestination.route
                )
            }
            NavigationEvent.NavigateToLecturerDashboard -> {
                navController.navigateToDashboardIfAuthorized(
                    userPreferencesRepository,
                    LecturerDashboardDestination.route
                )
            }
            null -> {
                // Handle null case if needed
            }
        }
        mainViewModel.onNavigationHandled()
    }

    NavHost(
        navController = navController,
        startDestination = SelectUserTypeDestination.route
    ) {
        // Authentication Flows
        composable(SelectUserTypeDestination.route) {
            SelectUserTypeScreen(
                viewModel = hiltViewModel(),
                onNextClicked = { userType ->
                    navController.navigateToSignUp(userType)
                }
            )
        }

        composable(
            route = LoginDestination.route,
            arguments = listOf(
                navArgument("userType") { type = NavType.StringType }
            )
        ) {
            LoginScreen(
                onNavigateToSignUp = {
                    // In this approach, we don't navigate to SignUp from Login anymore with userType.
                    // If you need to, consider navigating back to SelectUserType first, or rethinking the flow.
                    navController.navigate(SelectUserTypeDestination.route)
                },
                onNavigateToDashboard = { userType ->
                    val destination = when (userType) {
                        UserType.STUDENT -> StudentDashboardDestination.route
                        UserType.LECTURER -> LecturerDashboardDestination.route
                        UserType.ADMIN -> AdminDashboardDestination.route
                    }
                    coroutineScope.launch {
                        navController.navigateToDashboardIfAuthorized(userPreferencesRepository, destination)
                    }
                },
                onNavigateToForgotPassword = {}
            )
        }

        composable(
            route = SignUpDestination.route,
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) { backStackEntry ->
            val userTypeString = backStackEntry.arguments?.getString("userType")

            val userType = try {
                userTypeString?.let { UserType.valueOf(it) }
            } catch (e: IllegalArgumentException) {
                Log.e("MainNavHost", "Invalid UserType: $userTypeString", e)
                null
            }

            if (userType != null) {
                val signUpViewModel: SignUpViewModel = hiltViewModel()

                LaunchedEffect (userType) {
                    signUpViewModel.setUserType(userType)
                    userPreferencesRepository.saveSelectedUserType(userType.name)
                }

                SignUpScreen(
                    viewModel = signUpViewModel,
                    userType = userType, // Pass the userType to the SignUpScreen
                    onSignUpSuccess = {
                        val destination = when (userType) {
                            UserType.STUDENT -> StudentDashboardDestination.route
                            UserType.LECTURER -> LecturerDashboardDestination.route
                            UserType.ADMIN -> AdminDashboardDestination.route
                        }

                        coroutineScope.launch {
                            navController.navigateToDashboardIfAuthorized(userPreferencesRepository, destination)
                        }
                    },
                    onLoginClicked = {
                        navController.navigateToLogin(userType)
                    },
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }


        composable(StudentDashboardDestination.route) {
            StudentDashboardScreen(
                onNavigateToScanQr = {
                    navController.navigateToScanQr()
                },
                onNavigateToHistory = {
                    navController.navigateToAttendanceHistory()
                }
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

        composable(ScanQrDestination.route) {
            StudentScanQrScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable(AttendanceHistoryDestination.route) {
            StudentAttendanceHistoryScreen(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }
    }
}
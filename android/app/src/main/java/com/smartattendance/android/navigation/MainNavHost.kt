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
import com.smartattendance.android.navigation.helper.navigateToDashboardIfAuthorized
import com.smartattendance.android.navigation.helper.navigateToLogin
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
        startDestination.let { event ->
            when (event) {
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
            }
            mainViewModel.onNavigationHandled()
        }
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
//                backStackEntry ->
//
//            val userTypeString = backStackEntry.arguments?.getString("userType")
//
//            val userType = try {
//                userTypeString?.let { UserType.valueOf(it) } ?: throw IllegalArgumentException("UserType cannot be null")
//            } catch (e: IllegalArgumentException) {
//                Log.e("Main NavHost", "Invalid or missing userType in Login route", e)
//                navController.navigateToSelectUserType()
//                return@composable
//            }
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
                null // Or handle the error as appropriate for your app
            }

            if (userType != null) {
                val signUpViewModel: SignUpViewModel = hiltViewModel()

                SignUpScreen(
                    viewModel = signUpViewModel,
                    userType = userType,
                    onSignUpSuccess = {
                        val destination = when (userType) {
                            UserType.STUDENT -> StudentDashboardDestination.route
                            UserType.LECTURER -> LecturerDashboardDestination.route
                            UserType.ADMIN -> AdminDashboardDestination.route
                        }

                        coroutineScope.launch {
                            Log.d("SignUpScreen", "Destination : $destination")
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
            } else {
                // Handle the error: UserType not found in arguments
                Log.e("MainNavHost", "UserType not found in SignUp arguments")
                // Maybe navigate back or show an error message.
            }
        }

        // Dashboard Screens - No changes needed here as they don't receive userType directly anymore.
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
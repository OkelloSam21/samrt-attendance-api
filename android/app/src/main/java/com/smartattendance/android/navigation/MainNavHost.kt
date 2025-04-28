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
import com.smartattendance.android.feature.auth.login.LoginScreen
import com.smartattendance.android.feature.auth.signup.SignUpScreen
import com.smartattendance.android.feature.auth.signup.SignUpViewModel
import com.smartattendance.android.feature.lecturer.course.CreateCourseScreen
import com.smartattendance.android.feature.lecturer.createsession.CreateAttendanceSessionScreen
import com.smartattendance.android.feature.lecturer.dashboard.LecturerDashboardScreen
import com.smartattendance.android.feature.lecturer.session.SessionDetailScreen
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeScreen
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import com.smartattendance.android.feature.student.dashboard.StudentDashboardScreen
import com.smartattendance.android.feature.student.history.StudentAttendanceHistoryScreen
import com.smartattendance.android.feature.student.naviagtion.studentFlowScreens
import com.smartattendance.android.feature.student.scanqr.StudentScanQrScreen
import com.smartattendance.android.navigation.helper.navigateToCreateCourse
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
                    navController.navigate(SelectUserTypeDestination.route)
                },
                onNavigateToDashboard = { userType ->
                    val destination = when (userType) {
                        UserType.STUDENT -> StudentDashboardDestination.route
                        UserType.LECTURER -> LecturerDashboardDestination.route
                        UserType.ADMIN -> AdminDashboardDestination.route
                    }
                    coroutineScope.launch {
                        navController.navigateToDashboardIfAuthorized(
                            userPreferencesRepository,
                            destination
                        )
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

                LaunchedEffect(userType) {
                    signUpViewModel.setUserType(userType)
                    userPreferencesRepository.saveSelectedUserType(userType.name)
                }

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
                            navController.navigateToDashboardIfAuthorized(
                                userPreferencesRepository,
                                destination
                            )
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

        // Student flows
        studentFlowScreens(navController)

        // Lecturer flows
        composable(LecturerDashboardDestination.route) {
            LecturerDashboardScreen(
                onNavigateToCreateSession = { courseId ->
                    navController.navigate(CreateSessionDestination.route)
                },
                onNavigateToSessionDetail = { sessionId ->
                    navController.navigate("session_detail/$sessionId")
                },
                onNavigateToProfile = {
                    // TODO: Navigate to profile
                },
                onNavigateToAttendanceReport = { courseId ->
                    navController.navigate("attendance_report/$courseId")
                },
                onNavigateToCreateCourse = { lecturerId ->
                    navController.navigateToCreateCourse(lecturerId)
                }
            )
        }

        composable(
            route = CreateSessionDestination.route,
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""

            CreateAttendanceSessionScreen(
                courseId = courseId,
                onSessionCreated = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "session_detail/{sessionId}",
            arguments = listOf(
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""

            SessionDetailScreen(
                sessionId = sessionId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEndSession = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "attendance_report/{courseId}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""

            // TODO: Implement attendance report screen
            /*
            AttendanceReportScreen(
                courseId = courseId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
            */
        }

        composable(
            route = CreateCourseDestination.route,
            arguments = listOf(
                navArgument("lecturerId") { type = NavType.StringType}
            )
        ) { backStackEntry ->

            val lecturerId = backStackEntry.arguments?.getString("lecturerId") ?: ""
            CreateCourseScreen(
                lecturerId = lecturerId,
                onCourseCreated = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // Admin flows
        adminScreens(navController)
    }
}
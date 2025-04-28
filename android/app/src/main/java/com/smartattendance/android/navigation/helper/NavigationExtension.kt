package com.smartattendance.android.navigation.helper

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import com.smartattendance.android.feature.lecturer.course.CreateCourseScreen
import com.smartattendance.android.feature.lecturer.createsession.CreateAttendanceSessionScreen
import com.smartattendance.android.feature.lecturer.dashboard.LecturerDashboardScreen
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeDestination
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import com.smartattendance.android.feature.student.dashboard.StudentDashboardScreen
import com.smartattendance.android.navigation.AdminDashboardDestination
import com.smartattendance.android.navigation.CreateCourseDestination
import com.smartattendance.android.navigation.CreateSessionDestination
import com.smartattendance.android.navigation.LecturerDashboardDestination
import com.smartattendance.android.navigation.LoginDestination
import com.smartattendance.android.navigation.SignUpDestination
import com.smartattendance.android.navigation.StudentDashboardDestination
import kotlinx.coroutines.flow.firstOrNull

suspend fun NavController.navigateToDashboardIfAuthorized(
    userPreferencesRepository: UserPreferencesRepository,
    route: String
) {
    val userRole = userPreferencesRepository.userRole.firstOrNull()?.lowercase()
    val expectedRole = when (route) {
        StudentDashboardDestination.route -> UserType.STUDENT.name.lowercase()
        LecturerDashboardDestination.route -> UserType.LECTURER.name.lowercase()
        AdminDashboardDestination.route -> UserType.ADMIN.name.lowercase()
        else -> null
    }

    Log.e("navigateToDashboardIfAuthorized", "userRole: $userRole, expectedRole: $expectedRole")
//    Log.e("navigateToDashboardIfAuthorized", "userRole: $userRole, expectedRoute: $expectedRole")

    if (userRole != null && userRole == expectedRole) {
        navigate(route) {
            popUpTo(0) { inclusive = true }
        }
    } else {
        // Unauthorized: Navigate to SelectUserType and clear backstack
        Log.e("navigateToDashboardIfAuthorized", "userRole: $userRole, expectedRole : $expectedRole")
        popBackStack(SelectUserTypeDestination.route, inclusive = false)
        navigate(SelectUserTypeDestination.route) {
            popUpTo(0) { inclusive = true }
        }
    }
}

fun NavController.navigateToSelectUserType() {
    this.navigate(SelectUserTypeDestination.route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}

fun NavController.navigateToSignUp(userType: UserType) {
    navigate(SignUpDestination.createRoute(userType))
}

fun NavController.navigateToLogin(userType: UserType) {
    this.navigate(LoginDestination.createRoute(
        userType = userType)) {
        launchSingleTop = true
    }
}

fun NavController.navigateToCreateSession(courseId: String) {
    this.navigate(CreateSessionDestination.createRoute(courseId)) {
        launchSingleTop = true
    }
}

fun NavController.navigateToCreateCourse(lecturerId: String) {
    this.navigate(CreateCourseDestination.createRoute(lecturerId)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.createSession(courseId: String, navController: NavController) {
    composable<CreateSessionDestination> {
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
}

fun NavGraphBuilder.navigateToCreateCourse(navController: NavController) {
    composable<CreateCourseDestination> {
        CreateCourseScreen(
            lecturerId = it.arguments?.getString("lecturerId") ?: "",
            onCourseCreated = {
                navController.popBackStack()
            },
            onBack = {
                navController.popBackStack()
            }
        )
    }
}

fun NavController.navigateToLecturerDashboard() {
    this.navigate(LecturerDashboardDestination.route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}

fun NavGraphBuilder.lecturerDashboardScreen(
    navController: NavController
) {
    composable<LecturerDashboardDestination> {
        LecturerDashboardScreen(
            onNavigateToProfile = {},
            onNavigateToCreateSession = {
                navController.navigateToCreateSession(it)
            },
            onNavigateToSessionDetail = {
                navController.navigate("session_detail/$it")
            },
            onNavigateToAttendanceReport = {
                navController.navigate("attendance_report/$it")
            },
            onNavigateToCreateCourse = {
                navController.navigateToCreateSession(it)
            }
        )
    }
}

fun NavController.navigateToStudentDashboard() {
    this.navigate(StudentDashboardDestination.route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}

fun NavGraphBuilder.studentDashboardScreen(
    navController: NavController,
    onNavigateToScanQr: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    composable<StudentDashboardDestination> {
        StudentDashboardScreen(
            onNavigateToScanQr = onNavigateToScanQr,
            onNavigateToHistory = onNavigateToHistory
        )
    }
}
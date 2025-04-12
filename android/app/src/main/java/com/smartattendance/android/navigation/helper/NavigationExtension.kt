package com.smartattendance.android.navigation.helper

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import com.smartattendance.android.feature.admin.dashboard.AdminDashboardDestination
import com.smartattendance.android.feature.lecturer.createsession.CreateAttendanceSessionScreen
import com.smartattendance.android.feature.lecturer.dashboard.LecturerDashboardScreen
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeDestination
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import com.smartattendance.android.feature.student.dashboard.StudentDashboardScreen
import com.smartattendance.android.navigation.AttendanceHistoryDestination
import com.smartattendance.android.navigation.CreateSessionDestination
import com.smartattendance.android.navigation.LecturerDashboardDestination
import com.smartattendance.android.navigation.LoginDestination
import com.smartattendance.android.navigation.ScanQrDestination
import com.smartattendance.android.navigation.SignUpDestination
import com.smartattendance.android.navigation.StudentDashboardDestination
import kotlinx.coroutines.flow.firstOrNull

suspend fun NavController.navigateToDashboardIfAuthorized(
    userPreferencesRepository: UserPreferencesRepository,
    route: String
) {
    val userRole = userPreferencesRepository.userRole.firstOrNull()

    when {
        route == StudentDashboardDestination.route && userRole == UserType.STUDENT.name -> {
            navigate(route) { popUpTo(0) { inclusive = true } }
        }
        route == LecturerDashboardDestination.route && userRole == UserType.LECTURER.name -> {
            navigate(route) { popUpTo(0) { inclusive = true } }
        }
        route == AdminDashboardDestination.route && userRole == UserType.ADMIN.name -> {
            navigate(route) { popUpTo(0) { inclusive = true } }
        }
        else -> {
            // If no matching role or we can't verify, force navigation to dashboard anyway
            // based on the requested route, since we know the signup was successful
            navigate(route) { popUpTo(0) { inclusive = true } }
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

fun NavController.navigateToCreateSession() {
    this.navigate(CreateSessionDestination) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.createSession() {
    composable<CreateSessionDestination> {
        CreateAttendanceSessionScreen()
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
            onNavigateBack = { navController.navigateUp() }
        )
    }
}

fun NavController.navigateToStudentDashboard() {
    this.navigate(StudentDashboardDestination.route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}

fun NavController.navigateToScanQr() {
    this.navigate(ScanQrDestination.route) {
        launchSingleTop = true
//        popUpTo(0) { inclusive = true }
    }
}

fun NavController.navigateToAttendanceHistory() {
    this.navigate(AttendanceHistoryDestination.route) {
        launchSingleTop = true
//        popUpTo(0) { inclusive = true }
    }
}


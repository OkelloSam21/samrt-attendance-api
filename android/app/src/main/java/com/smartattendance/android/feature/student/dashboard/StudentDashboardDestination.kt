package com.smartattendance.android.feature.student.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object StudentDashboardDestination {
    const val route = "student_dashboard"
}

class StudentDashboardArgs(savedStateHandle: SavedStateHandle)

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


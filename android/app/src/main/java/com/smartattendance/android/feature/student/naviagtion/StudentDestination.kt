package com.smartattendance.android.feature.student.naviagtion

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.smartattendance.android.feature.student.dashboard.StudentDashboardScreen
import com.smartattendance.android.feature.student.history.StudentAttendanceHistoryScreen
import com.smartattendance.android.feature.student.scanqr.StudentScanQrScreen
import com.smartattendance.android.navigation.AttendanceHistoryDestination
import com.smartattendance.android.navigation.ScanQrDestination
import com.smartattendance.android.navigation.StudentDashboardDestination

// Navigation extension functions
fun NavController.navigateToStudentDashboard() {
    this.navigate(StudentDashboardDestination.route) {
        popUpTo(this@navigateToStudentDashboard.graph.id) {
            inclusive = true
        }
    }
}

fun NavController.navigateToScanQr() {
    this.navigate(ScanQrDestination.route)
}

fun NavController.navigateToAttendanceHistory() {
    this.navigate(AttendanceHistoryDestination.route)
}

// NavGraph builder extensions
fun NavGraphBuilder.studentFlowScreens(navController: NavController) {
    // Student Dashboard
    composable(StudentDashboardDestination.route) {
        StudentDashboardScreen(
            onNavigateToScanQr = { navController.navigateToScanQr() },
            onNavigateToHistory = { navController.navigateToAttendanceHistory() },
            onNavigateToProfile = { /* Handle profile navigation */ }
        )
    }
    
    // QR Code Scanning / Session Code Entry Screen
    composable(ScanQrDestination.route) {
        StudentScanQrScreen(
            onBackClicked = { navController.popBackStack() }
        )
    }
    
    // Attendance History Screen
    composable(AttendanceHistoryDestination.route) {
        StudentAttendanceHistoryScreen(
            onBackClicked = { navController.popBackStack() }
        )
    }
}
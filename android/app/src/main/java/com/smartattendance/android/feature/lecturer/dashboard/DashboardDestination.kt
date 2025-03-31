package com.smartattendance.android.feature.lecturer.dashboard

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object LecturerDashboardDestination {
    const val route = "lecturer_dashboard"
}

class LecturerDashboardArgs(savedStateHandle: SavedStateHandle)

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

// Placeholder for the actual screen
@Composable
fun LecturerDashboardScreen(
    onNavigateBack: () -> Unit
) {
    // Implement lecturer dashboard UI
}
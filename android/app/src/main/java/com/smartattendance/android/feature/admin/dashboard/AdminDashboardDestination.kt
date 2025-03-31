package com.smartattendance.android.feature.admin.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object AdminDashboardDestination {
    const val route = "admin_dashboard"
}

class AdminDashboardArgs(savedStateHandle: SavedStateHandle)

fun NavController.navigateToAdminDashboard() {
    this.navigate(AdminDashboardDestination.route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}

fun NavGraphBuilder.adminDashboardScreen(
    navController: NavController
) {
    composable<AdminDashboardDestination> {
        AdminDashboardScreen(
            onNavigateBack = { navController.navigateUp() }
        )
    }
}

// Placeholder for the actual screen
@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit
) {
    // Implement admin dashboard UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(text = "Admin Dashboard")

    }
}
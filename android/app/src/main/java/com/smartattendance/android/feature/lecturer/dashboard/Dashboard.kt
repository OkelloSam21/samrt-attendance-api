package com.smartattendance.android.feature.lecturer.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
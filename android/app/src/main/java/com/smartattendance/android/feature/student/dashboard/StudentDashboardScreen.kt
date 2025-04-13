package com.smartattendance.android.feature.student.dashboard

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartattendance.android.domain.model.AttendanceSession

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StudentDashboardScreen(
    viewModel: StudentDashboardViewModel = hiltViewModel(),
    onNavigateToScanQr: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on dashboard */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person, // Replace with appropriate icon
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToScanQr,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person, // Replace with appropriate icon
                            contentDescription = "Scan QR"
                        )
                    },
                    label = { Text("Scan QR") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToHistory,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person, // Replace with appropriate icon
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History") }
                )
            }
        }
    ) {paddingValues ->
        StudentDashboardScreenContent(
            viewModel = viewModel,
            onNavigateToScanQr = onNavigateToScanQr,
            onNavigateToHistory = onNavigateToHistory,
            onNavigateToProfile = { /* Navigate to profile */ }
        )
    }
}

@Composable
fun StudentDashboardScreenContent(
    viewModel: StudentDashboardViewModel,
    onNavigateToScanQr: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Error Handling
//    uiState.errorMessage?.let { errorMessage ->
//        AlertDialog(
//            onDismissRequest = { /* Dismiss logic */ },
//            title = { Text("Error") },
//            text = { Text(errorMessage) },
//            confirmButton = {
//                TextButton(onClick = { /* Dismiss logic */ }) {
//                    Text("OK")
//                }
//            }
//        )
//    }

//    Scaffold(
//        topBar = {
//            StudentDashboardTopAppBar(
//                studentName = uiState.studentName,
//                onProfileClick = onNavigateToProfile
//            )
//        },
//        bottomBar = {
//            StudentDashboardBottomBar(
//                onScanQrClick = {
//                    // Generate QR code before navigating
//                    viewModel.generateQrCode()
//                    onNavigateToScanQr()
//                },
//                onHistoryClick = onNavigateToHistory
//            )
//        }
//    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding()
                .padding(horizontal = 16.dp)
        ) {
            // Attendance Summary Card
//            AttendanceSummaryCard(
//                totalClasses = uiState.totalClasses,
//                attendedClasses = uiState.attendedClasses
//            )

            Spacer(modifier = Modifier.height(16.dp))

            // Upcoming Sessions
            Text(
                text = "Upcoming Sessions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
//            UpcomingSessionsList(
//                sessions = uiState.classesAbsent + uiState.classesAttended,
//                onSessionSelect = { session ->
//                    // Potential future implementation for session details
//                }
//            )
        }
    }


// Updated Upcoming Sessions List
@Composable
fun UpcomingSessionsList(
    sessions: List<AttendanceSession>,
    onSessionSelect: (AttendanceSession) -> Unit
) {
    LazyColumn {
        items(sessions) { session ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSessionSelect(session) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "course",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Instructor: ${session.lecturerId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Date: ${session.createdAt}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Time: ${session.createdAt} - ${session.expiresAt}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
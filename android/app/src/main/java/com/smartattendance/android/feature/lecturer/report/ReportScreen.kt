package com.smartattendance.android.feature.lecturer.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartattendance.android.data.network.model.SessionType
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportScreen(
    viewModel: AttendanceReportViewModel = hiltViewModel(),
    courseId: String,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(AttendanceReportTab.SUMMARY) }

    LaunchedEffect(courseId) {
        viewModel.loadReportData(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Report") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Export functionality */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export Report"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Course information header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = state.courseName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Total Sessions: ${state.sessions.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Average Attendance: ${state.averageAttendancePercentage}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                AttendanceReportTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = { Text(tab.title) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null
                            )
                        }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTab) {
                AttendanceReportTab.SUMMARY -> {
                    SummaryTab(
                        sessions = state.sessions,
                        isLoading = state.isLoading,
                        error = state.error,
                        onRefresh = { viewModel.loadReportData(courseId) }
                    )
                }
                AttendanceReportTab.STUDENTS -> {
                    StudentsTab(
                        students = state.students,
                        isLoading = state.isLoading,
                        error = state.error,
                        onRefresh = { viewModel.loadReportData(courseId) }
                    )
                }
                AttendanceReportTab.ANALYTICS -> {
                    AnalyticsTab(
                        analytics = state.analytics,
                        isLoading = state.isLoading,
                        error = state.error,
                        onRefresh = { viewModel.loadReportData(courseId) }
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryTab(
    sessions: List<SessionSummary>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        ErrorMessage(
            error = error,
            onRetry = onRefresh
        )
    } else if (sessions.isEmpty()) {
        EmptyState(
            message = "No attendance sessions found for this course.",
            icon = Icons.Default.Class
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sessions) { session ->
                SessionSummaryCard(session = session)
            }
        }
    }
}

@Composable
fun StudentsTab(
    students: List<StudentAttendance>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        ErrorMessage(
            error = error,
            onRetry = onRefresh
        )
    } else if (students.isEmpty()) {
        EmptyState(
            message = "No student attendance data found for this course.",
            icon = Icons.Default.People
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students) { student ->
                StudentAttendanceCard(student = student)
            }
        }
    }
}

@Composable
fun AnalyticsTab(
    analytics: AttendanceAnalytics,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        ErrorMessage(
            error = error,
            onRetry = onRefresh
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                AnalyticsCard(
                    title = "Overall Attendance Rate",
                    value = "${analytics.overallAttendanceRate}%",
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnalyticsCard(
                        title = "Present",
                        value = "${analytics.presentPercentage}%",
                        icon = Icons.Default.Done,
                        color = Color.Green,
                        modifier = Modifier.weight(1f)
                    )

                    AnalyticsCard(
                        title = "Absent",
                        value = "${analytics.absentPercentage}%",
                        icon = Icons.Default.Close,
                        color = Color.Red,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                AnalyticsCard(
                    title = "At-Risk Students",
                    value = "${analytics.atRiskStudentsCount}",
                    icon = Icons.Default.Warning,
                    color = Color(0xFFFF9800) // Orange
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Attendance Trends",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // This would be a line chart in a real app
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Attendance trend chart would be displayed here")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Weekly Distribution",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // This would be a bar chart in a real app
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Weekly attendance distribution chart would be displayed here")
                }
            }
        }
    }
}

@Composable
fun SessionSummaryCard(session: SessionSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (session.sessionType == SessionType.PHYSICAL.name) Icons.Default.LocationOn else Icons.Default.Computer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatDate(session.date),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Session Code: ${session.sessionCode}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = getAttendanceColor(session.attendancePercentage),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${session.attendancePercentage}%",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { session.attendancePercentage / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = getAttendanceColor(session.attendancePercentage)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Present: ${session.presentCount}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Total: ${session.totalStudents}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun StudentAttendanceCard(student: StudentAttendance) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "ID: ${student.id}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = getAttendanceColor(student.attendancePercentage),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${student.attendancePercentage}%",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { student.attendancePercentage / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = getAttendanceColor(student.attendancePercentage)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Present: ${student.sessionsAttended}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Total: ${student.totalSessions}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.2f), shape = RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ErrorMessage(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Retry")
            }
        }
    }
}

// Helper functions
private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(date)
}

private fun getAttendanceColor(percentage: Int): Color {
    return when {
        percentage >= 85 -> Color(0xFF4CAF50) // Green
        percentage >= 75 -> Color(0xFF8BC34A) // Light Green
        percentage >= 60 -> Color(0xFFFFC107) // Amber
        else -> Color(0xFFF44336) // Red
    }
}

// Data classes and enums
enum class AttendanceReportTab(val title: String, val icon: ImageVector) {
    SUMMARY("Summary", Icons.Default.Assessment),
    STUDENTS("Students", Icons.Default.People),
    ANALYTICS("Analytics", Icons.Default.BarChart)
}

data class SessionSummary(
    val id: String,
    val sessionCode: String,
    val sessionType: String,
    val date: Date,
    val presentCount: Int,
    val totalStudents: Int,
    val attendancePercentage: Int
)

data class StudentAttendance(
    val id: String,
    val name: String,
    val sessionsAttended: Int,
    val totalSessions: Int,
    val attendancePercentage: Int
)

data class AttendanceAnalytics(
    val overallAttendanceRate: Int,
    val presentPercentage: Int,
    val absentPercentage: Int,
    val atRiskStudentsCount: Int,
    val weeklyData: List<WeeklyAttendance>,
    val trendData: List<DailyAttendance>
)

data class WeeklyAttendance(
    val weekNumber: Int,
    val percentage: Int
)

data class DailyAttendance(
    val date: Date,
    val percentage: Int
)


//@Composable
//fun AnalyticsTab(
//    analytics: AttendanceAnalytics,
//    isLoading: Boolean,
//    error: String?,
//    onRefresh: () -> Unit
//) {
//    if (isLoading) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//    } else if (error != null) {
//        ErrorMessage(
//            error = error,
//            onRetry = onRefresh
//        )
//    } else {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//        ) {
//            item {
//                AnalyticsCard(
//                    title = "Overall Attendance Rate",
//                    value = "${analytics.overallAttendanceRate}%",
//                    icon = Icons.Default.CheckCircle,
//                    color = MaterialTheme.colorScheme.primary
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                ) {
//                    AnalyticsCard(
//                        title = "Present",
//                        value = "${analytics.presentPercentage}%",
//                        icon = Icons.Default.Done,
//                        color = Color.Green,
//                        modifier = Modifier.weight(1f)
//                    )
//
//                    AnalyticsCard(
//                        title = "Absent",
//                        value = "${analytics.absentPercentage}%",
//                        icon = Icons.Default.Close,
//                        color = Color.Red,
//                        modifier = Modifier.weight(1f)
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                AnalyticsCard(
//                    title = "At-Risk Students",
//                    value = "${analytics.atRiskStudentsCount}",
//                    icon = Icons.Default.Warning,
//                    color = Color(0xFFFF9800) // Orange
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "Attendance Trends",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(vertical = 8.dp)
//                )
//
//                // This would be a line chart in a real app
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("Attendance trend chart would be displayed here")
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = "Weekly Distribution",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(vertical = 8.dp)
//                )
//
//                // This would be a bar chart in a real app
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("Weekly attendance distribution chart would be displayed here")
//                }
//            }
//        }
//    }
//    }
//}
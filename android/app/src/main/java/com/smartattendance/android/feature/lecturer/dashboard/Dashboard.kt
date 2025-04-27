package com.smartattendance.android.feature.lecturer.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartattendance.android.domain.model.Course
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LecturerDashboardScreen(
    viewModel: LecturerDashboardViewModel = hiltViewModel(),
    onNavigateToCreateSession: (String) -> Unit,
    onNavigateToSessionDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAttendanceReport: (String) -> Unit,
    onNavigateToCreateCourse: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateSessionDialog by remember { mutableStateOf(false) }
    var selectedCourseId by remember { mutableStateOf("") }

    var showOptions by remember { mutableStateOf(false) }

    var userId = viewModel.userId

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lecturer Dashboard") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    Box {
                        IconButton(onClick = { showOptions = !showOptions }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More Options",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        DropdownMenu(
                            expanded = showOptions,
                            onDismissRequest = { showOptions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Create New Course") },
                                onClick = {
                                    showOptions = false
                                    onNavigateToCreateCourse(userId.toString())
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("My Profile") },
                                onClick = {
                                    showOptions = false
                                    onNavigateToProfile()
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateSessionDialog = true },
                icon = { Icon(Icons.Default.Add, "Create Attendance Session") },
                text = { Text("New Session") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        LecturerDashboardContent(
            state = state,
            onRefresh = { viewModel.loadDashboardData() },
            onCourseSelected = { courseId ->
                selectedCourseId = courseId
                showCreateSessionDialog = true
            },
            onSessionSelected = onNavigateToSessionDetail,
            onViewAttendanceReport = onNavigateToAttendanceReport,
            modifier = Modifier.padding(paddingValues)
        )

        // Create Session Dialog
        if (showCreateSessionDialog) {
            if (selectedCourseId.isEmpty() && state.courses.isNotEmpty()) {
                selectedCourseId = state.courses.first().id
            }

            if (selectedCourseId.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = {
                        showCreateSessionDialog = false
                        selectedCourseId = ""
                    },
                    title = { Text("Create Attendance Session") },
                    text = {
                        Column {
                            Text("Select a course to create an attendance session:")

                            Spacer(modifier = Modifier.height(16.dp))

                            state.courses.forEach { course ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedCourseId = course.id }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedCourseId == course.id,
                                        onClick = { selectedCourseId = course.id }
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = course.name,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showCreateSessionDialog = false
                                onNavigateToCreateSession(selectedCourseId)
                            }
                        ) {
                            Text("Create")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showCreateSessionDialog = false
                                selectedCourseId = ""
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            } else {
                // No courses available
                AlertDialog(
                    onDismissRequest = { showCreateSessionDialog = false },
                    title = { Text("No Courses Available") },
                    text = { Text("You need to create a course first before you can create an attendance session.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                showCreateSessionDialog = false
                                onNavigateToCreateCourse(userId.toString())
                            }
                        ) {
                            Text("Create Course")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCreateSessionDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LecturerDashboardContent(
    state: LecturerDashboardUiState,
    onRefresh: () -> Unit,
    onCourseSelected: (String) -> Unit,
    onSessionSelected: (String) -> Unit,
    onViewAttendanceReport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DashboardSummaryCard(
                    totalCourses = state.courses.size,
                    activeSessions = state.activeSessions.size,
                    totalStudents = state.totalStudents // New field to track total students
                )
            }

            // Courses Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Courses",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Show a "See All" button if we have many courses
                    if (state.courses.size > 3) {
                        TextButton(onClick = { /* Navigate to courses list */ }) {
                            Text("See All")
                        }
                    }
                }
            }

            if (state.courses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No courses found. Create your first course to get started.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            } else {
                items(state.courses.take(3)) { course ->
                    CourseCard(
                        course = course,
                        onCreateSession = { onCourseSelected(course.id) },
                        onViewAttendance = { onViewAttendanceReport(course.id) }
                    )
                }
            }

            // Active Sessions Section
            item {
                Text(
                    text = "Active Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            if (state.activeSessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No active sessions. Create a new attendance session to start tracking.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            } else {
                items(state.activeSessions) { session ->
                    SessionCard(
                        session = session,
                        onClick = { onSessionSelected(session.id) }
                    )
                }
            }

            // Recent Sessions Section (if we want to show past sessions)
            if (state.recentSessions.isNotEmpty()) {
                item {
                    Text(
                        text = "Recent Sessions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                items(state.recentSessions.take(3)) { session ->
                    SessionCard(
                        session = session,
                        onClick = { onSessionSelected(session.id) },
                        isActive = false
                    )
                }
            }

            // Error state
            if (state.error != null) {
                item {
                    ErrorMessageCard(
                        errorMessage = state.error,
                        onRetry = onRefresh
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardSummaryCard(
    totalCourses: Int,
    activeSessions: Int,
    totalStudents: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Dashboard Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    icon = Icons.Default.Book,
                    value = totalCourses.toString(),
                    label = "Courses"
                )

                StatisticItem(
                    icon = Icons.Default.Timer,
                    value = activeSessions.toString(),
                    label = "Active Sessions"
                )

                StatisticItem(
                    icon = Icons.Default.People,
                    value = totalStudents.toString(),
                    label = "Students"
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CourseCard(
    course: Course,
    onCreateSession: () -> Unit,
    onViewAttendance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = course.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Created: ${formatDate(course.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )

//            if (course. > 0) {
//                Text(
//                    text = "Students: ${course.studentCount}",
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onViewAttendance,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Attendance")
                }

                Button(
                    onClick = onCreateSession
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create Session")
                }
            }
        }
    }
}

@Composable
fun SessionCard(
    session: SessionData,
    onClick: () -> Unit,
    isActive: Boolean = true,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Class,
                    contentDescription = null,
                    tint = if (isActive)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = session.courseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )

                if (isActive) {
                    Spacer(modifier = Modifier.weight(1f))

                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Text("ACTIVE")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Session Code: ${session.sessionCode}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Started: ${formatTime(session.startTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Expires: ${formatTime(session.endTime)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isActive) {
                LinearProgressIndicator(
                    progress = { calculateRemainingTimePercentage(session.startTime, session.endTime) },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = "Attendance: ${session.attendanceCount} students",
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorMessageCard(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

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
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

private fun formatTime(date: Date): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
}

private fun calculateRemainingTimePercentage(startTime: Date, endTime: Date): Float {
    val currentTime = Date().time
    val totalDuration = endTime.time - startTime.time
    val elapsed = currentTime - startTime.time

    if (totalDuration <= 0) return 0f
    if (elapsed >= totalDuration) return 1f

    return (elapsed.toFloat() / totalDuration).coerceIn(0f, 1f)
}
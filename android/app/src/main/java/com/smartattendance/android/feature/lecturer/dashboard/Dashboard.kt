package com.smartattendance.android.feature.lecturer.dashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

            if (selectedCourseId.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = {
                        showCreateSessionDialog = false
                        selectedCourseId = ""
                    },
                    title = { Text("Create Attendance Session") },
                    text = {
                        Column {
                            Text("Do you want to create a new attendance session for this course?")

                            Spacer(modifier = Modifier.height(16.dp))

                            DropdownMenu(
                                expanded = false,
                                onDismissRequest = { },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                state.courses.forEach { course ->
                                    DropdownMenuItem(
                                        text = { Text(course.name) },
                                        onClick = { selectedCourseId = course.id }
                                    )
                                }
                            }

                            // Display selected course
                            val selectedCourse = state.courses.find { it.id == selectedCourseId }
                            if (selectedCourse != null) {
                                OutlinedCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = selectedCourse.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(16.dp)
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
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    onNavigateBack: () -> Unit
) {
    // Implement admin dashboard UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Text(text = "Admin Dashboard")

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
package com.smartattendance.android.feature.lecturer.session

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailScreen(
    viewModel: SessionDetailViewModel = hiltViewModel(),
    sessionId: String,
    onNavigateBack: () -> Unit,
    onEndSession: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    var showEndSessionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        viewModel.loadSessionDetails(sessionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showEndSessionDialog = true }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "End Session"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.session != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Session info card
                SessionInfoCard(
                    courseName = state.courseName,
                    sessionType = state.session!!.sessionType,
                    startTime = formatDateTimeForDisplay(state.session!!.startTime),
                    endTime = formatDateTimeForDisplay(state.session!!.endTime),
                    sessionCode = state.session!!.sessionCode
                )

                Spacer(modifier = Modifier.height(24.dp))

                // QR Code
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "QR Code",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (state.qrCodeBitmap != null) {
                            Image(
                                bitmap = state.qrCodeBitmap!!.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier
                                    .size(250.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .padding(16.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(250.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.isLoadingQrCode) {
                                    CircularProgressIndicator()
                                } else {
                                    Text(
                                        text = "Failed to load QR Code. Tap to retry.",
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .clickable { viewModel.refreshQrCode(sessionId) }
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Session code with copy button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Session Code: ${state.session!!.sessionCode}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(state.session!!.sessionCode))
                                }
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy Session Code"
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Attendance summary
                AttendanceSummaryCard(
                    presentCount = state.presentCount,
                    totalCount = state.totalStudentsCount
                )

                // Error message
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            // Session not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Session not found",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onNavigateBack
                    ) {
                        Text("Go Back")
                    }
                }
            }
        }
    }

    // End session confirmation dialog
    if (showEndSessionDialog) {
        AlertDialog(
            onDismissRequest = { showEndSessionDialog = false },
            title = { Text("End Session") },
            text = { Text("Are you sure you want to end this attendance session? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.endSession(sessionId)
                        showEndSessionDialog = false
                        onEndSession()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("End Session")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEndSessionDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SessionInfoCard(
    courseName: String,
    sessionType: String,
    startTime: String,
    endTime: String,
    sessionCode: String
) {
    Card(
        modifier = Modifier
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
                text = courseName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (sessionType == "PHYSICAL") Icons.Default.LocationOn else Icons.Default.Laptop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (sessionType == "PHYSICAL") "Physical Session" else "Virtual Session",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Start: $startTime • End: $endTime",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            val remainingTime = calculateRemainingTime(endTime)
            LinearProgressIndicator(
                progress = { remainingTime.second },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            Text(
                text = "Time Remaining: ${remainingTime.first}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun AttendanceSummaryCard(
    presentCount: Int,
    totalCount: Int
) {
    Card(
        modifier = Modifier
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
                text = "Attendance Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceStatItem(
                    title = "Present",
                    count = presentCount,
                    color = MaterialTheme.colorScheme.primary
                )

                AttendanceStatItem(
                    title = "Total Students",
                    count = totalCount,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val attendanceRate = if (totalCount > 0) (presentCount * 100f / totalCount) else 0f
            Text(
                text = "Attendance Rate: ${String.format("%.1f", attendanceRate)}%",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun AttendanceStatItem(
    title: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

// Helper functions
private fun formatDateTimeForDisplay(date: Date): String {
    val formatter = SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault())
    return formatter.format(date)
}

private fun calculateRemainingTime(endTimeStr: String): Pair<String, Float> {
    // Parse the end time from string
    val now = System.currentTimeMillis()
    
    // This is a simplified version - in a real app, you'd parse the actual end time
    val endTime = now + 30 * 60 * 1000 // Assume 30 minutes from now for demo
    
    val remainingMillis = endTime - now
    if (remainingMillis <= 0) {
        return Pair("Expired", 1.0f)
    }
    
    val remainingMinutes = remainingMillis / (60 * 1000)
    val remainingHours = remainingMinutes / 60
    
    val timeString = if (remainingHours > 0) {
        "$remainingHours h ${remainingMinutes % 60} min"
    } else {
        "$remainingMinutes min"
    }
    
    // Calculate progress (0 to 1)
    // Assuming a 2-hour session for this example
    val totalSessionMillis = 2 * 60 * 60 * 1000
    val progress = 1.0f - (remainingMillis.toFloat() / totalSessionMillis).coerceIn(0f, 1f)
    
    return Pair(timeString, progress)
}
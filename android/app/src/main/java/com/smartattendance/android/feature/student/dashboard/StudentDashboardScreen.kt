package com.smartattendance.android.feature.student.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import com.smartattendance.core.model.AttendanceStatus
import com.smartattendance.core.model.ClassSession
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
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
                    containerColor = MaterialTheme.colorScheme.primary,
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
                    onClick = { onNavigateToScanQr() },
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
                    onClick = { onNavigateToHistory() },
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                AttendanceStatsCard(
                    percentage = state.monthlyAttendancePercentage,
                    present = state.classesAttended,
                    absent = state.classesAbsent
                )
            }
            
            item {
                Text(
                    text = "Today's Classes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (state.todayClasses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No classes scheduled for today",
                            color = Color.Gray
                        )
                    }
                }
            } else {
                items(state.todayClasses) { classSession ->
                    ClassSessionCard(
                        classSession = classSession,
                        onMarkAttendance = { 
                            viewModel.onEvent(StudentDashboardEvent.MarkAttendance(classSession.id))
                        }
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun AttendanceStatsCard(
    percentage: Float,
    present: Int,
    absent: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "This Month's Attendance",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Attendance percentage circle
                Box(
                    modifier = Modifier.size(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = percentage / 100f,
                        modifier = Modifier.size(60.dp),
                        color = when {
                            percentage >= 75 -> MaterialTheme.colorScheme.primary
                            percentage >= 50 -> Color(0xFFFFA000)
                            else -> Color(0xFFE53935)
                        },
                        strokeWidth = 6.dp
                    )
                    Text(
                        text = "${percentage.toInt()}%",
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Column {
                    // Progress bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.LightGray)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(percentage / 100f)
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    when {
                                        percentage >= 75 -> MaterialTheme.colorScheme.primary
                                        percentage >= 50 -> Color(0xFFFFA000)
                                        else -> Color(0xFFE53935)
                                    }
                                )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "$present Present / $absent Absent",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClassSessionCard(
    classSession: ClassSession,
    onMarkAttendance: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = classSession.courseName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${classSession.startTime.format(DateTimeFormatter.ofPattern("h:mm a"))} - " +
                        "${classSession.endTime.format(DateTimeFormatter.ofPattern("h:mm a"))}",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                when (classSession.attendanceStatus) {
                    AttendanceStatus.PRESENT -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "Present",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFF388E3C),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    AttendanceStatus.ABSENT -> {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = "Absent",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = Color(0xFFD32F2F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    AttendanceStatus.PENDING -> {
                        Button(
                            onClick = onMarkAttendance,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Mark Attendance",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStudentDashboard() {
    MaterialTheme {
        val mockClasses = listOf(
            ClassSession(
                id = "1",
                courseName = "CS101: Intro to Programming",
                courseId = "CS101",
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 30),
                date = LocalDate.now(),
                attendanceStatus = AttendanceStatus.PRESENT
            ),
            ClassSession(
                id = "2",
                courseName = "MATH202: Calculus II",
                courseId = "MATH202",
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(12, 30),
                date = LocalDate.now(),
                attendanceStatus = AttendanceStatus.ABSENT
            ),
            ClassSession(
                id = "3",
                courseName = "ENG205: Technical Writing",
                courseId = "ENG205",
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(15, 30),
                date = LocalDate.now(),
                attendanceStatus = AttendanceStatus.PENDING
            )
        )
        
        StudentDashboardScreen(
            viewModel = StudentDashboardViewModel(),
            onNavigateToScanQr = {},
            onNavigateToHistory = {}
        )
    }
}
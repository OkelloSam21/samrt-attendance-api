package com.smartattendance.android.feature.admin.coursemanagement

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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseManagementScreen(
    viewModel: CourseManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditCourse: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showAssignLecturerDialog by remember { mutableStateOf(false) }
    var selectedCourseId by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddCourseDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Course"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "All Courses",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
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
                                        text = "No courses found. Add a course to get started.",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.courses) { course ->
                            CourseCard(
                                course = course,
                                onClick = { onEditCourse(course.id) },
                                onAssignLecturer = {
                                    selectedCourseId = course.id
                                    showAssignLecturerDialog = true
                                }
                            )
                        }
                    }

                    // Error message
                    if (state.error != null) {
                        item {
                            ErrorCard(
                                errorMessage = state.error!!,
                                onRetry = { viewModel.loadCourses() }
                            )
                        }
                    }
                }
            }

            // Add Course Dialog
            if (showAddCourseDialog) {
                AddCourseDialog(
                    onDismiss = { showAddCourseDialog = false },
                    onConfirm = { courseName ->
                        viewModel.createCourse(courseName)
                        showAddCourseDialog = false
                    }
                )
            }

            // Assign Lecturer Dialog
            if (showAssignLecturerDialog && selectedCourseId.isNotEmpty()) {
                val course = state.courses.find { it.id == selectedCourseId }
                if (course != null) {
                    AssignLecturerDialog(
                        courseName = course.name,
                        lecturers = state.lecturers,
                        currentLecturerId = course.lecturerId,
                        onDismiss = { 
                            showAssignLecturerDialog = false 
                            selectedCourseId = ""
                        },
                        onConfirm = { lecturerId ->
                            viewModel.assignLecturerToCourse(selectedCourseId, lecturerId)
                            showAssignLecturerDialog = false
                            selectedCourseId = ""
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseCard(
    course: CourseUiModel,
    onClick: () -> Unit,
    onAssignLecturer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = if (course.lecturerName.isNotEmpty()) 
                    "Lecturer: ${course.lecturerName}" 
                else 
                    "No lecturer assigned",
                style = MaterialTheme.typography.bodyMedium,
                color = if (course.lecturerName.isNotEmpty()) 
                    MaterialTheme.colorScheme.onSurface 
                else 
                    MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Created: ${formatDate(course.createdAt)}",
                style = MaterialTheme.typography.bodySmall
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onAssignLecturer,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Assign Lecturer")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var courseName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Course") },
        text = {
            Column {
                Text(
                    text = "Enter course name:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                OutlinedTextField(
                    value = courseName,
                    onValueChange = { 
                        courseName = it
                        isError = it.isBlank()
                    },
                    label = { Text("Course Name") },
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("Course name cannot be empty")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    if (courseName.isNotBlank()) {
                        onConfirm(courseName) 
                    } else {
                        isError = true
                    }
                }
            ) {
                Text("Add Course")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AssignLecturerDialog(
    courseName: String,
    lecturers: List<LecturerUiModel>,
    currentLecturerId: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedLecturerId by remember { mutableStateOf(currentLecturerId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Assign Lecturer to $courseName") },
        text = {
            Column {
                Text(
                    text = "Select a lecturer:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                if (lecturers.isEmpty()) {
                    Text(
                        text = "No lecturers available. Add lecturers first.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    lecturers.forEach { lecturer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLecturerId = lecturer.id }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedLecturerId == lecturer.id,
                                onClick = { selectedLecturerId = lecturer.id }
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Column {
                                Text(
                                    text = lecturer.name,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                
                                Text(
                                    text = lecturer.email,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedLecturerId) },
                enabled = lecturers.isNotEmpty()
            ) {
                Text("Assign")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ErrorCard(
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

// Helper function to format dates
private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}
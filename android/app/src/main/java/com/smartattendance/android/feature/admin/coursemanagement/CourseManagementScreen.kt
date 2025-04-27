package com.smartattendance.android.feature.admin.coursemanagement

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
                    lecturers = state.lecturers,
                    onDismiss = { showAddCourseDialog = false },
                    onConfirm = { courseName, lecturerId, schedules ->
                        viewModel.createCourse(courseName, lecturerId, schedules)
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
    lecturers: List<LecturerUiModel>,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, List<ScheduleUiModel>) -> Unit
) {
    var courseName by remember { mutableStateOf("") }
    var selectedLecturerId by remember { mutableStateOf<String?>(null) }
    var schedules by remember { mutableStateOf(listOf<ScheduleUiModel>()) }
    var isCourseNameError by remember { mutableStateOf(false) }
    var isLecturerExpanded by remember { mutableStateOf(false) }
    var showAddScheduleDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Course") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Course Name
                OutlinedTextField(
                    value = courseName,
                    onValueChange = {
                        courseName = it
                        isCourseNameError = it.isBlank()
                    },
                    label = { Text("Course Name") },
                    isError = isCourseNameError,
                    supportingText = {
                        if (isCourseNameError) {
                            Text("Course name cannot be empty")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lecturer Selection
                ExposedDropdownMenuBox(
                    expanded = isLecturerExpanded,
                    onExpandedChange = { isLecturerExpanded = !isLecturerExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = lecturers.find { it.id == selectedLecturerId }?.name ?: "Select Lecturer (Optional)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Lecturer") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLecturerExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = isLecturerExpanded,
                        onDismissRequest = { isLecturerExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("No Lecturer") },
                            onClick = {
                                selectedLecturerId = null
                                isLecturerExpanded = false
                            }
                        )
                        lecturers.forEach { lecturer ->
                            DropdownMenuItem(
                                text = { Text(lecturer.name) },
                                onClick = {
                                    selectedLecturerId = lecturer.id
                                    isLecturerExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Schedules Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedules",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedButton(
                        onClick = { showAddScheduleDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Schedule"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Schedule")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (schedules.isEmpty()) {
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
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No schedules added yet.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                } else {
                    schedules.forEachIndexed { index, schedule ->
                        ScheduleItem(
                            schedule = schedule,
                            onDelete = {
                                schedules = schedules.filterIndexed { i, _ -> i != index }
                            },
                            onUpdate = { updatedSchedule ->
                                schedules = schedules.toMutableList().also {
                                    it[index] = updatedSchedule
                                }
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (courseName.isNotBlank()) {
                        onConfirm(courseName, selectedLecturerId, schedules)
                    } else {
                        isCourseNameError = courseName.isBlank()
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

    // Add Schedule Dialog
    if (showAddScheduleDialog) {
        AddScheduleDialog(
            onDismiss = { showAddScheduleDialog = false },
            onConfirm = { newSchedule ->
                schedules = schedules + newSchedule
                showAddScheduleDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onConfirm: (ScheduleUiModel) -> Unit
) {
    var day by remember { mutableStateOf("Monday") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }
    var meetingLink by remember { mutableStateOf("") }

    var isDayExpanded by remember { mutableStateOf(false) }
    var isStartTimeError by remember { mutableStateOf(false) }
    var isEndTimeError by remember { mutableStateOf(false) }

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Schedule") },
        text = {
            Column {
                // Day Selection
                ExposedDropdownMenuBox(
                    expanded = isDayExpanded,
                    onExpandedChange = { isDayExpanded = !isDayExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = day,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Day of Week") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDayExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = isDayExpanded,
                        onDismissRequest = { isDayExpanded = false }
                    ) {
                        days.forEach { dayOption ->
                            DropdownMenuItem(
                                text = { Text(dayOption) },
                                onClick = {
                                    day = dayOption
                                    isDayExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Start Time
                OutlinedTextField(
                    value = startTime,
                    onValueChange = {
                        startTime = it
                        isStartTimeError = !isValidTimeFormat(it)
                    },
                    label = { Text("Start Time (HH:MM)") },
                    isError = isStartTimeError,
                    supportingText = {
                        if (isStartTimeError) {
                            Text("Please enter a valid time (HH:MM)")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // End Time
                OutlinedTextField(
                    value = endTime,
                    onValueChange = {
                        endTime = it
                        isEndTimeError = !isValidTimeFormat(it)
                    },
                    label = { Text("End Time (HH:MM)") },
                    isError = isEndTimeError,
                    supportingText = {
                        if (isEndTimeError) {
                            Text("Please enter a valid time (HH:MM)")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Room Number
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    label = { Text("Room Number (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Meeting Link
                OutlinedTextField(
                    value = meetingLink,
                    onValueChange = { meetingLink = it },
                    label = { Text("Meeting Link (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValidTimeFormat(startTime) && isValidTimeFormat(endTime)) {
                        onConfirm(
                            ScheduleUiModel(
                                day = day,
                                startTime = startTime,
                                endTime = endTime,
                                roomNumber = roomNumber,
                                meetingLink = meetingLink
                            )
                        )
                    } else {
                        isStartTimeError = !isValidTimeFormat(startTime)
                        isEndTimeError = !isValidTimeFormat(endTime)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Helper function to validate time format
private fun isValidTimeFormat(time: String): Boolean {
    return time.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleItem(
    schedule: ScheduleUiModel,
    onDelete: () -> Unit,
    onUpdate: (ScheduleUiModel) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${schedule.day}: ${schedule.startTime} - ${schedule.endTime}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Schedule"
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Schedule"
                        )
                    }
                }
            }

            if (schedule.roomNumber.isNotBlank()) {
                Text(
                    text = "Room: ${schedule.roomNumber}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (schedule.meetingLink.isNotBlank()) {
                Text(
                    text = "Meeting: ${schedule.meetingLink}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    if (expanded) {
        AlertDialog(
            onDismissRequest = { expanded = false },
            title = { Text("Edit Schedule") },
            text = {
                Column {
                    var day by remember { mutableStateOf(schedule.day) }
                    var startTime by remember { mutableStateOf(schedule.startTime) }
                    var endTime by remember { mutableStateOf(schedule.endTime) }
                    var roomNumber by remember { mutableStateOf(schedule.roomNumber) }
                    var meetingLink by remember { mutableStateOf(schedule.meetingLink) }

                    // Day of Week
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = day,
                            onValueChange = { day = it },
                            label = { Text("Day") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Start Time
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time (HH:MM)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // End Time
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time (HH:MM)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Room Number
                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it },
                        label = { Text("Room Number (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Meeting Link
                    OutlinedTextField(
                        value = meetingLink,
                        onValueChange = { meetingLink = it },
                        label = { Text("Meeting Link (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { expanded = false }) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                onUpdate(
                                    ScheduleUiModel(
                                        day = day,
                                        startTime = startTime,
                                        endTime = endTime,
                                        roomNumber = roomNumber,
                                        meetingLink = meetingLink
                                    )
                                )
                                expanded = false
                            }
                        ) {
                            Text("Update")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

// Helper function to format dates
private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}
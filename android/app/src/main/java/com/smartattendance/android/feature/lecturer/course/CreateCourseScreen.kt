package com.smartattendance.android.feature.lecturer.course

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartattendance.android.data.network.model.ScheduleRequest
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCourseScreen(
    lecturerId: String,
    onCourseCreated: () -> Unit,
    onBack: () -> Unit,
    viewModel: CreateCourseViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Course") },
                navigationIcon = {
                    IconButton(onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        CreateCurseScreenContent(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
            lecturerId = lecturerId,
            onCourseCreated = onCourseCreated,
            onBack = onBack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCurseScreenContent(
    modifier: Modifier = Modifier,
    viewModel: CreateCourseViewModel,
    lecturerId: String,
    onCourseCreated: () -> Unit,
    onBack: () -> Unit
) {
    val state = viewModel.state
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedScheduleIndex by remember { mutableStateOf(-1) }
    var isStartTime by remember { mutableStateOf(true) }
    var roomNumber by remember { mutableStateOf("") }
    var meetingLink by remember { mutableStateOf("") }
    val timePickerState = rememberTimePickerState()
    val daysOfWeek = listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
    var selectedDay by remember { mutableStateOf(daysOfWeek[0]) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.courseName,
            onValueChange = { viewModel.onCourseNameChange(it) },
            label = { Text("Course Name") },
            modifier = Modifier.fillMaxWidth(),
            isError = state.isCourseNameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Schedule Input
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = roomNumber,
                    onValueChange = { roomNumber = it },
                    label = { Text("Room Number") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(0.1f))
                OutlinedTextField(
                    value = meetingLink,
                    onValueChange = { meetingLink = it },
                    label = { Text("Meeting Link") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedDay,
                    onValueChange = { },
                    label = { Text("Day of Week") },
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { isDropdownExpanded = true }) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    }
                )
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    daysOfWeek.forEach { day ->
                        DropdownMenuItem(
                            text = { Text(day) },
                            onClick = {
                                selectedDay = day
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(0.1f))
                Button(
                    onClick = {
                        isStartTime = true
                        showTimePicker = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Start Time")
                }
                Spacer(modifier = Modifier.weight(0.1f))
                Button(
                    onClick = {
                        isStartTime = false
                        showTimePicker = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("End Time")
                }
            }
            Button(onClick = {
                val startTime = formatTime(timePickerState.hour, timePickerState.minute)
                val endTime = formatTime(timePickerState.hour, timePickerState.minute)
                viewModel.addSchedule(
                    ScheduleRequest(
                        dayOfWeek = selectedDay,
                        startTime = startTime,
                        endTime = endTime,
                        roomNumber = roomNumber,
                        meetingLink = meetingLink
                    )
                )
                roomNumber = ""
                meetingLink = ""
            }) {
                Text("Add Schedule")
            }
            LazyColumn {
                items(viewModel.schedules) { schedule ->
                    Text(text = "${schedule.dayOfWeek} - ${schedule.startTime} - ${schedule.endTime} - ${schedule.roomNumber} - ${schedule.meetingLink}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.createCourse(lecturerId) {
                    onCourseCreated()
                    viewModel.resetState()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Course")
        }

        if (viewModel.errorMessage != null) {
            Text(text = viewModel.errorMessage!!)
        }
    }
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(text = "Select Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minute)
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return dateFormat.format(calendar.time)
}
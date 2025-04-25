package com.smartattendance.android.feature.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeedCoursesScreen(
    viewModel: SeedCoursesViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Seed Courses") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add Multiple Courses",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                
                Text(
                    text = "Add one course name per line. Courses will be created without an assigned lecturer.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Course list
                CourseInputList(
                    courseNames = state.courseNames,
                    onCourseNameChanged = { index, name -> 
                        viewModel.onEvent(SeedCoursesEvent.CourseNameChanged(index, name))
                    },
                    onAddCourse = { viewModel.onEvent(SeedCoursesEvent.AddCourse) },
                    onRemoveCourse = { index -> 
                        viewModel.onEvent(SeedCoursesEvent.RemoveCourse(index))
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Button(
                    onClick = { viewModel.onEvent(SeedCoursesEvent.SeedCourses) },
                    enabled = !state.isLoading && state.courseNames.any { it.isNotBlank() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Create Courses")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedButton(
                    onClick = { viewModel.onEvent(SeedCoursesEvent.ClearAll) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear All")
                }
                
                // Success message
                if (state.successMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = state.successMessage ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                // Error message
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = state.error ?: "",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseInputList(
    courseNames: List<String>,
    onCourseNameChanged: (Int, String) -> Unit,
    onAddCourse: () -> Unit,
    onRemoveCourse: (Int) -> Unit
) {
    courseNames.forEachIndexed { index, courseName ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = courseName,
                onValueChange = { onCourseNameChanged(index, it) },
                label = { Text("Course ${index + 1}") },
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = { onRemoveCourse(index) },
                enabled = courseNames.size > 1
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove course"
                )
            }
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            onClick = onAddCourse
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add course"
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Another Course")
        }
    }
}
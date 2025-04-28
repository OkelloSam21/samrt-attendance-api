package com.smartattendance.android.feature.lecturer.createsession

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartattendance.android.data.network.model.SessionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAttendanceSessionScreen(
    viewModel: CreateSessionViewModel = hiltViewModel(),
    courseId: String,
    onNavigateBack: () -> Unit,
    onSessionCreated: () -> Unit
) {
    LaunchedEffect(courseId) {
        viewModel.setCourseId(courseId)
        viewModel.loadCourseDetails()

        Log.e("Create session ui", "course id : $courseId")
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSessionCreated()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Attendance Session") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        CreateSessionContent(
            state = state,
            onEvent = viewModel::onEvent,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun CreateSessionContent(
    state: CreateSessionUiState,
    onEvent: (CreateSessionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Course information
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Course: ${state.courseName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Session Type
            SessionTypeSelector(
                selected = state.sessionType,
                onTypeSelected = { type ->
                    onEvent(CreateSessionEvent.SessionTypeChanged(type))
                }
            )

            // Duration
            DurationSelector(
                duration = state.durationMinutes,
                onDurationChanged = { duration ->
                    onEvent(CreateSessionEvent.DurationChanged(duration))
                },
                error = state.durationError
            )

            // Location settings (for physical sessions)
            if (state.sessionType == SessionType.PHYSICAL.name) {
                LocationSettings(
                    useCurrentLocation = state.useCurrentLocation,
                    latitude = state.latitude,
                    longitude = state.longitude,
                    radius = state.radiusMeters,
                    onUseCurrentLocationChanged = { use ->
                        onEvent(CreateSessionEvent.UseCurrentLocationChanged(use))
                    },
                    onLatitudeChanged = { lat ->
                        onEvent(CreateSessionEvent.LatitudeChanged(lat))
                    },
                    onLongitudeChanged = { lng ->
                        onEvent(CreateSessionEvent.LongitudeChanged(lng))
                    },
                    onRadiusChanged = { rad ->
                        onEvent(CreateSessionEvent.RadiusChanged(rad))
                    },
                    locationError = state.locationError,
                    radiusError = state.radiusError
                )
            }

            // Create button
            Button(
                onClick = { onEvent(CreateSessionEvent.CreateSession) },
                enabled = state.isFormValid && !state.isCreating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isCreating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Create Session")
                }
            }

            // Error message
            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SessionTypeSelector(
    selected: String,
    onTypeSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Session Type",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Physical session option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == SessionType.PHYSICAL.name,
                    onClick = { onTypeSelected(SessionType.PHYSICAL.name) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Physical",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "For in-person classes with location verification",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Virtual session option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == SessionType.VIRTUAL.name,
                    onClick = { onTypeSelected(SessionType.VIRTUAL.name) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Virtual",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "For online classes without location verification",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun DurationSelector(
    duration: String,
    onDurationChanged: (String) -> Unit,
    error: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Session Duration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = duration,
                    onValueChange = onDurationChanged,
                    label = { Text("Duration (minutes)") },
                    isError = error != null,
                    supportingText = {
                        if (error != null) {
                            Text(error)
                        } else {
                            Text("Recommended: 30-120 minutes")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun LocationSettings(
    useCurrentLocation: Boolean,
    latitude: String,
    longitude: String,
    radius: String,
    onUseCurrentLocationChanged: (Boolean) -> Unit,
    onLatitudeChanged: (String) -> Unit,
    onLongitudeChanged: (String) -> Unit,
    onRadiusChanged: (String) -> Unit,
    locationError: String?,
    radiusError: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Location Settings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Use current location toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Use current location",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = useCurrentLocation,
                    onCheckedChange = onUseCurrentLocationChanged
                )
            }

            // Manual coordinates input (when not using current location)
            if (!useCurrentLocation) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = onLatitudeChanged,
                        label = { Text("Latitude") },
                        isError = locationError != null,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = longitude,
                        onValueChange = onLongitudeChanged,
                        label = { Text("Longitude") },
                        isError = locationError != null,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (locationError != null) {
                    Text(
                        text = locationError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Radius input
            OutlinedTextField(
                value = radius,
                onValueChange = onRadiusChanged,
                label = { Text("Radius (meters)") },
                isError = radiusError != null,
                supportingText = {
                    if (radiusError != null) {
                        Text(radiusError)
                    } else {
                        Text("Recommended: 50-200 meters")
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}
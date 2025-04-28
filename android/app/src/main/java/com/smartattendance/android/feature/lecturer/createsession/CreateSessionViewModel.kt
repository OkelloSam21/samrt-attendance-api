package com.smartattendance.android.feature.lecturer.createsession

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.data.network.model.SessionType
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSessionViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateSessionUiState())
    val uiState: StateFlow<CreateSessionUiState> = _uiState.asStateFlow()

    private var courseId: String = ""

    // In CreateSessionViewModel.kt
    fun setCourseId(id: String) {
        if (id.isBlank() || id == "{courseId}") { // Check for placeholder values
            _uiState.update {
                it.copy(
                    error = "Invalid course ID format",
                    isLoading = false
                )
            }
            Log.e("ViewModel", "Invalid ID received: $id")
            return
        }

        courseId = id
        Log.d("ViewModel", "Valid course ID set: $id")
    }
    init {
        loadCourseDetails()
    }

    fun loadCourseDetails() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val course = courseRepository.getCourseById(courseId).firstOrNull()
                Log.e("Create session", "course $course")
                if (course != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            courseName = course.name
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Course not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error loading course: ${e.message}"
                    )
                }
            }
        }
    }

    private fun fetchCourseById(id: String) {
        viewModelScope.launch {
            courseRepository.getCourseById(id).collect { course ->
                if (course != null) {
                    _uiState.update {
                        it.copy(
                            courseName = course.name,
                            isLoading = false,
                            error = ""
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: CreateSessionEvent) {
        when (event) {
            is CreateSessionEvent.SessionTypeChanged -> {
                _uiState.update {
                    it.copy(
                        sessionType = event.type
                    )
                }
                validateForm()
            }

            is CreateSessionEvent.DurationChanged -> {
                _uiState.update {
                    it.copy(
                        durationMinutes = event.minutes
                    )
                }
                validateDuration()
                validateForm()
            }

            is CreateSessionEvent.UseCurrentLocationChanged -> {
                _uiState.update {
                    it.copy(
                        useCurrentLocation = event.use
                    )
                }
                validateForm()
            }

            is CreateSessionEvent.LatitudeChanged -> {
                _uiState.update {
                    it.copy(
                        latitude = event.latitude
                    )
                }
                validateLocation()
                validateForm()
            }

            is CreateSessionEvent.LongitudeChanged -> {
                _uiState.update {
                    it.copy(
                        longitude = event.longitude
                    )
                }
                validateLocation()
                validateForm()
            }

            is CreateSessionEvent.RadiusChanged -> {
                _uiState.update {
                    it.copy(
                        radiusMeters = event.radius
                    )
                }
                validateRadius()
                validateForm()
            }

            is CreateSessionEvent.CreateSession -> {
                createSession()
            }
        }
    }

    private fun validateDuration() {
        val durationError = try {
            val duration = _uiState.value.durationMinutes.toInt()
            if (duration < 5) {
                "Duration must be at least 5 minutes"
            } else if (duration > 180) {
                "Duration cannot exceed 180 minutes (3 hours)"
            } else {
                null
            }
        } catch (e: NumberFormatException) {
            "Please enter a valid number"
        }

        _uiState.update {
            it.copy(
                durationError = durationError
            )
        }
    }

    private fun validateLocation() {
        if (_uiState.value.sessionType != SessionType.PHYSICAL.name || _uiState.value.useCurrentLocation) {
            _uiState.update {
                it.copy(
                    locationError = null
                )
            }
            return
        }

        try {
            val latitude = _uiState.value.latitude.toDoubleOrNull()
            val longitude = _uiState.value.longitude.toDoubleOrNull()

            val locationError = when {
                latitude == null || longitude == null -> "Please enter valid coordinates"
                latitude < -90 || latitude > 90 -> "Latitude must be between -90 and 90"
                longitude < -180 || longitude > 180 -> "Longitude must be between -180 and 180"
                else -> null
            }

            _uiState.update {
                it.copy(
                    locationError = locationError
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    locationError = "Please enter valid coordinates"
                )
            }
        }
    }

    private fun validateRadius() {
        if (_uiState.value.sessionType != SessionType.PHYSICAL.name) {
            return
        }

        val radiusError = try {
            val radius = _uiState.value.radiusMeters.toInt()
            if (radius < 10) {
                "Radius must be at least 10 meters"
            } else if (radius > 1000) {
                "Radius cannot exceed 1000 meters (1 km)"
            } else {
                null
            }
        } catch (e: NumberFormatException) {
            "Please enter a valid number"
        }

        _uiState.update {
            it.copy(
                radiusError = radiusError
            )
        }
    }

    private fun validateForm() {
        val state = _uiState.value

        // Basic validation
        val isDurationValid = state.durationError == null && state.durationMinutes.isNotEmpty()

        // Location validation for physical sessions
        val isLocationValid = if (state.sessionType == SessionType.PHYSICAL.name) {
            if (state.useCurrentLocation) {
                true
            } else {
                state.locationError == null &&
                        state.latitude.isNotEmpty() &&
                        state.longitude.isNotEmpty()
            }
        } else {
            true
        }

        // Radius validation for physical sessions
        val isRadiusValid = if (state.sessionType == SessionType.PHYSICAL.name) {
            state.radiusError == null && state.radiusMeters.isNotEmpty()
        } else {
            true
        }

        _uiState.update {
            it.copy(
                isFormValid = isDurationValid && isLocationValid && isRadiusValid
            )
        }
    }

    private fun createSession() {
        val state = _uiState.value

        if (!state.isFormValid) {
            return
        }

        _uiState.update {
            it.copy(
                isCreating = true,
                error = null
            )
        }

        viewModelScope.launch {
            try {
                val durationMinutes = state.durationMinutes.toInt()

                // Prepare location data for physical sessions
                val latitude = if (state.sessionType == SessionType.PHYSICAL.name) {
                    if (state.useCurrentLocation) {
                        // In a real app, you would get this from a location service
                        37.4220 // Example latitude (Google HQ)
                    } else {
                        state.latitude.toDoubleOrNull()
                    }
                } else {
                    null
                }

                val longitude = if (state.sessionType == SessionType.PHYSICAL.name) {
                    if (state.useCurrentLocation) {
                        // In a real app, you would get this from a location service
                        -122.0841 // Example longitude (Google HQ)
                    } else {
                        state.longitude.toDoubleOrNull()
                    }
                } else {
                    null
                }

                val radiusMeters = if (state.sessionType == SessionType.PHYSICAL.name) {
                    state.radiusMeters.toIntOrNull()
                } else {
                    null
                }

                val result = attendanceRepository.createAttendanceSession(
                    courseId = courseId,
                    durationMinutes = durationMinutes,
                    sessionType = state.sessionType,
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = radiusMeters
                )

                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            isSuccess = true,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isCreating = false,
                            error = result.exceptionOrNull()?.message ?: "Failed to create session"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreating = false,
                        error = "Error creating session: ${e.message}"
                    )
                }
            }
        }
    }
}

data class CreateSessionUiState(
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    // Course information
    val courseName: String = "",

    // Session settings
    val sessionType: String = SessionType.PHYSICAL.name,
    val durationMinutes: String = "60",
    val durationError: String? = null,

    // Location settings (for physical sessions)
    val useCurrentLocation: Boolean = true,
    val latitude: String = "",
    val longitude: String = "",
    val radiusMeters: String = "50",
    val locationError: String? = null,
    val radiusError: String? = null,

    // Form state
    val isFormValid: Boolean = true
)

sealed class CreateSessionEvent {
    data class SessionTypeChanged(val type: String) : CreateSessionEvent()
    data class DurationChanged(val minutes: String) : CreateSessionEvent()
    data class UseCurrentLocationChanged(val use: Boolean) : CreateSessionEvent()
    data class LatitudeChanged(val latitude: String) : CreateSessionEvent()
    data class LongitudeChanged(val longitude: String) : CreateSessionEvent()
    data class RadiusChanged(val radius: String) : CreateSessionEvent()
    object CreateSession : CreateSessionEvent()
}
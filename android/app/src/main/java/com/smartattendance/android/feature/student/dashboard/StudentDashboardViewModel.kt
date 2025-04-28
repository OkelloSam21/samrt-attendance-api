package com.smartattendance.android.feature.student.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.CourseRepository
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class StudentDashboardViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun onEvent(event: StudentDashboardEvent) {
        when (event) {
            is StudentDashboardEvent.RefreshDashboard -> {
                loadDashboardData()
            }
            is StudentDashboardEvent.MarkAttendance -> {
                markAttendance(event.sessionCode, event.latitude, event.longitude)
            }
        }
    }

    private fun loadDashboardData() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val userId = userPreferencesRepository.userId.firstOrNull() ?: ""

                // Load active attendance sessions
                attendanceRepository.getActiveAttendanceSessions()
                    .combine(courseRepository.getAllCourses()) { sessions, courses ->
                        // Map sessions to display data
                        sessions.map { session ->
                            val course = courses.find { it.id == session.course_id }

                            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                Locale.getDefault())
                            val startTime = try {
                                dateFormat.parse(session.createdAt)
                            } catch (e: Exception) {
                                Date()
                            }
                            val endTime = try {
                                dateFormat.parse(session.expiresAt)
                            } catch (e: Exception) {
                                Date()
                            }
                            SessionDisplayData(
                                id = session.id,
                                courseName = course?.name ?: "Unknown Course",
                                lecturerName = course?.lecturerName ?: "Unknown Lecturer",
                                startTime = startTime,
                                endTime = endTime
                            )
                        }
                    }
                    .catch { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to load sessions: ${e.message}"
                            )
                        }
                    }
                    .collect { sessionDisplayList ->
                        // Calculate attendance statistics
                        val totalAttendedSessions = calculateAttendedSessions(userId)
                        val totalSessions = sessionDisplayList.size + totalAttendedSessions.classesAttended + totalAttendedSessions.classesAbsent

                        val attendancePercentage = if (totalSessions > 0) {
                            (totalAttendedSessions.classesAttended.toFloat() / totalSessions) * 100
                        } else {
                            0f
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                todayClasses = sessionDisplayList,
                                monthlyAttendancePercentage = attendancePercentage,
                                classesAttended = totalAttendedSessions.classesAttended,
                                classesAbsent = totalAttendedSessions.classesAbsent
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load dashboard: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun calculateAttendedSessions(userId: String): AttendanceStats {
        // This is a placeholder implementation
        // In a real app, you'd fetch this data from the repository
        return AttendanceStats(
            classesAttended = 12,
            classesAbsent = 3
        )
    }

    private fun markAttendance(sessionCode: String, latitude: Double? = null, longitude: Double? = null) {
        _uiState.update { it.copy(isMarking = true) }

        viewModelScope.launch {
            try {
                val result = attendanceRepository.markAttendance(
                    sessionCode = sessionCode,
                    latitude = latitude,
                    longitude = longitude
                )

                if (result.isSuccess) {
                    // Refresh dashboard data after marking attendance
                    loadDashboardData()
                } else {
                    _uiState.update {
                        it.copy(
                            isMarking = false,
                            error = result.exceptionOrNull()?.message ?: "Failed to mark attendance"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isMarking = false,
                        error = e.message ?: "An unknown error occurred"
                    )
                }
            }
        }
    }
}

data class StudentDashboardUiState(
    val isLoading: Boolean = false,
    val isMarking: Boolean = false,
    val error: String? = null,
    val todayClasses: List<SessionDisplayData> = emptyList(),
    val monthlyAttendancePercentage: Float = 0f,
    val classesAttended: Int = 0,
    val classesAbsent: Int = 0
)

sealed class StudentDashboardEvent {
    data class MarkAttendance(
        val sessionCode: String,
        val latitude: Double? = null,
        val longitude: Double? = null
    ) : StudentDashboardEvent()

    object RefreshDashboard : StudentDashboardEvent()
}

data class AttendanceStats(
    val classesAttended: Int,
    val classesAbsent: Int
)


data class SessionDisplayData(
    val id: String,
    val courseName: String,
    val lecturerName: String,
    val startTime: Date,
    val endTime: Date
)
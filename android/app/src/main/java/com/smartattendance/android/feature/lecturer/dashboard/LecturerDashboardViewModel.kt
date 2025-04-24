package com.smartattendance.android.feature.lecturer.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.model.Attendance
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.CourseRepository
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LecturerDashboardViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LecturerDashboardUiState())
    val uiState: StateFlow<LecturerDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val userId = userPreferencesRepository.userId.firstOrNull() ?: ""

                // Load courses taught by this lecturer
                courseRepository.getCoursesByLecturerId(userId)
                    .catch { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to load courses: ${e.message}"
                            )
                        }
                    }
                    .collect { courses ->
                        val courseDataList = courses.map { course ->
                            CourseData(
                                id = course.id,
                                name = course.name,
                                createdAt = course.createdAt
                            )
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                courses = courseDataList
                            )
                        }

                        // Now load active sessions
                        loadActiveSessions(userId, courseDataList)
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

    private fun loadActiveSessions(lecturerId: String, courses: List<CourseData>) {
        viewModelScope.launch {
            try {
                attendanceRepository.getAttendanceSessionsByLecturerId(lecturerId)
                    .catch { e ->
                        _uiState.update {
                            it.copy(
                                error = "Failed to load active sessions: ${e.message}"
                            )
                        }
                    }
                    .collect { sessions ->
                        // Filter for active sessions only
                        val activeSessionsList = sessions
//                            .filter { it.isActive }
                            .map { session ->
                                val course = courses.find { it.id == session.course_id }
                                val attendanceCount = 0 // This would need to be fetched from the repository

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

                                SessionData(
                                    id = session.id,
                                    courseId = session.course_id,
                                    courseName = course?.name ?: "Unknown Course",
                                    sessionCode = session.session_code,
                                    startTime = startTime,
                                    endTime = endTime,
                                    attendanceCount = attendanceCount
                                )
                            }

                        _uiState.update {
                            it.copy(
                                activeSessions = activeSessionsList
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Failed to load sessions: ${e.message}"
                    )
                }
            }
        }
    }

    fun createSession(courseId: String, durationMinutes: Int, sessionType: String) {
        _uiState.update { it.copy(isCreatingSession = true) }

        viewModelScope.launch {
            try {
                val result = attendanceRepository.createAttendanceSession(
                    courseId = courseId,
                    durationMinutes = durationMinutes,
                    sessionType = sessionType
                )

                if (result.isSuccess) {
                    // Refresh data after creating a session
                    loadDashboardData()
                } else {
                    _uiState.update {
                        it.copy(
                            isCreatingSession = false,
                            error = result.exceptionOrNull()?.message ?: "Failed to create session"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingSession = false,
                        error = e.message ?: "An unknown error occurred"
                    )
                }
            }
        }
    }
}

data class LecturerDashboardUiState(
    val isLoading: Boolean = false,
    val isCreatingSession: Boolean = false,
    val error: String? = null,
    val courses: List<CourseData> = emptyList(),
    val activeSessions: List<SessionData> = emptyList()
)

data class CourseData(
    val id: String,
    val name: String,
    val createdAt: Date
)

data class SessionData(
    val id: String,
    val courseId: String,
    val courseName: String,
    val sessionCode: String,
    val startTime: Date,
    val endTime: Date,
    val attendanceCount: Int
)
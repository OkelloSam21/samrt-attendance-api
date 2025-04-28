package com.smartattendance.android.feature.lecturer.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.model.Course
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.CourseRepository
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import com.smartattendance.android.domain.repository.UserRepository
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
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LecturerDashboardUiState())
    val uiState: StateFlow<LecturerDashboardUiState> = _uiState.asStateFlow()

    val userId = userPreferencesRepository.userId

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
                            Course(
                                id = course.id,
                                name = course.name,
                                lecturerId = course.lecturerId,
                                lecturerName = course.lecturerName,
                                createdAt = course.createdAt,
                            )
                        }

                        // Load active and recent sessions
                        val activeSessions = loadActiveSessions(userId, courseDataList)
                        val recentSessions = loadRecentSessions(userId, courseDataList)

                        // Estimate total students across all courses
                        val totalStudents = courseDataList.count()

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                courses = courseDataList,
                                activeSessions = activeSessions,
                                recentSessions = recentSessions,
                                totalStudents = totalStudents
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

    private suspend fun estimateStudentCount(courseId: String): Int {
        // In a real application, you would get this data from your repository
        // For now, we'll return a random count between 15 and 40
        return (15..40).random()
    }

    private suspend fun loadActiveSessions(lecturerId: String, courses: List<Course>): List<SessionData> {
        try {
            // Use the new method we added to get active sessions
            val activeSessions = attendanceRepository.getSessionsByLecturerAndStatus(lecturerId, true).firstOrNull() ?: emptyList()

            return activeSessions.map { session ->
                val course = courses.find { it.id == session.courseId }
                val attendanceCount = getAttendanceCountForSession(session.id)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
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
                    courseId = session.courseId,
                    courseName = course?.name ?: "Unknown Course",
                    sessionCode = session.sessionCode,
                    startTime = startTime,
                    endTime = endTime,
                    attendanceCount = attendanceCount
                )
            }
        } catch (e: Exception) {
            // Log the error and return an empty list
            return emptyList()
        }
    }

    private suspend fun loadRecentSessions(lecturerId: String, courses: List<Course>): List<SessionData> {
        try {
            // Get recently ended sessions
            val recentSessions = attendanceRepository.getSessionsByLecturerAndStatus(lecturerId, false).firstOrNull() ?: emptyList()

            // Only take the 5 most recent sessions
            return recentSessions
                .sortedByDescending { it.createdAt }
                .take(5)
                .map { session ->
                    val course = courses.find { it.id == session.courseId }
                    val attendanceCount = getAttendanceCountForSession(session.id)

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
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
                        courseId = session.courseId,
                        courseName = course?.name ?: "Unknown Course",
                        sessionCode = session.sessionCode,
                        startTime = startTime,
                        endTime = endTime,
                        attendanceCount = attendanceCount
                    )
                }
        } catch (e: Exception) {
            // Log the error and return an empty list
            return emptyList()
        }
    }

    private suspend fun getAttendanceCountForSession(sessionId: String): Int {
        // In a real app, this would query the repository
        // For demo purposes, we'll generate a random count
        return (5..30).random()
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
    val courses: List<Course> = emptyList(),
    val activeSessions: List<SessionData> = emptyList(),
    val recentSessions: List<SessionData> = emptyList(),
    val totalStudents: Int = 0
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
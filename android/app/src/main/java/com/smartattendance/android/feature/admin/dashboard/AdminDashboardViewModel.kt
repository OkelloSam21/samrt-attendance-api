package com.smartattendance.android.feature.admin.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.CourseRepository
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val attendanceRepository: AttendanceRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // In a real app, these would be fetched from repositories
                // For this example, we'll use mock data
                
                // Mock statistics
                val totalStudents = 120
                val totalLecturers = 15
                val totalCourses = 25
                val totalAttendanceSessions = 320
                
                // Mock recent users
                val recentUsers = createMockUsers()
                
                // Mock recent courses
                val recentCourses = createMockCourses()
                
                // Mock recent activities
                val recentActivities = createMockActivities()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        totalStudents = totalStudents,
                        totalLecturers = totalLecturers,
                        totalCourses = totalCourses,
                        totalAttendanceSessions = totalAttendanceSessions,
                        recentUsers = recentUsers,
                        recentCourses = recentCourses,
                        recentActivities = recentActivities
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load dashboard data: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadUsers() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // In a real app, this would fetch from a repository
                val users = createMockUsers()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        recentUsers = users
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load users: ${e.message}"
                    )
                }
            }
        }
    }

    fun loadCourses() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // In a real app, this would fetch from a repository
                val courses = createMockCourses()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        recentCourses = courses
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load courses: ${e.message}"
                    )
                }
            }
        }
    }

    // Mock data helpers
    private fun createMockUsers(): List<UserData> {
        return listOf(
            UserData(
                id = "1",
                name = "John Doe",
                email = "john.doe@example.com",
                role = "STUDENT",
                registrationDate = Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L)
            ),
            UserData(
                id = "2",
                name = "Jane Smith",
                email = "jane.smith@example.com",
                role = "STUDENT",
                registrationDate = Date(System.currentTimeMillis() - 45 * 24 * 60 * 60 * 1000L)
            ),
            UserData(
                id = "3",
                name = "Prof. Robert Johnson",
                email = "robert.johnson@example.com",
                role = "LECTURER",
                registrationDate = Date(System.currentTimeMillis() - 60 * 24 * 60 * 60 * 1000L)
            ),
            UserData(
                id = "4",
                name = "Dr. Sarah Williams",
                email = "sarah.williams@example.com",
                role = "LECTURER",
                registrationDate = Date(System.currentTimeMillis() - 90 * 24 * 60 * 60 * 1000L)
            ),
            UserData(
                id = "5",
                name = "Admin User",
                email = "admin@example.com",
                role = "ADMIN",
                registrationDate = Date(System.currentTimeMillis() - 120 * 24 * 60 * 60 * 1000L)
            )
        )
    }

    private fun createMockCourses(): List<CourseData> {
        return listOf(
            CourseData(
                id = "1",
                name = "Introduction to Computer Science",
                lecturerName = "Prof. Robert Johnson",
                createdAt = Date(System.currentTimeMillis() - 60 * 24 * 60 * 60 * 1000L),
                studentCount = 45
            ),
            CourseData(
                id = "2",
                name = "Data Structures and Algorithms",
                lecturerName = "Dr. Sarah Williams",
                createdAt = Date(System.currentTimeMillis() - 45 * 24 * 60 * 60 * 1000L),
                studentCount = 38
            ),
            CourseData(
                id = "3",
                name = "Database Management Systems",
                lecturerName = "Prof. Robert Johnson",
                createdAt = Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L),
                studentCount = 42
            ),
            CourseData(
                id = "4",
                name = "Software Engineering",
                lecturerName = "Dr. Sarah Williams",
                createdAt = Date(System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000L),
                studentCount = 35
            ),
            CourseData(
                id = "5",
                name = "Mobile App Development",
                lecturerName = "Prof. Alan Turing",
                createdAt = Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L),
                studentCount = 28
            )
        )
    }

    private fun createMockActivities(): List<ActivityData> {
        return listOf(
            ActivityData(
                id = "1",
                type = ActivityType.USER_CREATED,
                description = "New student registered: Mark Wilson",
                timestamp = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L)
            ),
            ActivityData(
                id = "2",
                type = ActivityType.COURSE_CREATED,
                description = "New course created: Artificial Intelligence",
                timestamp = Date(System.currentTimeMillis() - 5 * 60 * 60 * 1000L)
            ),
            ActivityData(
                id = "3",
                type = ActivityType.ATTENDANCE_STARTED,
                description = "Attendance session started for Data Structures and Algorithms",
                timestamp = Date(System.currentTimeMillis() - 6 * 60 * 60 * 1000L)
            ),
            ActivityData(
                id = "4",
                type = ActivityType.USER_CREATED,
                description = "New lecturer registered: Dr. Emily Carter",
                timestamp = Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L)
            ),
            ActivityData(
                id = "5",
                type = ActivityType.ATTENDANCE_COMPLETED,
                description = "Attendance session completed for Mobile App Development",
                timestamp = Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L)
            )
        )
    }
}

data class AdminDashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val totalStudents: Int = 0,
    val totalLecturers: Int = 0,
    val totalCourses: Int = 0,
    val totalAttendanceSessions: Int = 0,
    val recentUsers: List<UserData> = emptyList(),
    val recentCourses: List<CourseData> = emptyList(),
    val recentActivities: List<ActivityData> = emptyList()
)

data class UserData(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val registrationDate: Date
)

data class CourseData(
    val id: String,
    val name: String,
    val lecturerName: String,
    val createdAt: Date,
    val studentCount: Int
)

data class ActivityData(
    val id: String,
    val type: ActivityType,
    val description: String,
    val timestamp: Date
)

enum class ActivityType(
    val icon: ImageVector,
    val color: Color
) {
    USER_CREATED(Icons.Default.PersonAdd, Color(0xFF2196F3)), // Blue
    COURSE_CREATED(Icons.Default.AddBox, Color(0xFF4CAF50)), // Green
    ATTENDANCE_STARTED(Icons.Default.Timer, Color(0xFFFFC107)), // Yellow/Amber
    ATTENDANCE_COMPLETED(Icons.Default.CheckCircle, Color(0xFF9C27B0)), // Purple
    SYSTEM_UPDATE(Icons.Default.Settings, Color(0xFF607D8B)) // Blue Grey
}
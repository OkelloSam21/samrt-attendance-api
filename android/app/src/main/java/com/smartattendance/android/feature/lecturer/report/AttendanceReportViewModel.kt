package com.smartattendance.android.feature.lecturer.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.CourseRepository
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import com.smartattendance.android.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AttendanceReportViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceReportUiState())
    val uiState: StateFlow<AttendanceReportUiState> = _uiState.asStateFlow()

    fun loadReportData(courseId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // Load course info
                val course = courseRepository.getCourseById(courseId).firstOrNull()
                if (course == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Course not found"
                        )
                    }
                    return@launch
                }
                
                // Load session data
                val sessions = attendanceRepository.getAttendanceSessionsByCourseId(courseId).firstOrNull() ?: emptyList()
                
                // In a real implementation, we would calculate these statistics from actual data
                // For this example, we'll generate mock data
                val sessionSummaries = generateMockSessionSummaries(sessions.size)
                val studentAttendances = generateMockStudentAttendances(20) // Assume 20 students
                val analytics = generateMockAnalytics()
                
                // Calculate average attendance percentage
                val averageAttendance = if (sessionSummaries.isNotEmpty()) {
                    sessionSummaries.sumOf { it.attendancePercentage } / sessionSummaries.size
                } else {
                    0
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        courseName = course.name,
                        sessions = sessionSummaries,
                        students = studentAttendances,
                        analytics = analytics,
                        averageAttendancePercentage = averageAttendance,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error loading report data: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun generateMockSessionSummaries(count: Int): List<SessionSummary> {
        val summaries = mutableListOf<SessionSummary>()
        val random = Random(System.currentTimeMillis())
        val calendar = Calendar.getInstance()
        
        // Generate sessions over the past few weeks
        for (i in 0 until count.coerceAtLeast(10)) {
            calendar.add(Calendar.DAY_OF_MONTH, -2)
            val date = calendar.time
            
            val totalStudents = 25 + random.nextInt(10)
            val presentCount = (totalStudents * (0.6 + random.nextDouble() * 0.4)).toInt()
            val attendancePercentage = (presentCount * 100 / totalStudents)
            
            summaries.add(
                SessionSummary(
                    id = "session_${i}",
                    sessionCode = "CODE${100 + i}",
                    sessionType = if (random.nextBoolean()) "PHYSICAL" else "VIRTUAL",
                    date = date,
                    presentCount = presentCount,
                    totalStudents = totalStudents,
                    attendancePercentage = attendancePercentage
                )
            )
        }
        
        return summaries
    }
    
    private fun generateMockStudentAttendances(count: Int): List<StudentAttendance> {
        val students = mutableListOf<StudentAttendance>()
        val random = Random(System.currentTimeMillis())
        
        for (i in 0 until count) {
            val totalSessions = 15 // Assume 15 total sessions
            val sessionsAttended = (totalSessions * (0.5 + random.nextDouble() * 0.5)).toInt()
            val attendancePercentage = (sessionsAttended * 100 / totalSessions)
            
            students.add(
                StudentAttendance(
                    id = "STU${1000 + i}",
                    name = "Student ${i + 1}",
                    sessionsAttended = sessionsAttended,
                    totalSessions = totalSessions,
                    attendancePercentage = attendancePercentage
                )
            )
        }
        
        return students.sortedByDescending { it.attendancePercentage }
    }
    
    private fun generateMockAnalytics(): AttendanceAnalytics {
        val random = Random(System.currentTimeMillis())
        
        // Generate weekly data
        val weeklyData = mutableListOf<WeeklyAttendance>()
        for (week in 1..10) {
            weeklyData.add(
                WeeklyAttendance(
                    weekNumber = week,
                    percentage = 60 + random.nextInt(40)
                )
            )
        }
        
        // Generate daily trend data
        val trendData = mutableListOf<DailyAttendance>()
        val calendar = Calendar.getInstance()
        
        for (i in 0 until 30) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            trendData.add(
                DailyAttendance(
                    date = calendar.time,
                    percentage = 60 + random.nextInt(40)
                )
            )
        }
        
        val overallRate = 75 + random.nextInt(15)
        val presentPercentage = overallRate
        val absentPercentage = 100 - overallRate
        val atRiskCount = random.nextInt(5) + 2
        
        return AttendanceAnalytics(
            overallAttendanceRate = overallRate,
            presentPercentage = presentPercentage,
            absentPercentage = absentPercentage,
            atRiskStudentsCount = atRiskCount,
            weeklyData = weeklyData,
            trendData = trendData
        )
    }
}

data class AttendanceReportUiState(
    val isLoading: Boolean = false,
    val courseName: String = "",
    val sessions: List<SessionSummary> = emptyList(),
    val students: List<StudentAttendance> = emptyList(),
    val analytics: AttendanceAnalytics = AttendanceAnalytics(
        overallAttendanceRate = 0,
        presentPercentage = 0,
        absentPercentage = 0,
        atRiskStudentsCount = 0,
        weeklyData = emptyList(),
        trendData = emptyList()
    ),
    val averageAttendancePercentage: Int = 0,
    val error: String? = null
)
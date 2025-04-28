package com.smartattendance.android.feature.student.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.CourseRepository
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import com.smartattendance.android.feature.student.model.AttendanceHistoryItem
import com.smartattendance.android.feature.student.model.toHistoryItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentAttendanceHistoryViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudentAttendanceHistoryUiState())
    val uiState: StateFlow<StudentAttendanceHistoryUiState> = _uiState.asStateFlow()

    init {
        loadAttendanceHistory()
    }

    private fun loadAttendanceHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val userId = userPreferencesRepository.userId.firstOrNull() ?: ""
                if (userId.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User not found"
                        )
                    }
                    return@launch
                }

                // Combine attendance records with course information
                val courses = courseRepository.getAllCourses().firstOrNull() ?: emptyList()
                val courseMap = courses.associateBy { it.id }

                attendanceRepository.getAttendancesByStudentId(userId).collectLatest { attendances ->
                    val historyItems = attendances.map { attendance ->
                        val courseName = courseMap[attendance.courseId]?.name ?: "Unknown Course"
                        attendance.toHistoryItem(courseName)
                    }

                    // Calculate attendance percentage
                    val totalRecords = historyItems.size
                    val presentRecords = historyItems.count { it.status.equals("Present", ignoreCase = true) }
                    val attendancePercentage = if (totalRecords > 0) {
                        (presentRecords * 100) / totalRecords
                    } else {
                        0
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            attendanceRecords = historyItems,
                            attendancePercentage = attendancePercentage,
                            errorMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading attendance history: ${e.message}"
                    )
                }
            }
        }
    }

    fun refreshData() {
        loadAttendanceHistory()
    }
}

data class StudentAttendanceHistoryUiState(
    val isLoading: Boolean = true,
    val attendanceRecords: List<AttendanceHistoryItem> = emptyList(),
    val attendancePercentage: Int = 0,
    val errorMessage: String? = null
)
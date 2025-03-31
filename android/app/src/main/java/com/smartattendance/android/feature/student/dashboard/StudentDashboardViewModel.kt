package com.smartattendance.android.feature.student.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.data.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentDashboardViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StudentDashboardUiState())
    val uiState: StateFlow<StudentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadTodayClasses()
    }

    fun onEvent(event: StudentDashboardEvent) {
        when (event) {
            is StudentDashboardEvent.MarkAttendance -> {
                markAttendance(event.classId)
            }

            is StudentDashboardEvent.RefreshDashboard -> {
                loadTodayClasses()
            }
        }
    }

    private fun loadTodayClasses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load today's classes"
                    )
                }
            }
        }
    }

    private fun markAttendance(classId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMarking = true) }

            try {
                attendanceRepository.markAttendance(classId)

                _uiState.update {
                    it.copy(
                        isMarking = false,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isMarking = false,
                        error = e.message ?: "Failed to mark attendance"
                    )
                }
            }
        }
    }

}

data class StudentDashboardUiState(
//    val todayClasses: List<ClassSession> = emptyList(),
    val monthlyAttendancePercentage: Float = 0f,
    val classesAttended: Int = 0,
    val classesAbsent: Int = 0,
    val isLoading: Boolean = false,
    val isMarking: Boolean = false,
    val error: String? = null
)

sealed class StudentDashboardEvent {
    data class MarkAttendance(val classId: String) : StudentDashboardEvent()
    object RefreshDashboard : StudentDashboardEvent()
}


data class AttendanceStats(
    val classesAttended: Int,
    val classesAbsent: Int
)
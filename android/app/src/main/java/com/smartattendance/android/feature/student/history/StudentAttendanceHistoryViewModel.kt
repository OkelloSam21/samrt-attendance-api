package com.smartattendance.android.feature.student.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.model.Attendance
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentAttendanceHistoryViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudentAttendanceHistoryUiState())
    val uiState: StateFlow<StudentAttendanceHistoryUiState> = _uiState.asStateFlow()

    init {
        loadAttendanceHistory()
    }

    private fun loadAttendanceHistory() {
        viewModelScope.launch {

            val userId = userPreferencesRepository.userId.firstOrNull() ?: ""

            try {
                val records = attendanceRepository.getAttendancesByStudentId(userId).collect {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = true,
                            attendanceRecords = it
                        )
                    }
                }

                val attendanceRecords = attendanceRepository.getAttendancesByStudentId(userId).firstOrNull() ?: emptyList()

                // Calculate attendance percentage
                val totalRecords = attendanceRecords.size
                val presentRecords = attendanceRecords.count { it.status == "Present" }
                val attendancePercentage = if (totalRecords > 0) {
                    (presentRecords * 100) / totalRecords
                } else {
                    0
                }
                _uiState.update { 
                    it.copy(
                        isLoading = false,
//                        attendanceRecords = records,
                        attendancePercentage = attendancePercentage
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        attendanceRecords = emptyList(),
                        attendancePercentage = 0
                    )
                }
            }
        }
    }
}

data class StudentAttendanceHistoryUiState(
    val isLoading: Boolean = true,
    val attendanceRecords: List<Attendance> = emptyList(),
    val attendancePercentage: Int = 0
)

data class AttendanceRecord(
    val courseName: String,
    val date: String,
    val isPresent: Boolean
)
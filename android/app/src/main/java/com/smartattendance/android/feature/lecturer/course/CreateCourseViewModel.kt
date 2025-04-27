package com.smartattendance.android.feature.lecturer.course

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.data.network.model.ScheduleRequest
import com.smartattendance.android.domain.model.Course
import com.smartattendance.android.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateCourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    var state by mutableStateOf(CreateCourseUiState())
        private set

    var schedules = mutableStateListOf<ScheduleRequest>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onCourseNameChange(name: String) {
        state = state.copy(courseName = name)
    }

    fun addSchedule(schedule: ScheduleRequest) {
        schedules.add(schedule)
    }

    fun removeSchedule(schedule: ScheduleRequest) {
        schedules.remove(schedule)
    }

    fun createCourse(lecturerId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result: Result<Course> = courseRepository.createCourse(state.courseName, lecturerId, schedules)
            isLoading = false
            result.onSuccess {
                onSuccess()
            }.onFailure {
                errorMessage = it.message ?: "Failed to create course"
            }
        }
    }

    fun resetState() {
        state = CreateCourseUiState()
        schedules.clear()
        errorMessage = null
    }
}

data class CreateCourseUiState(
    val courseName: String = "",
    val isCourseNameError: Boolean = false,
)
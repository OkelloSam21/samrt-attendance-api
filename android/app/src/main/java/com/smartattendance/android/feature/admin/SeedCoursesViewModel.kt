package com.smartattendance.android.feature.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeedCoursesViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeedCoursesUiState())
    val uiState: StateFlow<SeedCoursesUiState> = _uiState.asStateFlow()

    init {
        // Start with one empty course field
        _uiState.update { 
            it.copy(courseNames = listOf(""))
        }
    }

    fun onEvent(event: SeedCoursesEvent) {
        when (event) {
            is SeedCoursesEvent.CourseNameChanged -> {
                val updatedCourseNames = _uiState.value.courseNames.toMutableList()
                if (event.index < updatedCourseNames.size) {
                    updatedCourseNames[event.index] = event.name
                    _uiState.update { 
                        it.copy(courseNames = updatedCourseNames)
                    }
                }
            }
            
            is SeedCoursesEvent.AddCourse -> {
                val updatedCourseNames = _uiState.value.courseNames.toMutableList()
                updatedCourseNames.add("")
                _uiState.update { 
                    it.copy(courseNames = updatedCourseNames)
                }
            }
            
            is SeedCoursesEvent.RemoveCourse -> {
                val updatedCourseNames = _uiState.value.courseNames.toMutableList()
                if (event.index < updatedCourseNames.size && updatedCourseNames.size > 1) {
                    updatedCourseNames.removeAt(event.index)
                    _uiState.update { 
                        it.copy(courseNames = updatedCourseNames)
                    }
                }
            }
            
            is SeedCoursesEvent.ClearAll -> {
                _uiState.update { 
                    it.copy(
                        courseNames = listOf(""),
                        successMessage = null,
                        error = null
                    )
                }
            }
            
            is SeedCoursesEvent.SeedCourses -> {
                seedCourses()
            }
        }
    }

    private fun seedCourses() {
        val validCourseNames = _uiState.value.courseNames
            .filter { it.isNotBlank() }
            .distinct()
            
        if (validCourseNames.isEmpty()) {
            _uiState.update { 
                it.copy(error = "Please enter at least one course name")
            }
            return
        }
        
        _uiState.update { 
            it.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )
        }
        
        viewModelScope.launch {
            try {
                val result = courseRepository.seedCourses(validCourseNames)
                
                if (result.isSuccess) {
                    val count = result.getOrNull() ?: 0
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            successMessage = "Successfully created $count ${if (count == 1) "course" else "courses"}",
                            courseNames = listOf(""), // Reset to one empty field
                            error = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to create courses: ${result.exceptionOrNull()?.message ?: "Unknown error"}",
                            successMessage = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Error: ${e.message ?: "Unknown error"}",
                        successMessage = null
                    )
                }
            }
        }
    }
}

data class SeedCoursesUiState(
    val isLoading: Boolean = false,
    val courseNames: List<String> = emptyList(),
    val successMessage: String? = null,
    val error: String? = null
)

sealed class SeedCoursesEvent {
    data class CourseNameChanged(val index: Int, val name: String) : SeedCoursesEvent()
    object AddCourse : SeedCoursesEvent()
    data class RemoveCourse(val index: Int) : SeedCoursesEvent()
    object ClearAll : SeedCoursesEvent()
    object SeedCourses : SeedCoursesEvent()
}
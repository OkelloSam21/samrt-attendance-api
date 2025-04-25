package com.smartattendance.android.feature.admin.coursemanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.model.Course
import com.smartattendance.android.domain.model.UserData
import com.smartattendance.android.domain.repository.CourseRepository
import com.smartattendance.android.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CourseManagementViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CourseManagementUiState())
    val uiState: StateFlow<CourseManagementUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
        loadLecturers()
    }

    fun loadCourses() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                courseRepository.getAllCourses()
                    .catch { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Failed to load courses: ${e.message}"
                            )
                        }
                    }
                    .collectLatest { courses ->
                        val courseModels = courses.map { course ->
                            CourseUiModel(
                                id = course.id,
                                name = course.name,
                                lecturerId = course.lecturerId,
                                lecturerName = course.lecturerName,
                                createdAt = course.createdAt
                            )
                        }
                        
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                courses = courseModels,
                                error = null
                            )
                        }
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

    fun loadLecturers() {
        viewModelScope.launch {
            try {
                userRepository.getLecturers()
                    .catch { e ->
                        _uiState.update {
                            it.copy(error = "Failed to load lecturers: ${e.message}")
                        }
                    }
                    .collectLatest { lecturers ->
                        val lecturerModels = lecturers.map { lecturer ->
                            LecturerUiModel(
                                id = lecturer.id,
                                name = lecturer.name,
                                email = lecturer.email
                            )
                        }
                        
                        _uiState.update {
                            it.copy(
                                lecturers = lecturerModels,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to load lecturers: ${e.message}")
                }
            }
        }
    }

    fun createCourse(name: String) {
        _uiState.update { it.copy(isCreatingCourse = true) }

        viewModelScope.launch {
            try {
                val result = courseRepository.createCourse(name)
                
                if (result.isSuccess) {
                    loadCourses() // Refresh the course list
                    _uiState.update {
                        it.copy(
                            isCreatingCourse = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isCreatingCourse = false,
                            error = "Failed to create course: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingCourse = false,
                        error = "Failed to create course: ${e.message}"
                    )
                }
            }
        }
    }

    fun assignLecturerToCourse(courseId: String, lecturerId: String) {
        _uiState.update { it.copy(isAssigningLecturer = true) }

        viewModelScope.launch {
            try {
                // First get the lecturer's name
                val lecturer = userRepository.getUserById(lecturerId).firstOrNull()
                
                if (lecturer == null) {
                    _uiState.update {
                        it.copy(
                            isAssigningLecturer = false,
                            error = "Lecturer not found"
                        )
                    }
                    return@launch
                }
                
                // Get current course
                val course = courseRepository.getCourseById(courseId).firstOrNull()
                
                if (course == null) {
                    _uiState.update {
                        it.copy(
                            isAssigningLecturer = false,
                            error = "Course not found"
                        )
                    }
                    return@launch
                }
                
                // Update the course with the new lecturer
                val result = courseRepository.updateCourse(
                    courseId = courseId,
                    name = course.name,
                    lecturerId = lecturerId,
                    lecturerName = lecturer.name
                )
                
                if (result.isSuccess) {
                    loadCourses() // Refresh the course list
                    _uiState.update {
                        it.copy(
                            isAssigningLecturer = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isAssigningLecturer = false,
                            error = "Failed to assign lecturer: ${result.exceptionOrNull()?.message}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAssigningLecturer = false,
                        error = "Failed to assign lecturer: ${e.message}"
                    )
                }
            }
        }
    }
}

data class CourseManagementUiState(
    val isLoading: Boolean = false,
    val isCreatingCourse: Boolean = false,
    val isAssigningLecturer: Boolean = false,
    val courses: List<CourseUiModel> = emptyList(),
    val lecturers: List<LecturerUiModel> = emptyList(),
    val error: String? = null
)

data class CourseUiModel(
    val id: String,
    val name: String,
    val lecturerId: String,
    val lecturerName: String,
    val createdAt: Date
)

data class LecturerUiModel(
    val id: String,
    val name: String,
    val email: String
)
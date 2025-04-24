package com.smartattendance.android.feature.auth.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.data.network.model.AdminSignUpRequest
import com.smartattendance.android.data.network.model.LecturerSignUpRequest
import com.smartattendance.android.data.network.model.StudentSignUpRequest
import com.smartattendance.android.domain.model.AuthResult
import com.smartattendance.android.domain.repository.AuthRepository
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    // New method to set the user type from the screen
    fun setUserType(userType: UserType) {
        _uiState.update { it.copy(userType = userType) }
        Log.d("SignUpViewModel", "User type set: $userType")
    }

    fun onEvent(event: SignUpScreenEvents) {
        when (event) {
            is SignUpScreenEvents.EmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }

            is SignUpScreenEvents.LoginClicked -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }

            is SignUpScreenEvents.NameChanged -> {
                _uiState.update { it.copy(name = event.name) }
            }

            is SignUpScreenEvents.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }

            is SignUpScreenEvents.EmployeeIdChanged -> {
                _uiState.update { it.copy(employeeId = event.employeeId) }
            }

            is SignUpScreenEvents.DepartmentChanged -> {
                _uiState.update { it.copy(department = event.department) }
            }

            is SignUpScreenEvents.YearOfStudyChanged -> {
                _uiState.update { it.copy(yearOfStudy = event.yearOfStudy) }
            }

            is SignUpScreenEvents.SignUp -> {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        name = event.name,
                        email = event.email,
                        password = event.password,
                        errorMessage = null,
                    )
                }
                viewModelScope.launch {
                    val state = _uiState.value

                    // Field validation
                    if (state.name.isBlank() || state.email.isBlank() || state.password.isBlank()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "All fields are required"
                            )
                        }
                        return@launch
                    }

                    // User type specific validations
                    val userType = state.userType ?: run {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "User type not provided"
                            )
                        }
                        return@launch
                    }

                    val request = when (userType) {
                        UserType.STUDENT -> {
                            if (state.regNo.isBlank()) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = "Registration number is required"
                                    )
                                }
                                return@launch
                            }

                            StudentSignUpRequest(
                                name = state.name,
                                email = state.email,
                                password = state.password,
                                regNo = state.regNo,
                                department = state.department,
                                yearOfStudy = state.yearOfStudy
                            )
                        }

                        UserType.LECTURER -> {
                            if (state.employeeId.isBlank()) {
                                _uiState.update {
                                    it.copy(
                                        isLoading = false,
                                        errorMessage = "Employee ID is required for lecturers"
                                    )
                                }
                                return@launch
                            }

                            LecturerSignUpRequest(
                                name = state.name,
                                email = state.email,
                                password = state.password,
                                employeeId = state.employeeId,
                                department = state.department
                            )
                        }

                        UserType.ADMIN -> {
                            AdminSignUpRequest(
                                name = state.name,
                                email = state.email,
                                password = state.password,
                                department = state.department
                            )
                        }
                    }

                    // Log the request before sending it
                    Log.d("SignUpViewModel", "Sending signup request: $request")

                    // Process the registration
                    when (val result = authRepository.register(request)) {
                        is AuthResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                            }
                        }

                        AuthResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    isSignUpSuccessful = true
                                )
                            }
                            // Save the user type to preferences
                            userPreferencesRepository.saveSelectedUserType(userType.name)
                        }
                    }
                }
            }

            is SignUpScreenEvents.RegNoChanged -> {
                _uiState.update { it.copy(regNo = event.regNo) }
            }
        }
    }
}

data class SignUpUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val regNo: String = "",        // For students
    val employeeId: String = "",   // For lecturers (separate from regNo)
    val department: String = "",
    val yearOfStudy: Int? = null,  // For students
    val userType: UserType? = null,
    val isSignUpSuccessful: Boolean = false,
)


sealed class SignUpScreenEvents {
    data class SignUp(val name: String, val email: String, val password: String) : SignUpScreenEvents()
    data class NameChanged(val name: String) : SignUpScreenEvents()
    data class EmailChanged(val email: String) : SignUpScreenEvents()
    data class PasswordChanged(val password: String) : SignUpScreenEvents()
    data class RegNoChanged(val regNo: String) : SignUpScreenEvents()
    data class EmployeeIdChanged(val employeeId: String) : SignUpScreenEvents()
    data class DepartmentChanged(val department: String) : SignUpScreenEvents()
    data class YearOfStudyChanged(val yearOfStudy: Int) : SignUpScreenEvents()
    object LoginClicked : SignUpScreenEvents()
}
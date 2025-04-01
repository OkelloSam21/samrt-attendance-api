package com.smartattendance.android.feature.auth.signup

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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

    init {
        // Retrieve user type from preferences
        viewModelScope.launch {
            userPreferencesRepository.selectedUserType.collect { userTypeString ->
                userTypeString?.let { typeString ->
                    try {
                        val userType = UserType.valueOf(typeString.uppercase())
                        _uiState.update {
                            it.copy(userType = userType)
                        }
                    } catch (e: IllegalArgumentException) {
                        _uiState.update {
                            it.copy(errorMessage = "Invalid user type")
                        }
                    }
                }
            }
        }
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

                    // Validate user type
                    val userType = state.userType ?: run {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "User type not selected"
                            )
                        }
                        return@launch
                    }

                    // Validate additional ID for student and lecturer
                    if ((userType == UserType.STUDENT || userType == UserType.LECTURER) && state.regNo.isBlank()) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = when (userType) {
                                    UserType.STUDENT -> "Registration number is required"
                                    UserType.LECTURER -> "Employee role number is required"
                                    UserType.ADMIN -> ""
                                }
                            )
                        }
                        return@launch
                    }

                    // Create appropriate request based on user type
                    val request = when (userType) {
                        UserType.STUDENT -> StudentSignUpRequest(
                            name = state.name,
                            email = state.email,
                            password = state.password,
                            regNo = state.regNo
                        )
                        UserType.LECTURER -> LecturerSignUpRequest(
                            name = state.name,
                            email = state.email,
                            password = state.password,
                            employeeRoleNo = state.regNo
                        )
                        UserType.ADMIN -> AdminSignUpRequest(
                            name = state.name,
                            email = state.email,
                            password = state.password
                        )
                    }

                    // Single registration method
                    val result = authRepository.register(request)

                    // Handle result
                    when (result) {
                        is AuthResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message ?: "Registration failed"
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
                        }
                    }
                }
            }

            is SignUpScreenEvents.EmployeeIdChanged -> {
                _uiState.update { it.copy(regNo = event.employeeId) }
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
    val regNo: String = "",
    val userType: UserType? = null,
    val password: String = "",
    val isSignUpSuccessful: Boolean = false,
//    val userType: UserType? = UserType.STUDENT
)

sealed class SignUpScreenEvents {
    data class SignUp(val name: String, val email: String, val password: String) :
        SignUpScreenEvents()

    data class NameChanged(val name: String) : SignUpScreenEvents()
    data class EmailChanged(val email: String) : SignUpScreenEvents()
    data class PasswordChanged(val password: String) : SignUpScreenEvents()
    data class RegNoChanged(val regNo: String) : SignUpScreenEvents()
    data class EmployeeIdChanged(val employeeId: String) : SignUpScreenEvents()
    object LoginClicked : SignUpScreenEvents()
}
package com.smartattendance.android.feature.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.model.AuthResult
import com.smartattendance.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()


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
                    val email = _uiState.value.email
                    val password = _uiState.value.password

                    when(val result = authRepository.login(email, password)){
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
                                    errorMessage = null
                                )
                            }
                        }
                    }
                }
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
    val isSignUpSuccessful: Boolean = false,
//    val userType: UserType? = UserType.STUDENT
)

sealed class SignUpScreenEvents {
    data class SignUp(val name: String, val email: String, val password: String) :
        SignUpScreenEvents()

    data class NameChanged(val name: String) : SignUpScreenEvents()
    data class EmailChanged(val email: String) : SignUpScreenEvents()
    data class PasswordChanged(val password: String) : SignUpScreenEvents()
//    object NavigateDashboard : SignUpScreenEvents()
//    object BackClicked : SignUpScreenEvents()
    object LoginClicked : SignUpScreenEvents()
}
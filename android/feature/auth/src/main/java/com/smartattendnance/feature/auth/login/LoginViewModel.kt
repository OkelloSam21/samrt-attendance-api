package com.smartattendnance.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
//    private val loginRepository: LoginRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.Login -> {
                _uiState.update {
                    it.copy(isLoading = true)
                }
                viewModelScope.launch {
//                    val result = loginRepository.login(event.username, event.password)
//                    uiState.value = uiState.value.copy(isLoading = false)
//                    if (result.isSuccess) {
//                        uiState.value = uiState.value.copy(navigateToSignUp = true)
//                    } else {
//                        uiState.value =
//                            uiState.value.copy(errorMessage = result.exceptionOrNull()?.message)
//                    }
                }
            }
            is LoginScreenEvent.SignUp -> {
                // Navigate to sign up screen
                _uiState.update<LoginUiState> {
                    it.copy(navigateToSignUp = true)
                }
            }

            is LoginScreenEvent.EmailChanged -> {
                _uiState.update {
                    it.copy(email = event.email)
                }
            }
            is LoginScreenEvent.PasswordChanged -> {
                _uiState.update {
                    it.copy(password = event.password)
                }
            }

            LoginScreenEvent.ForgotPassword -> {
                _uiState.update {
                    it.copy(navigateToForgotPassword = true )
                }
            }
        }
    }

}

data class LoginUiState(
    val isLoading: Boolean = false,
    val navigateToSignUp: Boolean = false,
    val navigateToForgotPassword: Boolean = false,
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null
)

sealed class LoginScreenEvent {
    data class Login(val username: String, val password: String) : LoginScreenEvent()
    data class EmailChanged(val email: String) : LoginScreenEvent()
    data class PasswordChanged(val password: String) : LoginScreenEvent()
    object SignUp : LoginScreenEvent()
    object ForgotPassword : LoginScreenEvent()
}
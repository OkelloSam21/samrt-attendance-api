package com.smartattendnance.feature.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
    // private val loginRepository: LoginRepository
) : ViewModel() {
    private val loginArgs = LoginArgs(savedStateHandle)

    private val _uiState = MutableStateFlow(
        LoginUiState(
            userType = loginArgs.userType
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.Login -> {
                _uiState.update {
                    it.copy(isLoading = true)
                }
                viewModelScope.launch {
                    // Existing login logic
                    // If successful, emit navigation event to the proper dashboard
                    // based on user type
                    val userType = _uiState.value.userType
                    // Example:
                    // _navigationEvents.emit(LoginNavigationEvent.NavigateToDashboard(userType))
                }
            }

            is LoginScreenEvent.SignUp -> {
                // Navigate to sign up screen with user type
                viewModelScope.launch {
                    _navigationEvents.emit(
                        LoginNavigationEvent.NavigateToSignUp(_uiState.value.userType)
                    )
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
                viewModelScope.launch {
                    _navigationEvents.emit(LoginNavigationEvent.NavigateToForgotPassword)
                }
            }
        }
    }
}

// Update your LoginUiState to include userType
data class LoginUiState(
    val isLoading: Boolean = false,
    val navigateToSignUp: Boolean = false,
    val navigateToForgotPassword: Boolean = false,
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val userType: String? = null
)

// Add this sealed class if you don't have navigation events yet
sealed class LoginNavigationEvent {
    data class NavigateToDashboard(val userType: String?) : LoginNavigationEvent()
    data class NavigateToSignUp(val userType: String?) : LoginNavigationEvent()
    object NavigateToForgotPassword : LoginNavigationEvent()
}

sealed class LoginScreenEvent {
    data class Login(val username: String, val password: String) : LoginScreenEvent()
    data class EmailChanged(val email: String) : LoginScreenEvent()
    data class PasswordChanged(val password: String) : LoginScreenEvent()
    object SignUp : LoginScreenEvent()
    object ForgotPassword : LoginScreenEvent()
}
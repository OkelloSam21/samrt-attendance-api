package com.smartattendance.android.feature.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.data.repository.AuthRepository
import com.smartattendance.android.domain.model.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: AuthRepository
): ViewModel() {
    private val args = LoginArgs(savedStateHandle)

    private val _uiState = MutableStateFlow(LoginUiState(userType = args.toString()))
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableStateFlow<LoginNavigationEvents>(LoginNavigationEvents.NavigateToDashBoard(args.toString()))
    val navigationEvents: StateFlow<LoginNavigationEvents> = _navigationEvents.asStateFlow()

    fun onEvent(event: LoginUiEvents) {
        when (event) {
            is LoginUiEvents.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }

            is LoginUiEvents.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }

            is LoginUiEvents.OnLoginClicked -> {
                _uiState.update { it.copy(isLoading = true) }
                login(
                    email = uiState.value.email,
                    password = uiState.value.password
                )
            }

            is LoginUiEvents.OnSignUpClicked -> {
                _navigationEvents.update { LoginNavigationEvents.NavigateToSignUp(uiState.value.userType) }
            }
            is LoginUiEvents.OnForgotPasswordClicked -> {
                _navigationEvents.update { LoginNavigationEvents.NavigateToForgotPassword }
            }
        }
    }

    private fun login( email: String, password: String) {
        viewModelScope.launch{
            when ( val result = repository.login(email  = email, password = password ) ){
                is AuthResult.Success -> {
                    _navigationEvents.emit(LoginNavigationEvents.NavigateToDashBoard(uiState.value.userType))
                    _uiState.update { it.copy(isLoading = false) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message, isLoading = false) }
                }
            }
        }
    }

}

data class LoginUiState (
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val userType: String = ""
)

sealed class LoginNavigationEvents {
    data class NavigateToDashBoard(val userType: String) : LoginNavigationEvents()
    data class NavigateToSignUp(val userType: String) : LoginNavigationEvents()
    data object NavigateToForgotPassword : LoginNavigationEvents()
}

sealed class LoginUiEvents {
    data class OnEmailChanged(val email: String) : LoginUiEvents()
    data class OnPasswordChanged(val password: String) : LoginUiEvents()
    data object OnLoginClicked : LoginUiEvents()
    data object OnSignUpClicked : LoginUiEvents()
    data object OnForgotPasswordClicked : LoginUiEvents()
}
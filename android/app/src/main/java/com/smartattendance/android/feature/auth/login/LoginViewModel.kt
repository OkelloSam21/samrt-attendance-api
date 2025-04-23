package com.smartattendance.android.feature.auth.login

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.model.AuthResult
import com.smartattendance.android.domain.repository.AuthRepository
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Extract userType from SavedStateHandle
    private val userType: UserType =
        UserType.valueOf(savedStateHandle.get<String>("userType") ?: UserType.STUDENT.name)
    init{
        _uiState.update { it.copy(userType = userType) }
        Log.d("LoginViewModel", "userType: $userType")
    }

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
                Log.d("Login ViewModel", "usertype is : ${_uiState.value.userType}")
                login(
                    email = uiState.value.email,
                    password = uiState.value.password
                )
            }

            is LoginUiEvents.OnForgotPasswordClicked -> {
                _uiState.update {
                    it.copy(
                        errorMessage = "",
                        isLoginSuccessful = false
                    )
                }
            }

            is LoginUiEvents.OnLoginSuccess -> TODO()
            LoginUiEvents.OnSignUpClicked -> TODO()
        }
    }

    private fun login( email: String, password: String) {
        viewModelScope.launch{
            when ( val result = repository.login(email  = email, password = password ) ){
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                }
                is AuthResult.Error -> {
                    _uiState.update { it.copy(errorMessage = result.message, isLoginSuccessful = false, isLoading = false) }
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
    val userType: UserType = UserType.STUDENT,
    val isLoginSuccessful: Boolean = false
)


sealed class LoginUiEvents {
    data class OnEmailChanged(val email: String) : LoginUiEvents()
    data class OnPasswordChanged(val password: String) : LoginUiEvents()
    data object OnLoginClicked : LoginUiEvents()
    data object OnSignUpClicked : LoginUiEvents()
    data class OnLoginSuccess(val userType: UserType): LoginUiEvents()
    data object OnForgotPasswordClicked : LoginUiEvents()
}
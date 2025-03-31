package com.smartattendance.android.feature.auth.login.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState = _uiState.asStateFlow()

    // SharedFlow for navigation events
    private val _navigationEvents = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun onEvent(event: SignUpScreenEvents) {
        when (event) {
            is SignUpScreenEvents.BackClicked -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                    )
                }
                viewModelScope.launch {
                    _navigationEvents.emit(SignUpNavigationEvent.NavigateBack)
                }
            }

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
                viewModelScope.launch {
                    _navigationEvents.emit(SignUpNavigationEvent.NavigateToLogin)
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
                        password = event.password
                    )
                }
                viewModelScope.launch {
                    _navigationEvents.emit(SignUpNavigationEvent.NavigateToDashboard)
                }
            }

            SignUpScreenEvents.NavigateDashboard -> {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            }

            else -> {}
        }
    }
}

data class SignUpUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val name: String = "",
    val email: String = "",
    val password: String = ""
)

sealed class SignUpScreenEvents {
    data class SignUp(val name: String, val email: String, val password: String) :
        SignUpScreenEvents()

    data class NameChanged(val name: String) : SignUpScreenEvents()
    data class EmailChanged(val email: String) : SignUpScreenEvents()
    data class PasswordChanged(val password: String) : SignUpScreenEvents()
    object NavigateDashboard : SignUpScreenEvents()
    object BackClicked : SignUpScreenEvents()
    object LoginClicked : SignUpScreenEvents()
}

sealed class SignUpNavigationEvent {
    object NavigateToDashboard : SignUpNavigationEvent()
    object NavigateToLogin : SignUpNavigationEvent()
    object NavigateBack : SignUpNavigationEvent()
}
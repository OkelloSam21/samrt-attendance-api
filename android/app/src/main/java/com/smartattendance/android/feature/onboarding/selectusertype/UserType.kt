package com.smartattendance.android.feature.onboarding.selectusertype

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class SelectUserTypeViewModel @Inject constructor(
    // If you need a repository for saving user type, inject it here
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SelectUserTypeUiState())
    val uiState = _uiState.asStateFlow()

    private var selectedUserType: UserType?
        get() = savedStateHandle["selectedUserType"]
        set(value) {
            savedStateHandle["selectedUserType"] = value
        }

    
    fun onEvent(event: SelectUserTypeEvent) {
        when (event) {
            is SelectUserTypeEvent.UserTypeSelected -> {
                _uiState.update {
                    it.copy(selectedUserType = event.userType)
                }
                // You could save the user type here if needed
                selectedUserType = event.userType
            }
            
            SelectUserTypeEvent.NextClicked -> {
                val userType = _uiState.value.selectedUserType
//                if (userType != null) {
//                    viewModelScope.launch {
//                        _navigationEvents.emit(
//                            SelectUserTypeNavigationEvent.NavigateToLogin(userType)
//                        )
//                    }
//                }
            }
        }
    }
}

data class SelectUserTypeUiState(
    val selectedUserType: UserType? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class SelectUserTypeEvent {
    data class UserTypeSelected(val userType: UserType) : SelectUserTypeEvent()
    object NextClicked : SelectUserTypeEvent()
}

sealed class SelectUserTypeNavigationEvent {
    data class NavigateToLogin(val userType: UserType) : SelectUserTypeNavigationEvent()
    data class NavigateToSignUp(val userType: UserType) : SelectUserTypeNavigationEvent()
}
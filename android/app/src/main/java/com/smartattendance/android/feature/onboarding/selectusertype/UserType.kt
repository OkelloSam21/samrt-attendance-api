package com.smartattendance.android.feature.onboarding.selectusertype

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SelectUserTypeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectUserTypeUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<SelectUserTypeNavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    fun onEvent(event: SelectUserTypeEvent) {
        when (event) {
            is SelectUserTypeEvent.UserTypeSelected -> {
                // Update UI state
                _uiState.update {
                    it.copy(selectedUserType = event.userType)
                }

                // Save user type to preferences
                viewModelScope.launch {
                    userPreferencesRepository.saveSelectedUserType(event.userType.name)
                }
            }

            SelectUserTypeEvent.NextClicked -> {
                val userType = _uiState.value.selectedUserType
                userType?.let { type ->
                    Log.d("SelectUserTypeScreen", "Selected userTye : $type")
                    viewModelScope.launch {
                        _navigationEvents.emit(
                            SelectUserTypeNavigationEvent.NavigateToSignUp(type)
                        )
                    }
                }
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
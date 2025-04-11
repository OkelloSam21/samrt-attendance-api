package com.smartattendance.android

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.data.repository.UserPreferencesRepositoryImpl
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserPreferencesRepositoryImpl
) : ViewModel() {
    // State to keep splash screen visible
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Destination state – Now holds a navigation event instead of a route string
    private val _startDestination = MutableStateFlow<NavigationEvent?>(null)
    val startDestination: StateFlow<NavigationEvent?> = _startDestination.asStateFlow()

    init {
        determineInitialDestination()
    }

    private fun determineInitialDestination() {
        viewModelScope.launch {
            try {
                val accessToken = repository.accessToken.first()
                val userRole = repository.userRole.first()

                val navigationEvent = when {
                    accessToken.isNullOrBlank() && userRole.isNullOrBlank() ->
                        NavigationEvent.NavigateToSelectUserType

                    accessToken.isNullOrBlank() -> {
                        val userType = when (userRole?.lowercase()) {
                            "student" -> UserType.STUDENT
                            "admin" -> UserType.ADMIN
                            "lecturer" -> UserType.LECTURER
                            else -> {
                                // Handle invalid or missing userRole when accessToken is blank
                                // For consistency, navigate to SelectUserType
                                null  // Or throw an exception, depending on your preference.
                            }
                        }
                        userType?.let { NavigationEvent.NavigateToLogin(userType = it) } ?:
                        NavigationEvent.NavigateToSelectUserType
                    }

                    else -> {
                        val userType = when (userRole?.lowercase()) {
                            "student" -> UserType.STUDENT
                            "admin" -> UserType.ADMIN
                            "lecturer" -> UserType.LECTURER
                            else -> null
                        }

                        when (userType) {
                            UserType.STUDENT -> NavigationEvent.NavigateToStudentDashboard
                            UserType.ADMIN -> NavigationEvent.NavigateToAdminDashboard
                            UserType.LECTURER -> NavigationEvent.NavigateToLecturerDashboard
                            else -> NavigationEvent.NavigateToSelectUserType  // Invalid or missing role
                        }
                    }
                }

                _startDestination.value = navigationEvent
            } catch (e: Exception) {
                _startDestination.value = NavigationEvent.NavigateToSelectUserType // consistent handling
            } finally {
                _isLoading.value = false
            }
        }
    }

    // New event to signal that the navigation has been handled
    fun onNavigationHandled() {
        _startDestination.value = null
    }
}

// Sealed class to represent navigation events
sealed class NavigationEvent {
    object NavigateToSelectUserType : NavigationEvent()
    data class NavigateToLogin(val userType: UserType) : NavigationEvent()
    object NavigateToStudentDashboard : NavigationEvent()
    object NavigateToAdminDashboard : NavigationEvent()
    object NavigateToLecturerDashboard : NavigationEvent()
}
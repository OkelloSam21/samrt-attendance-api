package com.smartattendance.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.data.repository.UserPreferencesRepositoryImpl
import com.smartattendance.android.feature.admin.dashboard.AdminDashboardDestination
import com.smartattendance.android.feature.auth.login.LoginDestination
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeDestination
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import com.smartattendance.android.feature.lecturer.dashboard.LecturerDashboardDestination
import com.smartattendance.android.feature.student.dashboard.StudentDashboardDestination
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
): ViewModel() {
    // State to keep splash screen visible
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Destination state
    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination: StateFlow<String?> = _startDestination.asStateFlow()

    init {
        determineInitialDestination()
    }

    private fun determineInitialDestination() {
        viewModelScope.launch {
            try {
                // Collect the first values of access token and user role
                val accessToken = repository.accessToken.first()
                val userRole = repository.userRole.first()

                // Determine destination based on access token and user role
                val destination = when {
                    // No access token and no user role - first-time installation
                    accessToken.isNullOrBlank() && userRole.isNullOrBlank() ->
                        SelectUserTypeDestination.route

                    // Access token is blank but user role exists - go to login
                    accessToken.isNullOrBlank() ->
                        LoginDestination.route

                    // Access token and user role exist - navigate to dashboard
                    else -> {
                        // Convert user role to UserType enum
                        val userType = when (userRole?.lowercase()) {
                            "student" -> UserType.STUDENT
                            "admin" -> UserType.ADMIN
                            "lecturer" -> UserType.LECTURER
                            else -> null
                        }

                        // If user type is valid, return dashboard route, otherwise login
                        userType?.let {
                            when (it) {
                                UserType.STUDENT -> StudentDashboardDestination.route
                                UserType.ADMIN -> AdminDashboardDestination.route
                                UserType.LECTURER -> LecturerDashboardDestination.route
                            }
                        } ?: LoginDestination.route
                    }
                }

                // Set the start destination
                _startDestination.value = destination
            } catch (e: Exception) {
                // Handle any errors during destination determination
                _startDestination.value = LoginDestination.route
            } finally {
                // Always set loading to false
                _isLoading.value = false
            }
        }
    }
}
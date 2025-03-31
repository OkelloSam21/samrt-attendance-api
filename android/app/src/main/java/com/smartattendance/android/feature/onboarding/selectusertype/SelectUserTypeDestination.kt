package com.smartattendance.android.feature.auth.selectusertype

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.smartattendance.android.feature.admin.dashboard.navigateToAdminDashboard
import com.smartattendance.android.feature.auth.login.navigateToLogin
import com.smartattendance.android.feature.auth.login.selectusertype.SelectUserTypeNavigationEvent
import com.smartattendance.android.feature.auth.login.selectusertype.SelectUserTypeScreen
import com.smartattendance.android.feature.auth.login.selectusertype.SelectUserTypeViewModel
import com.smartattendance.android.feature.auth.signup.navigateToSignUp
import com.smartattendance.android.feature.lecturer.dashboard.navigateToLecturerDashboard
import com.smartattendance.android.feature.student.dashboard.navigateToStudentDashboard
import kotlinx.serialization.Serializable

@Serializable
object SelectUserTypeDestination


enum class UserType {
    STUDENT, ADMIN, LECTURER
}

fun NavGraphBuilder.selectUserTypeScreen(
    navController: NavController
) {
    composable<SelectUserTypeDestination> {
        val viewModel: SelectUserTypeViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle().value

        // Collect navigation events from the ViewModel
        LaunchedEffect(key1 = viewModel.navigationEvents) {
            viewModel.navigationEvents.collect { event ->
                when (event) {
                    is SelectUserTypeNavigationEvent.NavigateToLogin -> {
                        navController.navigateToLogin(userType = event.userType.name.lowercase())
                    }
                    is SelectUserTypeNavigationEvent.NavigateToSignUp -> {
                        navController.navigateToSignUp(userType = event.userType.name.lowercase())
                    }
//                    is SelectUserTypeNavigationEvent.NavigateToDashboard -> {
//                        when (event.userType) {
//                            UserType.STUDENT -> navController.navigateToStudentDashboard()
//                            UserType.ADMIN -> navController.navigateToAdminDashboard()
//                            UserType.LECTURER -> navController.navigateToLecturerDashboard()
//                        }
//                    }

                    else -> {}
                }
            }
        }
        SelectUserTypeScreen(viewModel = viewModel)
    }
}

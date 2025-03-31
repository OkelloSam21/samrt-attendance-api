package com.smartattendance.android.feature.auth.login.selectusertype

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.smartattendance.android.feature.auth.login.LoginDestination
import com.smartattendnance.feature.auth.signup.SignUpDestination

object SelectUserTypeDestination {
    const val route = "select_user_type"
}

fun NavGraphBuilder.selectUserTypeScreen(
    navController: NavController
) {
    composable(com.smartattendance.android.feature.auth.login.selectusertype.SelectUserTypeDestination.route) {
        val viewModel: SelectUserTypeViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle().value

        // Collect navigation events from the ViewModel
        LaunchedEffect(key1 = viewModel.navigationEvents) {
            viewModel.navigationEvents.collect { event ->
                when (event) {
                    is SelectUserTypeNavigationEvent.NavigateToLogin -> {
                        // Navigate to login with user type as parameter
                        navController.navigate(
                            com.smartattendance.android.feature.auth.login.selectusertype.SelectUserTypeNavigation.navigateToLogin(
                                event.userType
                            )
                        )
                    }
                    is SelectUserTypeNavigationEvent.NavigateToSignUp -> {
                        navController.navigate(
                            com.smartattendance.android.feature.auth.login.selectusertype.SelectUserTypeNavigation.navigateToSignUp(
                                event.userType
                            )
                        )
                    }
                }
            }
        }

        SelectUserTypeScreen(viewModel = viewModel)
    }
}

object SelectUserTypeNavigation {
    fun navigateToLogin(userType: UserType): String {
        // Modify this to match your login destination route
        // This assumes LoginDestination has a route property
        return "${com.smartattendance.android.feature.auth.login.LoginDestination.route}?userType=${userType.name}"
    }

    fun navigateToSignUp(userType: UserType): String {
        // Modify this to match your signup destination route
        return "${SignUpDestination.route}?userType=${userType.name}"
    }
}
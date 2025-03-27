package com.smartattendnance.feature.auth.selectusertype

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.smartattendnance.feature.auth.login.LoginDestination
import com.smartattendnance.feature.auth.signup.SignUpDestination

object SelectUserTypeDestination {
    const val route = "select_user_type"
}

fun NavGraphBuilder.selectUserTypeScreen(
    navController: NavController
) {
    composable(SelectUserTypeDestination.route) {
        val viewModel: SelectUserTypeViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle().value

        // Collect navigation events from the ViewModel
        LaunchedEffect(key1 = viewModel.navigationEvents) {
            viewModel.navigationEvents.collect { event ->
                when (event) {
                    is SelectUserTypeNavigationEvent.NavigateToLogin -> {
                        // Navigate to login with user type as parameter
                        navController.navigate(
                            SelectUserTypeNavigation.navigateToLogin(event.userType)
                        )
                    }
                    is SelectUserTypeNavigationEvent.NavigateToSignUp -> {
                        navController.navigate(
                            SelectUserTypeNavigation.navigateToSignUp(event.userType)
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
        return "${LoginDestination.route}?userType=${userType.name}"
    }

    fun navigateToSignUp(userType: UserType): String {
        // Modify this to match your signup destination route
        return "${SignUpDestination.route}?userType=${userType.name}"
    }
}
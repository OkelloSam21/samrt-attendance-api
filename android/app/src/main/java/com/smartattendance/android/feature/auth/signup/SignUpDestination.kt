package com.smartattendance.android.feature.auth.signup

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable


object SignUpDestination {
    const val route = "signUp"
}

fun NavGraphBuilder.signUpScreen(
    navController: NavController
) {
    composable(com.smartattendance.android.feature.auth.login.signup.SignUpDestination.route) {
        val viewModel: SignUpViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle()

        // Collect navigation events from the ViewModel
        LaunchedEffect(key1 = viewModel.navigationEvents) {
            viewModel.navigationEvents.collect { event ->
                when (event) {
                    com.smartattendance.android.feature.auth.login.signup.SignUpNavigationEvent.NavigateToDashboard -> {
                        navController.navigate(com.smartattendance.android.feature.auth.login.signup.SignUpNavigation.navigateToDashboard())
                    }

                    com.smartattendance.android.feature.auth.login.signup.SignUpNavigationEvent.NavigateToLogin -> {
                        navController.navigate(com.smartattendance.android.feature.auth.login.signup.SignUpNavigation.navigateToLogin())
                    }

                    com.smartattendance.android.feature.auth.login.signup.SignUpNavigationEvent.NavigateBack -> {
                        navController.popBackStack()
                    }
                }
            }
        }

        SignUpScreenContent(
            state = state.value,
            event = viewModel::onEvent
        )
    }
}

object SignUpNavigation {
    fun navigateToDashboard(): String {
        return "dashboard" // Replace with your actual dashboard route
    }

    fun navigateToLogin(): String {
        return "login" // Replace with your actual login route
    }

    fun navigateToSignUp(): String {
        return SignUpDestination.route
    }
}
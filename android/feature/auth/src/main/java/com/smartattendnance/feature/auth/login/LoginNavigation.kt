package com.smartattendnance.feature.auth.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

/**
 * We need to keep this public so we can "see" it from the app module,
 * and use it as a starting point/destination.
 */
@Serializable
data object LoginDestination

fun NavGraphBuilder.loginScreen(
    onLoginClicked: (email: String, password: String) -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    composable<LoginDestination> {
        LoginScreen(
            onLoginClicked = onLoginClicked,
            onForgotPasswordClicked = onForgotPasswordClicked,
            onSignUpClicked = onSignUpClicked
        )
    }
}
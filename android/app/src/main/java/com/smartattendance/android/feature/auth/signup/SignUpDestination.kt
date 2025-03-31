package com.smartattendance.android.feature.auth.signup

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartattendance.android.feature.auth.login.signup.SignUpScreen
import kotlinx.serialization.Serializable

@Serializable
object SignUpDestination {
    const val route = "signup?userType={userType}"
    const val userTypeParam = "userType"
    const val defaultUserType = "student"

    fun createRoute(userType: String) = "signup?userType=$userType"
}

class SignUpArgs(userType: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        userType = savedStateHandle[SignUpDestination.userTypeParam]
            ?: SignUpDestination.defaultUserType
    )
}

fun NavGraphBuilder.signUpScreen(
) {
    composable(
        route = SignUpDestination.route,
        arguments = listOf(navArgument(SignUpDestination.userTypeParam) {
            defaultValue = SignUpDestination.defaultUserType
        })
    ) {
        SignUpScreen()
    }
}

fun NavController.navigateToSignUp(userType: String) {
    this.navigate(SignUpDestination.createRoute(userType)) {
        launchSingleTop = true
    }
}

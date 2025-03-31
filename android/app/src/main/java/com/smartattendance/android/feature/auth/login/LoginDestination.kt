package com.smartattendance.android.feature.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.serialization.Serializable

@Serializable
data object LoginDestination  {
    const val route = "login?userType={userType}"
    const val userTypeParam = "userType"
    const val defaultUserType = "student"

    fun createRoute(userType: String) = "login?userType=$userType"
}

class LoginArgs(userType: String) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        userType = savedStateHandle[LoginDestination.userTypeParam] ?: LoginDestination.defaultUserType
    )
}

fun NavController.navigateToLogin(userType: String) {
    this.navigate(LoginDestination.createRoute(userType)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.loginScreen() {
    composable(
        route = LoginDestination.route,
        arguments = listOf(navArgument(LoginDestination.userTypeParam) {
            defaultValue = LoginDestination.defaultUserType
        })
    ) {
        LoginScreen()

    }
}
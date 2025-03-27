package com.smartattendnance.feature.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartattendnance.feature.auth.selectusertype.UserType
import kotlinx.serialization.Serializable

/**
 * Login destination that accepts user type parameter
 */
@Serializable
data object LoginDestination {
    const val route = "login?userType={userType}"
    const val userTypeArg = "userType"

    fun createRoute(userType: String? = null): String {
        return if (userType != null) {
            "login?userType=$userType"
        } else {
            "login"
        }
    }
}

class LoginArgs(val userType: String?) {
    constructor(savedStateHandle: SavedStateHandle) : this(
        userType = savedStateHandle[LoginDestination.userTypeArg]
    )
}

fun NavController.navigateToLogin(userType: String? = null) {
    this.navigate(LoginDestination.createRoute(userType)) {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.loginScreen() {
    composable(
        route = LoginDestination.route,
        arguments = listOf(
            navArgument(LoginDestination.userTypeArg) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) {
        LoginScreen()
    }
}
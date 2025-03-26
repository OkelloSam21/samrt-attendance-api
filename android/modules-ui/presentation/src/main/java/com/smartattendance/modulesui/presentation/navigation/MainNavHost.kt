package com.smartattendance.modulesui.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.smartattendnance.feature.auth.login.LoginDestination
import com.smartattendnance.feature.auth.login.loginScreen
import com.smartattendnance.feature.auth.signup.signUpScreen

@Composable
fun MainNavHost() {
    val navController: NavHostController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = LoginDestination
    ) {
        loginScreen(
            onLoginClicked = { },
            onForgotPasswordClicked = {},
            onSignUpClicked = {}
        )

        signUpScreen(
            onSignUpClicked = { },
            onBackClicked = { },
            onLoginClicked = {}
        )
    }
}

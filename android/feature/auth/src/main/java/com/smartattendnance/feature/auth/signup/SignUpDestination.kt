package com.smartattendnance.feature.auth.signup

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable


@Serializable
data object SignUpDestination

fun NavGraphBuilder.signUpScreen(

){
    composable < SignUpDestination>{
        SignUpScreen(

        )
    }
}

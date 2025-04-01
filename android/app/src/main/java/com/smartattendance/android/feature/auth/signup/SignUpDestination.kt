package com.smartattendance.android.feature.auth.signup

import androidx.navigation.NavController
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import kotlinx.serialization.Serializable

@Serializable
object SignUpDestination {
    const val route = "signup?userType={userType}"
}

fun NavController.navigateToSignUp(userType: UserType?) {
    this.navigate(SignUpDestination.route) {
        launchSingleTop = true
    }
}

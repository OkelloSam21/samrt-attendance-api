package com.smartattendance.android.feature.auth.login

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartattendance.android.feature.admin.dashboard.navigateToAdminDashboard
import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import com.smartattendance.android.feature.lecturer.dashboard.navigateToLecturerDashboard
import com.smartattendance.android.feature.student.dashboard.navigateToStudentDashboard
import kotlinx.serialization.Serializable

@Serializable
data object LoginDestination  {
    const val route = "login?userType={userType}"
}


fun NavController.navigateToLogin() {
    this.navigate(LoginDestination.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToDashBoard(userType: UserType) {
    when(userType) {
        UserType.STUDENT -> {
            navigateToStudentDashboard()
        }
        UserType.ADMIN -> {
            navigateToAdminDashboard()
        }
        UserType.LECTURER -> {
            navigateToLecturerDashboard()
        }
    }
}


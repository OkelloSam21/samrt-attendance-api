package com.smartattendance.android.navigation

import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import kotlinx.serialization.Serializable

// In a file like NavigationDestinations.kt or within your feature packages
object SelectUserTypeDestination {
    const val route = "select_user_type"
}

object LoginDestination {
    const val route = "login/{userType}"

    fun createRoute(userType: UserType): String = "login/${userType.name}"
}

object SignUpDestination {
    const val route = "sign_up/{userType}"
    fun createRoute(userType: UserType): String = "sign_up/${userType.name}"
}

object StudentDashboardDestination {
    const val route = "student_dashboard"
}

object LecturerDashboardDestination {
    const val route = "lecturer_dashboard"
}

object AdminDashboardDestination {
    const val route = "admin_dashboard"
}

@Serializable
object CreateSessionDestination


package com.smartattendance.android.navigation

import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import kotlinx.serialization.Serializable

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

object ScanQrDestination {
    const val route = "scan_qr"
}

object AttendanceHistoryDestination {
    const val route = "attendance_history"
}
@Serializable
object CreateSessionDestination


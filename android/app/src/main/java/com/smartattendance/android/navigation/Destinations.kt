package com.smartattendance.android.navigation

import com.smartattendance.android.feature.onboarding.selectusertype.UserType
import kotlinx.serialization.Serializable

// Authentication Destinations
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

// Student Destinations
object StudentDashboardDestination {
    const val route = "student_dashboard"
}

object ScanQrDestination {
    const val route = "scan_qr"
}

object AttendanceHistoryDestination {
    const val route = "attendance_history"
}

// Lecturer Destinations
object LecturerDashboardDestination {
    const val route = "lecturer_dashboard"
}

object CreateCourseDestination {
    const val route = "create_course/{lecturerId}"

    fun createRoute(lecturerId: String): String = "create_course/$lecturerId"
}

@Serializable
object CreateSessionDestination {
    const val route = "create_session/{courseId}"
    fun createRoute(courseId: String): String = "create_session/$courseId"
}

object SessionDetailDestination {
    const val route = "session_detail/{sessionId}"

    fun createRoute(sessionId: String): String = "session_detail/$sessionId"
}

object AttendanceReportDestination {
    const val route = "attendance_report/{courseId}"

    fun createRoute(courseId: String): String = "attendance_report/$courseId"
}

// Admin Destinations
object AdminDashboardDestination {
    const val route = "admin_dashboard"
}

object UserManagementDestination {
    const val route = "user_management"
}

object CourseManagementDestination {
    const val route = "course_management"
}

object ReportsDestination {
    const val route = "reports"
}

object UserDetailsDestination {
    const val route = "user_details/{userId}"

    fun createRoute(userId: String): String = "user_details/$userId"
}

object CourseDetailsDestination {
    const val route = "course_details/{courseId}"

    fun createRoute(courseId: String): String = "course_details/$courseId"
}

// Shared Destinations
object ProfileDestination {
    const val route = "profile"
}
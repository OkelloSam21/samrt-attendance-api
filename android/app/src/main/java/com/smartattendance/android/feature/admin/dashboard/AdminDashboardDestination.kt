package com.smartattendance.android.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.smartattendance.android.feature.admin.SeedCoursesScreen
import com.smartattendance.android.feature.admin.coursemanagement.CourseManagementScreen
import com.smartattendance.android.feature.admin.dashboard.AdminDashboardScreen

// Define the new destination routes
object AdminCourseManagementDestination {
    const val route = "admin_course_management"
}

object AdminSeedCoursesDestination {
    const val route = "admin_seed_courses"
}

// Extension functions for NavController
fun NavController.navigateToAdminCourseManagement() {
    this.navigate(AdminCourseManagementDestination.route)
}

fun NavController.navigateToAdminSeedCourses() {
    this.navigate(AdminSeedCoursesDestination.route)
}

// Extension functions for NavGraphBuilder
fun NavGraphBuilder.adminCourseManagementScreen(
    navController: NavController,
    onEditCourse: (String) -> Unit
) {
    composable(AdminCourseManagementDestination.route) {
        CourseManagementScreen(
            onNavigateBack = { navController.popBackStack() },
            onEditCourse = onEditCourse
        )
    }
}

fun NavGraphBuilder.adminSeedCoursesScreen(
    navController: NavController
) {
    composable(AdminSeedCoursesDestination.route) {
        SeedCoursesScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

// Extension to add all admin routes to the NavGraphBuilder
fun NavGraphBuilder.adminScreens(
    navController: NavController
) {
    composable(AdminDashboardDestination.route) {
        AdminDashboardScreen(
            onNavigateToUserManagement = {
                // TODO: Navigate to user management when implemented
            },
            onNavigateToCourseManagement = {
                navController.navigateToAdminCourseManagement()
            },
            onNavigateToSeedCourses = {
                navController.navigateToAdminSeedCourses()
            },
            onNavigateToReports = {
                // TODO: Navigate to reports when implemented
            },
            onNavigateToUserDetails = { userId ->
                // TODO: Navigate to user details when implemented
            },
            onNavigateToCourseDetails = { courseId ->
                // TODO: Navigate to course details when implemented
            }
        )
    }

    adminCourseManagementScreen(
        navController = navController,
        onEditCourse = { courseId ->
            // TODO: Navigate to course edit screen when implemented
        }
    )

    adminSeedCoursesScreen(
        navController = navController
    )
}
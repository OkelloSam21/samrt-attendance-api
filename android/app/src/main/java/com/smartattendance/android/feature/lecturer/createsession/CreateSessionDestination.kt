package com.smartattendance.android.feature.lecturer.createsession

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object CreateSessionDestination

fun NavController.navigateToCreateSession() {
    this.navigate(CreateSessionDestination) {
        launchSingleTop = true
    }
}
fun NavGraphBuilder.createSession() {
    composable<CreateSessionDestination> {
        CreateAttendanceSessionScreen()
    }
}

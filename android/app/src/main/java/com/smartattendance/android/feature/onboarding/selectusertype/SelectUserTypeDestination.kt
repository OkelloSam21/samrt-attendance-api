package com.smartattendance.android.feature.onboarding.selectusertype

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object SelectUserTypeDestination{
    const val route = "select_user_type"
}


enum class UserType {
    STUDENT, ADMIN, LECTURER
}

fun NavGraphBuilder.selectUserTypeScreen(
    navController: NavController
) {
    composable<SelectUserTypeDestination> {
        val viewModel: SelectUserTypeViewModel = hiltViewModel()
        val state = viewModel.uiState.collectAsStateWithLifecycle().value

//        // Collect navigation events from the ViewModel
//        LaunchedEffect(key1 = viewModel.navigationEvents) {
//            viewModel.navigationEvents.collect { event ->
//                when (event) {
//                    is SelectUserTypeNavigationEvent.NavigateToLogin -> {
//                        navController.navigateToLogin(userType = event.userType.name.lowercase())
//                    }
//                    is SelectUserTypeNavigationEvent.NavigateToSignUp -> {
//                        navController.navigateToSignUp(userType = event.userType)
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//        SelectUserTypeScreen(viewModel = viewModel)
    }
}

package com.smartattendance.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartattendance.android.domain.repository.UserPreferencesRepository
import com.smartattendance.android.navigation.MainNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep splash screen visible while determining initial destination
        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        enableEdgeToEdge()
        setContent {
            val startDestinationEventState = viewModel.startDestination.collectAsStateWithLifecycle()
            val startDestinationEvent = startDestinationEventState.value

            //Log.d("Navigation", "Start Destination Event: $startDestinationEvent") //for debugging

            MainNavHost(
                startDestinationEvent = startDestinationEvent, //Pass the event
                userPreferencesRepository = userPreferencesRepository
            )
        }
    }
}

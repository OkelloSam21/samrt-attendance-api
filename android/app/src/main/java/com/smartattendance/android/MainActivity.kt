package com.smartattendance.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.smartattendance.android.navigation.MainNavHost
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartattendance.android.feature.onboarding.selectusertype.SelectUserTypeDestination

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep splash screen visible while determining initial destination
        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        enableEdgeToEdge()
        setContent {
            val startDestinationState = viewModel.startDestination.collectAsStateWithLifecycle()
            val startDestination = startDestinationState.value ?: SelectUserTypeDestination.route

            // Debug print
            Log.d("Navigation", "Start Destination: $startDestination")
            // Only set content when start destination is determined
            MainNavHost(initialStartDestination = startDestination)
        }
    }
}


package com.smartattendance.android.feature.student.scanqr

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanScreen(
    viewModel: QrScanViewModel = hiltViewModel(),
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onClose: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan QR Code") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToDashboard() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Close, // Replace with appropriate icon
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already on QR screen */ },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Close, // Replace with appropriate icon
                            contentDescription = "Scan QR"
                        )
                    },
                    label = { Text("Scan QR") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToHistory() },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Close, // Replace with appropriate icon
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black)
        ) {
            // Camera Preview
            if (hasCameraPermission) {
                CameraPreview(
                    onQrCodeScanned = { code ->
                        viewModel.onEvent(QrScanEvent.QrCodeScanned(code))
                    }
                )

                // QR Code scanning frame
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Scanner frame
                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .border(
                                width = 2.dp,
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        // Top-left corner
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .border(
                                    width = 6.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(topStart = 12.dp)
                                )
                        )

                        // Top-right corner
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.TopEnd)
                                .border(
                                    width = 6.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(topEnd = 12.dp)
                                )
                        )

                        // Bottom-left corner
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomStart)
                                .border(
                                    width = 6.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(bottomStart = 12.dp)
                                )
                        )

                        // Bottom-right corner
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd)
                                .border(
                                    width = 6.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(bottomEnd = 12.dp)
                                )
                        )

                        // Scanning line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .align(Alignment.Center)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                // Instruction text
                Text(
                    text = "Position the QR code within the frame",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter)
                )

                // Bottom controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp) // Adjust for bottom nav
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { viewModel.onEvent(QrScanEvent.ManualCodeEntry) },
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "OR ENTER CODE MANUALLY",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Flashlight button
                    IconButton(
                        onClick = { viewModel.onEvent(QrScanEvent.ToggleFlashlight) },
                        modifier = Modifier
                            .size(75.dp)
                    ) {
                        Icon(Icons.Filled.FlashlightOn, contentDescription = "")
                    }
                }
            }
        }
    }
}
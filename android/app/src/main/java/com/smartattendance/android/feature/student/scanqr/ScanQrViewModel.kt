package com.smartattendance.android.feature.student.scanqr

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class StudentScanQrViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(StudentScanQrUiState())
    val uiState: StateFlow<StudentScanQrUiState> = _uiState.asStateFlow()

    // Flag to prevent multiple QR code processing at the same time
    private val isProcessing = AtomicBoolean(false)

    fun processQrCode(qrContent: String) {
        // If already processing, ignore new scans
        if (isProcessing.getAndSet(true)) return

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isScanning = true,
                        errorMessage = null,
                        successMessage = null
                    )
                }

                // Parse QR content
                val sessionData = parseQrContent(qrContent)

                if (sessionData != null) {
                    // Try to get current location
                    val location = locationRepository.getCurrentLocation()

                    // Mark attendance with session code and location if available
                    val result = attendanceRepository.markAttendance(
                        sessionCode = sessionData.sessionCode,
                        latitude = location?.latitude,
                        longitude = location?.longitude
                    )

                    if (result.isSuccess) {
                        _uiState.update {
                            it.copy(
                                isScanning = false,
                                successMessage = if (sessionData.courseName != "Class") {
                                    "Successfully marked attendance for ${sessionData.courseName}"
                                } else {
                                    "Attendance marked successfully!"
                                }
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isScanning = false,
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to mark attendance"
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isScanning = false,
                            errorMessage = "Invalid code format. Please try again or ask your instructor."
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        errorMessage = "Error: ${e.message ?: "Unknown error occurred"}"
                    )
                }
            } finally {
                // Reset processing flag after a short delay to prevent rapid successive scans
                viewModelScope.launch {
                    kotlinx.coroutines.delay(2000)
                    isProcessing.set(false)
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }

    private fun parseQrContent(qrContent: String): SessionData? {
        return try {
            val parts = qrContent.split(":")
            if (parts.size >= 2) {
                SessionData(
                    sessionCode = parts[0].trim(),
                    courseName = parts[1].trim()
                )
            } else {
                // Fallback to just using the raw content as session code
                // This handles manually entered codes as well
                val sanitizedCode = qrContent.trim()
                if (sanitizedCode.isNotEmpty()) {
                    SessionData(
                        sessionCode = sanitizedCode,
                        courseName = "Class"
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}

data class StudentScanQrUiState(
    val isScanning: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class SessionData(
    val sessionCode: String,
    val courseName: String
)
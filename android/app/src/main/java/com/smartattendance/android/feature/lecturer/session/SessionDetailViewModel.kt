package com.smartattendance.android.feature.lecturer.session

import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.smartattendance.android.domain.repository.AttendanceRepository
import com.smartattendance.android.domain.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.EnumMap
import javax.inject.Inject

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository,
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionDetailUiState())
    val uiState: StateFlow<SessionDetailUiState> = _uiState.asStateFlow()

    fun loadSessionDetails(sessionId: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // Get session details
                val session = attendanceRepository.getSessionById(sessionId).firstOrNull()
                
                if (session != null) {
                    // Get course details
                    val course = courseRepository.getCourseById(session.courseId).firstOrNull()
                    
                    // Get attendance records for this session
                    val attendanceRecords = attendanceRepository.getAttendancesBySessionId(sessionId).firstOrNull() ?: emptyList()
                    
                    val presentCount = attendanceRecords.count { it.status == "Present" }
                    
                    // Get total students (this is simplified - in a real app, you'd have a proper way to get this)
                    val totalStudents = 50 // Placeholder
                    
                    // Parse dates from strings
                    val startTime = try {
                        Date(session.createdAt.toLong())
                    } catch (e: Exception) {
                        Date()
                    }
                    
                    val endTime = try {
                        Date(session.expiresAt.toLong())
                    } catch (e: Exception) {
                        Date(System.currentTimeMillis() + 60 * 60 * 1000) // 1 hour from now as fallback
                    }
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            session = SessionDetailData(
                                id = session.id,
                                courseId = session.courseId,
                                sessionType = session.sessionType.name,
                                sessionCode = session.sessionCode,
                                startTime = startTime,
                                endTime = endTime
                            ),
                            courseName = course?.name ?: "Unknown Course",
                            presentCount = presentCount,
                            totalStudentsCount = totalStudents,
                            error = null
                        )
                    }
                    
                    // Generate QR code for the session
                    generateQrCode(session.sessionCode)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Session not found"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error loading session details: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun refreshQrCode(sessionId: String) {
        viewModelScope.launch {
            try {
                val session = attendanceRepository.getSessionById(sessionId).firstOrNull()
                if (session != null) {
                    generateQrCode(session.sessionCode)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to refresh QR code: ${e.message}")
                }
            }
        }
    }
    
    private fun generateQrCode(sessionCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingQrCode = true) }
            
            try {
                // Generate QR code
                val qrBitmap = generateQrCodeBitmap(sessionCode)
                
                _uiState.update {
                    it.copy(
                        isLoadingQrCode = false,
                        qrCodeBitmap = qrBitmap
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingQrCode = false,
                        error = "Failed to generate QR code: ${e.message}"
                    )
                }
            }
        }
    }
    
    private fun generateQrCodeBitmap(content: String, size: Int = 512): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.MARGIN] = 1 // Make the QR code a bit more compact
        
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        
        return bitmap
    }
    
    fun endSession(sessionId: String) {
        viewModelScope.launch {
            try {
                // In a real app, you'd call your repository to end the session
                // For now, we'll just update the UI state
                _uiState.update {
                    it.copy(
                        isSessionEnded = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to end session: ${e.message}")
                }
            }
        }
    }
}

data class SessionDetailUiState(
    val isLoading: Boolean = false,
    val isLoadingQrCode: Boolean = false,
    val session: SessionDetailData? = null,
    val courseName: String = "",
    val presentCount: Int = 0,
    val totalStudentsCount: Int = 0,
    val qrCodeBitmap: Bitmap? = null,
    val isSessionEnded: Boolean = false,
    val error: String? = null
)

data class SessionDetailData(
    val id: String,
    val courseId: String,
    val sessionType: String,
    val sessionCode: String,
    val startTime: Date,
    val endTime: Date
)
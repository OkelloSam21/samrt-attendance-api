package com.smartattendnance.core.data.network

import com.smartattendnance.core.data.network.model.AdminSignUpRequest
import com.smartattendnance.core.data.network.model.AttendanceResponse
import com.smartattendnance.core.data.network.model.AttendanceSession
import com.smartattendnance.core.data.network.model.AttendanceSessionRequest
import com.smartattendnance.core.data.network.model.AuthResponse
import com.smartattendnance.core.data.network.model.CourseRequest
import com.smartattendnance.core.data.network.model.CourseResponse
import com.smartattendnance.core.data.network.model.CourseUpdateRequest
import com.smartattendnance.core.data.network.model.CreateUserRequest
import com.smartattendnance.core.data.network.model.LecturerSignUpRequest
import com.smartattendnance.core.data.network.model.LoginRequest
import com.smartattendnance.core.data.network.model.MarkAttendanceRequest
import com.smartattendnance.core.data.network.model.QrCodeResponse
import com.smartattendnance.core.data.network.model.RefreshTokenRequest
import com.smartattendnance.core.data.network.model.StudentSignUpRequest
import com.smartattendnance.core.data.network.model.UserProfileResponse
import com.smartattendnance.core.data.network.util.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApiClient @Inject constructor(
    private val httpClient: HttpClient
) {
    // Authentication endpoints
    suspend fun login(loginRequest: LoginRequest): ApiResponse<AuthResponse> {
        return try {
            val response = httpClient.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): ApiResponse<AuthResponse> {
        return try {
            val response = httpClient.post("/auth/refresh") {
                contentType(ContentType.Application.Json)
                setBody(refreshTokenRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun registerStudent(request: StudentSignUpRequest): ApiResponse<AuthResponse> {
        return try {
            val response = httpClient.post("/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun registerLecturer(request: LecturerSignUpRequest): ApiResponse<AuthResponse> {
        return try {
            val response = httpClient.post("/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun registerAdmin(request: AdminSignUpRequest): ApiResponse<AuthResponse> {
        return try {
            val response = httpClient.post("/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    // User endpoints
    suspend fun getAllUsers(): ApiResponse<List<UserProfileResponse>> {
        return try {
            val response = httpClient.get("/users")
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun createUser(createUserRequest: CreateUserRequest): ApiResponse<UserProfileResponse> {
        return try {
            val response = httpClient.post("/users/create") {
                contentType(ContentType.Application.Json)
                setBody(createUserRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getUserById(userId: String): ApiResponse<UserProfileResponse> {
        return try {
            val response = httpClient.get("/users/$userId")
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun deleteUser(userId: String): ApiResponse<Unit> {
        return try {
            httpClient.delete("/users/delete/$userId")
            ApiResponse.Success(Unit)
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    // Course endpoints
    suspend fun createCourse(courseRequest: CourseRequest): ApiResponse<CourseResponse> {
        return try {
            val response = httpClient.post("/courses/create") {
                contentType(ContentType.Application.Json)
                setBody(courseRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getAllCourses(): ApiResponse<List<CourseResponse>> {
        return try {
            val response = httpClient.get("/courses")
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getCourseById(courseId: String): ApiResponse<CourseResponse> {
        return try {
            val response = httpClient.get("/courses/$courseId")
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun updateCourse(courseId: String, updateRequest: CourseUpdateRequest): ApiResponse<CourseResponse> {
        return try {
            val response = httpClient.put("/courses/update/$courseId") {
                contentType(ContentType.Application.Json)
                setBody(updateRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun deleteCourse(courseId: String): ApiResponse<Unit> {
        return try {
            httpClient.delete("/courses/delete/$courseId")
            ApiResponse.Success(Unit)
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    // Attendance endpoints
    suspend fun createAttendanceSession(sessionRequest: AttendanceSessionRequest): ApiResponse<AttendanceSession> {
        return try {
            val response = httpClient.post("/attendance/sessions") {
                contentType(ContentType.Application.Json)
                setBody(sessionRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getQrCodeForSession(): ApiResponse<QrCodeResponse> {
        return try {
            val response = httpClient.get("/attendance/sessions/qr")
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun markAttendance(markAttendanceRequest: MarkAttendanceRequest): ApiResponse<AttendanceResponse> {
        return try {
            val response = httpClient.post("/attendance/mark") {
                contentType(ContentType.Application.Json)
                setBody(markAttendanceRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }
}
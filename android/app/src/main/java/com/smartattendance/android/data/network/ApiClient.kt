package com.smartattendance.android.data.network

import android.util.Log
import com.smartattendance.android.data.network.model.*
import com.smartattendance.android.data.network.util.ApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.text.get

@Singleton
class ApiClient @Inject constructor(
    private val httpClient: HttpClient
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    // Authentication endpoints
    suspend fun login(loginRequest: LoginRequest): ApiResponse<LoginResponse> {
        return try {
            val response = httpClient.post("/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(loginRequest)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    ApiResponse.Success(response.body())
                }

                HttpStatusCode.Unauthorized -> {
                    val errorMessage = response.bodyAsText()
                    Log.e("ApiClient", "Authentication failed: $errorMessage")
                    ApiResponse.Error(errorMessage)
                }

                else -> {
                    val errorMessage = response.bodyAsText()
                    Log.e(
                        "ApiClient",
                        "Unexpected status: ${response.status}, Message: $errorMessage"
                    )
                    ApiResponse.Error("Unexpected error: ${response.status}")
                }
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Login error", e)
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun refreshToken(refreshTokenRequest: RefreshTokenRequest): ApiResponse<LoginResponse> {
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

    suspend fun registerUser(request: Any): ApiResponse<SignUpResponse> {
        return try {
            val response = httpClient.post("/auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> {
                    val responseBody = response.bodyAsText()
                    Log.d("ApiClient", "Response body: $responseBody")

                    runCatching {
                        // Use runCatching for more idiomatic Kotlin error handling
                        val signUpResponse = json.decodeFromString<SignUpResponse>(responseBody)
                        ApiResponse.Success(signUpResponse)
                    }.getOrElse { e ->
                        Log.e("ApiClient", "JSON parsing error", e)
                        ApiResponse.Error("Failed to parse response: ${e.message}")
                    }
                }

                HttpStatusCode.Conflict -> {
                    val errorMessage = response.bodyAsText()
                    Log.e("ApiClient", "Conflict error: $errorMessage")
                    ApiResponse.Error(errorMessage)
                }

                else -> {
                    val errorMessage = response.bodyAsText()
                    Log.e(
                        "ApiClient",
                        "Unexpected status: ${response.status}, Message: $errorMessage"
                    )
                    ApiResponse.Error("Unexpected error: ${response.status}")
                }
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "Network error", e)
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

    suspend fun getUserById(userId: String): ApiResponse<UserProfileResponse> {
        return try {
            val response = httpClient.get("/users/$userId")
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun createUser(createUserRequest: CreateUserRequest): ApiResponse<UserProfileResponse> {
        return try {
            val response = httpClient.post("/users") {
                contentType(ContentType.Application.Json)
                setBody(createUserRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun updateUser(userId: String, updateProfileRequest: UpdateProfileRequest): ApiResponse<UserProfileResponse> {
        return try {
            val response = httpClient.put("/users/$userId") {
                contentType(ContentType.Application.Json)
                setBody(updateProfileRequest)
            }
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
    suspend fun createCourse(courseRequest: CourseRequest): ApiResponse<CreateCourseApiResponse> {
        return try {
            val response = httpClient.post("/courses") {
                contentType(ContentType.Application.Json)
                setBody(courseRequest)
            }

            when {
                // Accept both 200 OK and 201 Created as success
                response.status.isSuccess() -> {
                    ApiResponse.Success(response.body())
                }

                response.status == HttpStatusCode.Unauthorized -> {
                    val errorMessage = response.bodyAsText()
                    Log.e("ApiClient", "Authentication failed: $errorMessage")
                    ApiResponse.Error(errorMessage)
                }

                else -> {
                    val errorMessage = response.bodyAsText()
                    Log.e(
                        "ApiClient",
                        "Unexpected status: ${response.status}, Message: $errorMessage"
                    )
                    ApiResponse.Error("Unexpected error: ${response.status}")
                }
            }
        } catch (e: Exception) {
            Log.e("ApiClient", "create course error", e)
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getAllCourses(): ApiResponse<List<CourseResponse>> {
        return try {
            val response = httpClient.get("/courses") {
                contentType(ContentType.Application.Json)
            }

            if (response.status == HttpStatusCode.OK) {
                ApiResponse.Success(response.body())
            } else {
                val errorMessage = response.bodyAsText()
                ApiResponse.Error(errorMessage)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    // In ApiClient.kt
    suspend fun getCourseById(courseId: String): ApiResponse<Course> {
        return try {
            val response = httpClient.get("/courses/$courseId")
            if (response.status.isSuccess()) {
                ApiResponse.Success(response.body())
            } else {
                val errorMessage = response.bodyAsText()
                ApiResponse.Error(errorMessage)
            }
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getCurseByLecturerId(lecturerId: String): ApiResponse<List<Course>> {
        return try {
            val response = httpClient.get("/courses/lecturer/$lecturerId")
            when {
                response.status.isSuccess() -> {
                    val courseListResponse: CourseListResponse = response.body()
                    ApiResponse.Success(courseListResponse.data ?: emptyList())
                }

                response.status == HttpStatusCode.Unauthorized -> {
                    val errorMessage = response.bodyAsText()
                    Log.e("ApiClient", "Authentication failed: $errorMessage")
                    ApiResponse.Error(errorMessage)
                }
                else -> {
                    val errorResponse: ErrorResponse? = try {
                        json.decodeFromString<ErrorResponse>(response.bodyAsText())
                    } catch (e: Exception) {
                        Log.e("ApiClient", "Error decoding error response: $e")
                        return ApiResponse.Error("An unknown error occurred")
                    }
                    val errorMessage = errorResponse?.error?.message ?: "An error occurred"
                    ApiResponse.Error(errorMessage)
                }
            }

        } catch (e: Exception) {
            Log.e("ApiClient", "getCurseByLecturerId error", e)
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun updateCourse(
        courseId: String,
        updateRequest: CourseUpdateRequest
    ): ApiResponse<Course> {
        return try {
            val response = httpClient.put("/courses/$courseId") {
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

    // New admin course management endpoints
    suspend fun adminCreateCourse(request: AdminCourseCreateRequest): ApiResponse<Course> {
        return try {
            val response = httpClient.post("/courses/admin/create") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            Log.e("ApiClient", "Admin create course error", e)
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun assignLecturerToCourse(courseId: String, lecturerId: String): ApiResponse<Course> {
        return try {
            val response = httpClient.post("/courses/admin/assign-lecturer/$courseId/$lecturerId") {
                contentType(ContentType.Application.Json)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            Log.e("ApiClient", "Assign lecturer error", e)
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun getAvailableLecturers(): ApiResponse<List<LecturerDto>> {
        return try {
            val response = httpClient.get("/courses/admin/lecturers")
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            Log.e("ApiClient", "Get lecturers error", e)
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

    suspend fun getQrCodeForSession(sessionId: String): ApiResponse<QrCodeResponse> {
        return try {
            val response = httpClient.get("/attendance/sessions/$sessionId/qr")
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }

    suspend fun markAttendance(markAttendanceRequest: MarkAttendanceRequest): ApiResponse<MarkAttendanceResponse> {
        return try {
            val response = httpClient.post("/attendance/mark") {
                contentType(ContentType.Application.Json)
                setBody(markAttendanceRequest)
            }
            ApiResponse.Success(response.body())
        } catch (e: Exception) {
            Log.e("APi client", "failed ${e.message}")
            ApiResponse.Error(e.message ?: "Unknown error occurred")
        }
    }
}

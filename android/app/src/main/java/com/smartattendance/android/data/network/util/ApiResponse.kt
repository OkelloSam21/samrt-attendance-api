package com.smartattendance.android.data.network.util

import kotlinx.serialization.Serializable

@Serializable
sealed class ApiResponse<out T> {
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val errorMessage: String) : ApiResponse<Nothing>()
}
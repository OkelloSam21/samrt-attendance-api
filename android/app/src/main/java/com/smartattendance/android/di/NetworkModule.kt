package com.smartattendance.android.di

import com.smartattendance.android.data.network.ApiClient
import com.smartattendance.android.data.network.auth.AuthInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.internal.connection.ConnectInterceptor.intercept
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    //    private const val BASE_URL = "https://smartattendance-backend-bug4bxgybhbwecey.canadacentral-01.azurewebsites.net/"
    private const val BASE_URL = "https://smart-attendance-api-image-production.up.railway.app/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun providesLogger(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

//    @Provides
//    @Singleton
//    fun providesOkHttpClient(
//        logger: HttpLoggingInterceptor,
//        authInterceptor: AuthInterceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(logger)
////            .addInterceptor(authInterceptor)
//            .addInterceptor(interceptor = authInterceptor)
//            .connectTimeout(10, TimeUnit.SECONDS)
//            .readTimeout(10, TimeUnit.SECONDS)
//            .writeTimeout(10, TimeUnit.SECONDS)
//            .build()
//    }

    @Provides
    @Singleton
    fun provideHttpClient(
        json: Json,
        authInterceptor: AuthInterceptor
    ): HttpClient {
        return HttpClient(Android) {
            // Configure the client
            install(ContentNegotiation) {
                json(json)
            }

            install(Logging) {
                level = LogLevel.BODY
                logger = Logger.DEFAULT
            }

            defaultRequest {
                // Apply base URL to all requests
                url(BASE_URL)

                // Add content type header
                header(HttpHeaders.ContentType, ContentType.Application.Json)

                // Add auth headers to all requests (except login/register)
                if (!url.encodedPath.contains("/auth/login") &&
                    !url.encodedPath.contains("/auth/register")) {
                    runBlocking {
                        authInterceptor.addAuthHeaders(this@defaultRequest)
                    }
                }
            }
        }
    }
}
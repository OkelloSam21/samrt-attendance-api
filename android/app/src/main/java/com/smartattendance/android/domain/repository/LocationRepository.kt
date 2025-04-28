package com.smartattendance.android.domain.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    private var lastKnownLocation: Location? = null
    
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        // Check if location services are enabled
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        if (!isLocationEnabled) {
            return null
        }
        
        // Try to get last known location first
        try {
            lastKnownLocation = fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            // Ignore errors with last location
        }
        
        // If we don't have a last location, request a new one
        if (lastKnownLocation == null) {
            lastKnownLocation = requestNewLocation()
        }
        
        return lastKnownLocation
    }
    
    @SuppressLint("MissingPermission")
    private suspend fun requestNewLocation(): Location? = suspendCancellableCoroutine { continuation ->
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()
        
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    fusedLocationClient.removeLocationUpdates(this)
                    lastKnownLocation = location
                    continuation.resume(location)
                } ?: run {
                    continuation.resume(null)
                }
            }
        }
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        
        continuation.invokeOnCancellation {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}

// Extension function to convert Google Task to Kotlin Coroutine
private suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result ->
        continuation.resume(result)
    }
    addOnFailureListener { exception ->
        continuation.resume(null as T)
    }
}
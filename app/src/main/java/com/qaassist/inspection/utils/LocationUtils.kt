package com.qaassist.inspection.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationUtils(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { continuation ->
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(15000)
                .build()
            
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (continuation.isActive) {
                        Log.i("LocationUtils", "Location obtained: $location")
                        continuation.resume(location)
                    }
                }
                .addOnFailureListener { exception ->
                    if (continuation.isActive) {
                        Log.e("LocationUtils", "Failed to get location", exception)
                        continuation.resume(null)
                    }
                }
                .addOnCanceledListener {
                    if (continuation.isActive) {
                        Log.w("LocationUtils", "Location request was canceled")
                        continuation.resume(null)
                    }
                }
        } catch (e: SecurityException) {
            Log.e("LocationUtils", "Location permission not granted", e)
            if (continuation.isActive) {
                continuation.resume(null)
            }
        } catch (e: Exception) {
            Log.e("LocationUtils", "Unexpected error getting location", e)
            if (continuation.isActive) {
                continuation.resume(null)
            }
        }
    }
    
    fun formatCoordinates(latitude: Double?, longitude: Double?): String {
        return if (latitude != null && longitude != null) {
            "Lat: ${"%.6f".format(latitude)}, Lng: ${"%.6f".format(longitude)}"
        } else {
            "Location not available"
        }
    }
    
    fun formatSingleCoordinate(coordinate: Double?, isLatitude: Boolean = true): String {
        return if (coordinate != null) {
            val prefix = if (isLatitude) "Lat" else "Lng"
            "$prefix: ${"%.6f".format(coordinate)}"
        } else {
            "Not available"
        }
    }
    
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}
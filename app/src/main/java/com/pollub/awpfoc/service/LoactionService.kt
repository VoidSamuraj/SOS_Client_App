package com.pollub.awpfoc.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pollub.awpfoc.BASE_WEBSOCKET_URL
import kotlinx.coroutines.*
import com.pollub.awpfoc.R
import com.pollub.awpfoc.data.SharedPreferencesManager
import com.pollub.awpfoc.network.NetworkClient.WebSocketManager

class LocationService : Service() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //associate coroutine with scope to easy cancel coroutine
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    var customerId: Int? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        customerId = SharedPreferencesManager.getUser().toCustomerInfo().id
        startForeground(1, createNotification())
        WebSocketManager.setOnReportFinished { stopSelf() }
        WebSocketManager.connect(BASE_WEBSOCKET_URL)
        sendStartupInfo()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
        job.cancel()
        WebSocketManager.disconnect()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
    private fun sendStartupInfo() {
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                    if (location != null) {
                        val locationData = """
                        {"callReport":true, "userId":${customerId},"latitude": ${location.latitude}, "longitude": ${location.longitude}}
                    """.trimIndent()

                        scope.launch {
                            try {
                                WebSocketManager.sendMessage(locationData)
                                Log.d("LocationService", "Initial location sent")
                            } catch (e: Exception) {
                                Log.e(
                                    "LocationService",
                                    "Error sending initial location: ${e.message}"
                                )
                            }
                        }
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    fun sendReconnectMessage(
        location: Location
    ) {
        val locationData = """
                        {"reconnectMessage":true, "userId":${customerId},"latitude": ${location.latitude}, "longitude": ${location.longitude}}
                    """.trimIndent()

        scope.launch {
            try {
                WebSocketManager.sendMessage(locationData)
                Log.d("LocationService", "Initial location sent")
            } catch (e: Exception) {
                Log.e(
                    "LocationService",
                    "Error sending initial location: ${e.message}"
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L // every 10 seconds
        )
            .setMinUpdateIntervalMillis(10000L)
            .setMaxUpdateDelayMillis(10000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        if (WebSocketManager.isConnecting.value)
                            sendReconnectMessage(location)
                        else
                            sendLocationToServer(location)
                    }
                }
            },
            Looper.getMainLooper()
        )
    }

    private fun sendLocationToServer(location: Location) {
        if (WebSocketManager.lastReportId != -1) {
            val locationData = """
                            {"reportId":${WebSocketManager.lastReportId},"userId":${customerId},"latitude": ${location.latitude}, "longitude": ${location.longitude}}
                            """.trimIndent()

            scope.launch {
                try {
                    WebSocketManager.sendMessage(locationData)
                } catch (e: Exception) {
                    Log.e("LocationService", "Error sending location: ${e.message}")
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        WebSocketManager.sendMessage("""{"reportId":${WebSocketManager.lastReportId},"status": cancel}""".trimIndent())
        fusedLocationClient.removeLocationUpdates(object : LocationCallback() {})
    }

    private fun createNotification(): Notification {
        val channelId = "location_service_channel"
        val channelName = "Location Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Firma Ochroniarska")
            .setContentText("Twoja lokalizacja jest udostępniana ochroniarzowi.")
            .setSmallIcon(R.drawable.baseline_navigation_24)
            .build()
    }
}


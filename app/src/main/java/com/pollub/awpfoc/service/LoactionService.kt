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
import android.app.PendingIntent
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pollub.awpfoc.BASE_WEBSOCKET_URL
import com.pollub.awpfoc.MainActivity
import kotlinx.coroutines.*
import com.pollub.awpfoc.R
import com.pollub.awpfoc.data.SharedPreferencesManager
import com.pollub.awpfoc.network.NetworkClient.WebSocketManager
import com.pollub.awpfoc.utils.TokenManager
import com.pollub.awpfoc.utils.TokenManager.isRefreshTokenExpired

class LocationService : Service() {

    private var locationCallback: LocationCallback? = null

    companion object{
        private const val UPDATE_LOCATION_INTERVAL= 10_000L
        private const val CHECK_TOKEN_INTERVAL_COUNT = TokenManager.TOKEN_EXPIRATION_THRESHOLD*500/UPDATE_LOCATION_INTERVAL
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var tokenCheckCounter=0

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
        WebSocketManager.disconnect()
        stopLocationUpdates()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * Sends info to call support, containing userId,
     */
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

    /**
     * Sends info to reconnect, containing userId,
     */
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

    /**
     * Starts sending location updates in 10s intervals
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10_000L // every 10 seconds
        )
            .setMinUpdateIntervalMillis(10_000L)
            .setMaxUpdateDelayMillis(10_000L)
            .build()
        locationCallback=object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                println("refresh $tokenCheckCounter / $CHECK_TOKEN_INTERVAL_COUNT ")
                if(tokenCheckCounter>=CHECK_TOKEN_INTERVAL_COUNT){
                    tokenCheckCounter=0
                    if (isRefreshTokenExpired()){
                        WebSocketManager.disconnect()
                        return
                    }
                    runBlocking{TokenManager.refreshTokenIfNeeded()}
                    /*else if (runBlocking{TokenManager.refreshTokenIfNeeded()!=null}) {
                        println("refreshexpired2")
                        WebSocketManager.disconnect()
                        WebSocketManager.connect(BASE_WEBSOCKET_URL)
                        sendStartupInfo()
                        return
                    }*/
                }else
                    ++tokenCheckCounter

                for (location in locationResult.locations) {
                    if (WebSocketManager.isConnecting.value)
                        sendReconnectMessage(location)
                    else
                        sendLocationToServer(location)
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    /**
     * sending location data, with report and user ID
     */
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
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    /**
     * Displays notifications on device
     */
    private fun createNotification(): Notification {
        val channelId = "location_service_channel"
        val channelName = "Location Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Firma Ochroniarska")
            .setContentText("System śledzi twoją lokalizację.")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.baseline_navigation_24)
            .build()
    }
}


package com.pollub.awpfoc.utils

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.pollub.awpfoc.data.SharedPreferencesManager
import com.pollub.awpfoc.network.NetworkClient
import com.pollub.awpfoc.service.LocationService
import com.pollub.awpfoc.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class WearOsListener : WearableListenerService() {

    companion object {
        var viewModelInstance: AppViewModel? = null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("WearApp", "WearableListenerService created")
        SharedPreferencesManager.init(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val userExpDate = SharedPreferencesManager.getUser().protection_expiration_date

        val isActive = if (userExpDate != null) {
            val expirationDate =
                LocalDateTime.parse(userExpDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            LocalDateTime.now().isBefore(expirationDate)
        } else false

        Log.d("WearApp", messageEvent.path)
        when (messageEvent.path) {
            "/check_token" -> {
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        val isValid = if (TokenManager.isRefreshTokenExpired()) {
                            false
                        } else {
                            TokenManager.refreshTokenIfNeeded()
                            true
                        }

                        sendToWear(
                            "/token_status",
                            (if (isValid) "valid" else "invalid") +
                                    (if (isActive) "" else " protection_expired") +
                                    (if (viewModelInstance != null) " status_${viewModelInstance!!.reportState.value}" else "")
                        )
                    } catch (e: Exception) {
                        Log.e("WearApp", "Error handling message", e)
                    }
                }
            }

            "/start_sos" -> {
                val userExpDate = SharedPreferencesManager.getUser().protection_expiration_date
                if (userExpDate != null) {
                    val expirationDate =
                        LocalDateTime.parse(userExpDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    val isActive = LocalDateTime.now().isBefore(expirationDate)
                    if (isActive) {
                        NetworkClient.WebSocketManager.executeOnStart {
                            viewModelInstance?.isSosActive?.value = true
                            viewModelInstance?.reportState?.value = AppViewModel.Companion.ReportState.WAITING
                            CoroutineScope(Dispatchers.IO).launch {
                                sendToWear("/start_sos", "started")
                            }
                        }
                        this.startService(Intent(this, LocationService::class.java))
                    } else {
                        CoroutineScope(Dispatchers.IO).launch {
                            sendToWear("/start_sos", "protection_expired")
                        }
                    }

                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        sendToWear("/start_sos", "no_logged_in")
                    }
                }

            }

            "/end_sos" -> {
                NetworkClient.WebSocketManager.executeOnClose {
                    viewModelInstance?.isSosActive?.value = false
                    viewModelInstance?.reportState?.value = AppViewModel.Companion.ReportState.NONE
                    CoroutineScope(Dispatchers.IO).launch {
                        sendToWear("/end_sos", "stopped")
                    }
                }
                NetworkClient.WebSocketManager.setCloseCode(4000)
                this.stopService(Intent(this, LocationService::class.java))

            }

            else -> {
                Log.e("WearApp", "Unknown message path: ${messageEvent.path}")
            }
        }
    }

    private suspend fun sendToWear(path: String, message: String) {
        val nodeId = getConnectedNodeId()
        if (nodeId != null)
            Wearable.getMessageClient(this)
                .sendMessage(nodeId, path, message.toByteArray())
        else
            Log.e("WearApp", "Failed to send message to Wear OS, no node")
    }


    private suspend fun getConnectedNodeId(): String? {
        return suspendCoroutine { continuation ->
            val nodeClient = Wearable.getNodeClient(this)
            nodeClient.connectedNodes.addOnSuccessListener { nodes ->
                if (nodes.isNotEmpty()) {
                    continuation.resume(nodes[0].id)
                } else {
                    Log.e("WearApp", "No connected nodes found")
                    continuation.resume(null)
                }
            }.addOnFailureListener {
                Log.e("WearApp", "Failed to get connected nodes", it)
                continuation.resume(null)
            }
        }
    }
}
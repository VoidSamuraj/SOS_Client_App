package com.pollub.awpfowc.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.wearable.Wearable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    var hasProtectExpired: Boolean = false

    suspend fun checkToken(callOnNoConnection: () -> Unit, callOnConnection: () -> Unit) {
        val nodeId = getConnectedNodeId()
        if (nodeId != null)
            Wearable.getMessageClient(context).sendMessage(nodeId, "/check_token", ByteArray(0))
                .addOnSuccessListener {
                    callOnConnection()
                }
                .addOnFailureListener {
                    callOnNoConnection()
                }
        else
            callOnNoConnection()
    }

    suspend fun sendSOSRequest(start: Boolean, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val nodeId = getConnectedNodeId()
        if (nodeId != null)
            Wearable.getMessageClient(context)
                .sendMessage(nodeId, (if (start) "/start_sos" else "/end_sos"), ByteArray(0))
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onFailure()
                }
        else
            onFailure()
    }

    suspend fun addListener(
        onTokenActive: () -> Unit,
        onTokenExpire: () -> Unit,
        onProtectExpiration: () -> Unit,
        onStartedSOS: () -> Unit,
        onStoppedSOS: () -> Unit,
        onFailure: () -> Unit
    ) {
        Wearable.getMessageClient(context).addListener { messageEvent ->
            if (messageEvent.path == "/token_status") {
                val status = String(messageEvent.data)
                if (status.contains("invalid"))
                    onTokenExpire()
                else {
                    if (status.contains("protection_expired")) {
                        hasProtectExpired = true
                        onProtectExpiration()
                    } else
                        onTokenActive()
                }

            } else if (messageEvent.path == "/start_sos") {
                val status = String(messageEvent.data)
                when (status) {
                    "started" -> {
                        onStartedSOS()
                    }

                    "protection_expired" -> {
                        hasProtectExpired = true
                        onProtectExpiration()
                    }

                    "no_logged_in" -> {
                        onTokenExpire()
                    }

                    else ->
                        onFailure()
                }
            } else if (messageEvent.path == "/end_sos") {
                val status = String(messageEvent.data)
                if (status == "stopped")
                    onStoppedSOS()
                else
                    onFailure()
            }
        }
    }

    private suspend fun getConnectedNodeId(): String? {
        return suspendCoroutine { continuation ->
            val nodeClient = Wearable.getNodeClient(context)
            nodeClient.connectedNodes.addOnSuccessListener { nodes ->
                if (nodes.isNotEmpty()) {
                    continuation.resume(nodes[0].id)
                } else {
                    continuation.resumeWithException(Exception("No connected nodes found"))
                }
            }
        }
    }

}
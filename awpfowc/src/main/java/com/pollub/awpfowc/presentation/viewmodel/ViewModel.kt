package com.pollub.awpfowc.presentation.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.wearable.Wearable
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    var hasProtectExpired: Boolean = false

    companion object {
        enum class ReportState {
            CONFIRMED,
            WAITING,
            NONE
        }
    }

    val reportState = mutableStateOf(ReportState.NONE)

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
            val status = String(messageEvent.data)
            when (messageEvent.path) {
                "/token_status" -> {
                    if (status.contains("invalid"))
                        onTokenExpire()
                    else {
                        if (status.contains("protection_expired")) {
                            hasProtectExpired = true
                            onProtectExpiration()
                        } else {
                            if (status.contains("status_NONE"))
                                reportState.value = ReportState.NONE
                            else if (status.contains("status_WAITING")) {
                                reportState.value = ReportState.WAITING
                                onStartedSOS()
                            } else if (status.contains("status_CONFIRMED")) {
                                reportState.value = ReportState.CONFIRMED
                                onStartedSOS()
                            }
                            onTokenActive()
                        }
                    }

                }

                "/start_sos" -> {
                    when (status) {
                        "started" -> {
                            reportState.value = ReportState.WAITING
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
                }

                "/end_sos" -> {
                    if (status == "stopped")
                        onStoppedSOS()
                    else
                        onFailure()
                }

                "/sos_status" -> {
                    when (status) {
                        "confirmed" -> {
                            reportState.value = ReportState.CONFIRMED
                        }

                        "waiting" -> {
                            reportState.value = ReportState.WAITING
                        }

                        "finished" -> {
                            reportState.value = ReportState.NONE
                            onStoppedSOS()
                        }
                    }
                }
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
                    continuation.resume(null)
                }
            }
        }
    }

}
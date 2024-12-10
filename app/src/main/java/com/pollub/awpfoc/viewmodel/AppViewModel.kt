package com.pollub.awpfoc.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import com.pollub.awpfoc.data.SharedPreferencesManager
import com.pollub.awpfoc.data.models.CustomerInfo
import com.pollub.awpfoc.network.NetworkClient
import com.pollub.awpfoc.network.NetworkClient.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * ViewModel that manages user authentication and customer-related operations in the application.
 * It interacts with the UserRepository to perform actions like login, registration, customer information editing,
 * password reminders, and token validation. SharedPreferencesManager is used to save or clear user data.
 */
class AppViewModel : ViewModel() {

    companion object{
        enum class ReportState{
            CONFIRMED,
            WAITING,
            NONE
        }
    }
    val reportState = mutableStateOf(ReportState.NONE)

    val isSystemConnected = mutableStateOf(false)
    val isSmartWatchConnected = mutableStateOf(false)
    val isSosActive = mutableStateOf(false)

    fun getIsSystemConnecting() = WebSocketManager.isConnecting

    /**
     * Checks if the provided login is not already in use.
     *
     * @param login The login string to be checked.
     * @param onSuccess Callback function to be executed if the login is not in use.
     * @param onFailure Callback function to be executed with an error message if the login is already used or if an error occurs.
     */
    fun isLoginNotUsed(login: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        NetworkClient.userRepository.isLoginUsed(login, onSuccess = { isUsed ->
            if (isUsed)
                onFailure("Login is already used")
            else
                onSuccess()
        },
            error = { error ->
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            })
    }

    /**
     * Logs in a user with the provided login and password.
     *
     * @param login The login string.
     * @param password The password string.
     * @param onSuccess Callback function to be executed on successful login.
     * @param onFailure Callback function to be executed with an error message if login fails.
     */
    fun login(
        login: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        NetworkClient.userRepository.login(login, password) { client, error, longTimeToken ->
            if (client != null) {
                SharedPreferencesManager.saveUser(client)
                longTimeToken?.let {
                    SharedPreferencesManager.saveSecureToken(it)
                }
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }


    /**
     * Logs out the currently logged-in user.
     *
     * @param onSuccess Callback function to be executed on successful logout.
     * @param onFailure Callback function to be executed with an error message if logout fails.
     */
    fun logout(onSuccess: () -> Unit, onFailure: (message: String) -> Unit) {
        NetworkClient.userRepository.logout { success, error ->
            if (success) {
                SharedPreferencesManager.clear()
                SharedPreferencesManager.removeSecureToken()
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }

    /**
     * Registers a new customer with the provided login, password, and customer information.
     *
     * @param login The login string for the new customer.
     * @param password The password string for the new customer.
     * @param customer The CustomerInfo object containing customer details.
     * @param onSuccess Callback function to be executed on successful registration.
     * @param onFailure Callback function to be executed with an error message if registration fails.
     */
    fun register(
        login: String,
        password: String,
        customer: CustomerInfo,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        NetworkClient.userRepository.register(
            login,
            password,
            customer
        ) { client, error, longTimeToken ->
            if (client != null) {
                SharedPreferencesManager.saveUser(client)
                longTimeToken?.let {
                    SharedPreferencesManager.saveSecureToken(it)
                }
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }

    /**
     * Edits customer information based on the provided parameters.
     *
     * @param id The customer's ID.
     * @param login Optional new login string.
     * @param password Current password string.
     * @param newPassword Optional new password string.
     * @param name Optional new first name string.
     * @param surname Optional new surname string.
     * @param email Optional new email address string.
     * @param phone Optional new phone number string.
     * @param pesel Optional new PESEL (Polish national identification number).
     * @param onSuccess Callback function to be executed on successful update.
     * @param onFailure Callback function to be executed with an error message if the update fails.
     */
    fun editCustomer(
        id: Int,
        login: String?,
        password: String,
        newPassword: String?,
        name: String?,
        surname: String?,
        email: String?,
        phone: String?,
        pesel: String?,
        onSuccess: () -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        NetworkClient.userRepository.editCustomer(
            id = id,
            login = login,
            password = password,
            newPassword = newPassword,
            name = name,
            surname = surname,
            email = email,
            phone = phone,
            pesel = pesel,
            callback = { customer, error ->
                if (customer != null) {
                    SharedPreferencesManager.saveUser(customer)
                    onSuccess()
                } else {
                    error?.let {
                        onFailure(error)
                    }
                    Log.e("AuthViewModel", error ?: "Unknown error")
                }
            }
        )
    }

    /**
     * Validates the client's token and retrieves customer information if the token is valid.
     *
     * @param token The client token string to be validated.
     * @param onSuccess Callback function to be executed on successful validation.
     * @param onFailure Callback function to be executed with an error message if validation fails.
     */
    fun checkClientToken(
        token: String,
        onSuccess: () -> Unit,
        onFailure: (message: String) -> Unit
    ) {
        NetworkClient.userRepository.checkClientToken(token) { customer, error ->
            if (customer != null) {
                SharedPreferencesManager.saveUser(customer)
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }

    /**
     * Sends a password reminder to the specified email.
     *
     * @param email The email address to send the password reminder to.
     * @param onSuccess Callback function to be executed on successful reminder.
     * @param onFailure Callback function to be executed with an error message if the reminder fails.
     */
    fun remindPassword(email: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        NetworkClient.userRepository.remindPassword(
            email,
            onSuccess = onSuccess,
            onFailure = { error ->
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            })
    }

    /**
     *  empty call checking if connection is available
     *
     *  @param onSuccess Callback executed if connection available
     *  @param onFailure Callback executed if connection unavailable
     */
    fun checkIfConnected(onSuccess: () -> Unit, onFailure: () -> Unit) {
        NetworkClient.userRepository.isConnected(onSuccess = onSuccess, onFailure = onFailure)
    }

    inner class ObserveIfConnectionAvailable(
        private val context: Context,
        private val lifecycleOwner: LifecycleOwner,
        private val invokeIfConnected: () -> Unit,
        private val invokeIfDisconnected: () -> Unit,
        private val invokeIfWatchConnected: () -> Unit,
        private val invokeIfWatchDisconnected: () -> Unit
    ) : DefaultLifecycleObserver {

        private var job: Job? = null
        private var delay_time = 10_000L
        override fun onStart(owner: LifecycleOwner) {
            job = CoroutineScope(Dispatchers.Main).launch() {
                while (isActive && lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {

                    if (WebSocketManager.isServiceStopping) {
                        checkIfConnected(onSuccess = {
                            delay_time = 15_000L
                            invokeIfConnected()
                        },
                            onFailure = {
                                delay_time = 5_000L
                                invokeIfDisconnected()
                            })
                    }
                    if(isWearableConnected(context)){
                        invokeIfWatchConnected()
                    }else{
                        invokeIfWatchDisconnected()
                    }
                    delay(delay_time)
                }
            }
        }

        override fun onStop(owner: LifecycleOwner) {
            job?.cancel()
        }
    }

    private suspend fun isWearableConnected(context: Context): Boolean {
        return try {
            val nodes: List<Node> = Wearable.getNodeClient(context).connectedNodes.await()
            nodes.isNotEmpty()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun sendStartSOSToWear(context: Context) {
        val nodeId = getConnectedNodeId(context)
        if (nodeId != null)
            Wearable.getMessageClient(context)
                .sendMessage(nodeId, "/start_sos", "started".toByteArray())
                .addOnSuccessListener {
                    Log.d("WearApp", "start sent to Wear OS")
                }
                .addOnFailureListener {
                    Log.e("WearApp", "Failed to send start status to wear Os", it)
                }
        else
            Log.e("WearApp", "Failed to send start status to Wear OS, no node")

    }

    suspend fun sendStopSOSToWear(context: Context) {
        val nodeId = getConnectedNodeId(context)
        if (nodeId != null)
            Wearable.getMessageClient(context)
                .sendMessage(nodeId, "/end_sos", "stopped".toByteArray())
                .addOnSuccessListener {
                    Log.d("WearApp", "stop status sent to Wear OS")
                }
                .addOnFailureListener {
                    Log.e("WearApp", "Failed to send stop status to wear Os", it)
                }
        else
            Log.e("WearApp", "Failed to send stop status to Wear OS, no node")
    }
    suspend fun sendLoggedInToWear(context: Context, isLoggedIn:Boolean) {
        val message = if (isLoggedIn) "valid" else "invalid"
        val nodeId = getConnectedNodeId(context)
        if (nodeId != null)
            Wearable.getMessageClient(context)
                .sendMessage(nodeId, "/token_status", message.toByteArray())
        else
            Log.e("WearApp", "Failed to send stop status to Wear OS, no node")
    }

    private suspend fun getConnectedNodeId(context: Context): String? {
        return suspendCoroutine { continuation ->
            val nodeClient = Wearable.getNodeClient(context)
            nodeClient.connectedNodes.addOnSuccessListener { nodes ->
                if (nodes.isNotEmpty()) {
                    Log.d("WearApp", "Connected node found: ${nodes[0].id}")
                    continuation.resume(nodes[0].id)
                } else {
                    Log.e("WearApp", "No connected nodes found")
                    continuation.resumeWithException(Exception("No connected nodes found"))
                }
            }.addOnFailureListener {
                Log.e("WearApp", "Failed to get connected nodes", it)
                continuation.resumeWithException(it)
            }
        }
    }
}
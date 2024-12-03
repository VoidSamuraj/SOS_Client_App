package com.pollub.awpfoc.repository

import android.util.Log
import com.pollub.awpfoc.data.ApiService
import com.pollub.awpfoc.data.models.Credentials
import com.pollub.awpfoc.data.models.Customer
import com.pollub.awpfoc.data.models.CustomerInfo
import com.pollub.awpfoc.data.models.JWTToken
import com.pollub.awpfoc.data.models.TokenResponse
import com.pollub.awpfoc.network.NetworkClient
import com.pollub.awpfoc.utils.TokenManager
import com.pollub.awpfoc.utils.TokenManager.isRefreshTokenExpired
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Repository class for handling user-related API calls through the ApiService.
 */
class UserRepository {
    // Instance of ApiService for making network requests
    private val apiService: ApiService = NetworkClient.instance

    private fun String?.filterString():String?{
        return this?.replace(Regex("\\b\\d{1,3}(\\.\\d{1,3}){3}:\\d+\\b"), "")?. replace(" to","")?. replace("/","")
    }
    suspend fun refreshToken(refreshToken: String): TokenResponse? {
        val response = apiService.refreshToken(refreshToken = refreshToken)
        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e("UserRepository.isLoginUsed", "" + response.errorBody()?.string())
            null
        }
    }

    suspend fun refreshRefreshToken(refreshToken: String): TokenResponse? {
        val response = apiService.refreshRefreshToken(refreshToken = refreshToken)
        return if (response.isSuccessful) {
            response.body()
        } else {
            Log.e("UserRepository.isLoginUsed", "" + response.errorBody()?.string())
            null
        }
    }

    /**
     * Checks if a given login is already used in the system.
     *
     * @param login The login to check.
     * @param onSuccess Callback function that receives a Boolean indicating if the login is used.
     * @param error Callback function that handles errors by receiving an error message string.
     */
    fun isLoginUsed(
        login: String,
        onSuccess: (isUsed: Boolean) -> Unit,
        error: (errorMessage: String?) -> Unit
    ) {
        apiService.isLoginUsed(login).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    response.body()?.let { isLoginUsed ->
                        onSuccess(isLoginUsed)
                    }
                } else {
                    val filteredError = response.errorBody()?.string()?.filterString()
                    error(filteredError)
                    Log.e("UserRepository.isLoginUsed", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<Boolean?>, t: Throwable) {
                val filteredError = t.message?.filterString()
                error(filteredError)
                Log.e("UserRepository.isLoginUsed", "" + t.message)
            }
        })
    }

    /**
     *  empty call checking if connection is available
     *  @param onSuccess Callback executed if connection available
     *  @param onFailure Callback executed if connection unavailable
     */
    fun isConnected(onSuccess: () -> Unit, onFailure: () -> Unit) {
        apiService.isConnectionAvailable().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                onFailure()
            }
        })
    }

    /**
     * Logs a user into the system by verifying credentials.
     *
     * @param login The user's login.
     * @param password The user's password.
     * @param callback Callback function that receives a Customer object and LongTimeToken on success, or an error message on failure.
     */
    fun login(login: String, password: String, callback: (Customer?, String?, String?) -> Unit) {
        val credentials = Credentials(login, password)
        apiService.loginClient(credentials)
            .enqueue(object : Callback<Triple<String, CustomerInfo, JWTToken>> {
                override fun onResponse(
                    call: Call<Triple<String, CustomerInfo, JWTToken>>,
                    response: Response<Triple<String, CustomerInfo, JWTToken>>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { customer ->
                            if (customer.second.token != null) {
                                callback(
                                    Customer.fromCustomerInfo(
                                        customerInfo = customer.second,
                                        login = customer.first,
                                        password = ""
                                    ), null,
                                    customer.third.token
                                )
                            } else {
                                callback(null, "Error: No token", null)
                            }
                        }
                    } else {
                        val filteredError = response.errorBody()?.string()?.filterString()
                        callback(null, filteredError, null)
                        Log.e("UserRepository.login", "" + response.errorBody()?.string())
                    }
                }

                override fun onFailure(
                    call: Call<Triple<String, CustomerInfo, JWTToken>>,
                    t: Throwable
                ) {
                    val filteredError = t.message.toString().filterString()
                    callback(null, filteredError, null)
                    Log.e("UserRepository.login", t.message.toString())
                }
            })
    }

    /**
     * Registers a new customer in the system.
     *
     * @param login The login to register.
     * @param password The password for the account.
     * @param client CustomerInfo containing additional user details.
     * @param callback Callback function that receives a Customer object and LongTimeToken on success, or an error message on failure.
     */
    fun register(
        login: String,
        password: String,
        client: CustomerInfo,
        callback: (Customer?, String?, String?) -> Unit
    ) {

        apiService.registerClient(
            login = login,
            password = password,
            name = client.name,
            surname = client.surname,
            email = client.email,
            phone = client.phone,
            pesel = client.pesel
        ).enqueue(object : Callback<Triple<String, CustomerInfo, JWTToken>> {
            override fun onResponse(
                call: Call<Triple<String, CustomerInfo, JWTToken>>,
                response: Response<Triple<String, CustomerInfo, JWTToken>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { customer ->
                        if (customer.second.token != null) {
                            callback(
                                Customer.fromCustomerInfo(
                                    customerInfo = customer.second,
                                    login = customer.first,
                                    password = ""
                                ), null,
                                customer.third.token
                            )
                        } else {

                            callback(null, "Error: No token", null)
                        }
                    }
                } else {
                    val filteredError = response.errorBody()?.string()?.filterString()
                    callback(null, filteredError, null)
                    Log.e("UserRepository.register", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(
                call: Call<Triple<String, CustomerInfo, JWTToken>>,
                t: Throwable
            ) {
                val filteredError = t.message.filterString()
                callback(null, filteredError, null)
                Log.e("UserRepository.register", t.message.toString())
            }
        })
    }

    /**
     * Logs the user out of the system.
     *
     * @param callback Callback function that receives true on successful logout, or false with an error message.
     */
    fun logout(callback: (Boolean, String?) -> Unit) {
        apiService.logoutClient().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    callback(true, null)
                } else {
                    val filteredError = response.errorBody()?.string()?.filterString()
                    callback(false, filteredError)
                    Log.e("UserRepository.logout", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                val filteredError = t.message.filterString()
                callback(false, filteredError)
                Log.e("UserRepository.logout", t.message.toString())
            }
        })
    }

    /**
     * Edits the details of an existing customer.
     *
     * @param id The customer's ID.
     * @param login The new login (optional).
     * @param password The current password for authentication.
     * @param newPassword The new password (optional).
     * @param name The customer's new name (optional).
     * @param surname The customer's new surname (optional).
     * @param email The customer's new email (optional).
     * @param phone The customer's new phone number (optional).
     * @param pesel The customer's new PESEL (optional).
     * @param callback Callback function that receives an updated Customer object on success, or an error message on failure.
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
        callback: (Customer?, String?) -> Unit
    ) {
        //check if RefreshToken is valid
        if (isRefreshTokenExpired()) {
            callback(null, "Error: Need authorization")
            return
        }
        //refresh if Refresh AccessToken if needed
        if (runBlocking {
                if (TokenManager.refreshTokenIfNeeded() == null) {
                    callback(null, "Error: Need authorization")
                    return@runBlocking true
                }
                return@runBlocking false
            })
            return

        apiService.editClient(
            id = id,
            login = login,
            password = password,
            newPassword = newPassword,
            name = name,
            surname = surname,
            email = email,
            phone = phone,
            pesel = pesel
        ).enqueue(object : Callback<Pair<String, CustomerInfo>> {
            override fun onResponse(
                call: Call<Pair<String, CustomerInfo>>,
                response: Response<Pair<String, CustomerInfo>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { customer ->
                        Log.d("EIDTTT", customer.toString())
                        if (customer.second.token != null) {
                            callback(
                                Customer.fromCustomerInfo(
                                    customerInfo = customer.second,
                                    login = customer.first,
                                    password = ""
                                ), null
                            )
                        } else {
                            callback(null, "Error: No token")
                        }
                    }
                } else {
                    val filteredError = response.errorBody()?.string()?.filterString()
                    callback(null, filteredError)
                    Log.e("UserRepository.editCustomer", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<Pair<String, CustomerInfo>>, t: Throwable) {
                Log.e("UserRepository.editCustomer", "" + t.message)
                val filteredError = t.message.toString().filterString()
                callback(null, filteredError)
            }
        })
    }

    /**
     * Validates the provided client token.
     *
     * @param token The token to validate.
     * @param callback Callback function that receives a Customer object on success, or an error message on failure.
     */
    fun checkClientToken(token: String, callback: (Customer?, String?) -> Unit) {
        apiService.checkClientToken(token).enqueue(object : Callback<Pair<String, CustomerInfo>> {
            override fun onResponse(
                call: Call<Pair<String, CustomerInfo>>,
                response: Response<Pair<String, CustomerInfo>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { customer ->
                        if (customer.second.token != null) {
                            callback(
                                Customer.fromCustomerInfo(
                                    customerInfo = customer.second,
                                    login = customer.first,
                                    password = ""
                                ), null
                            )
                        } else {
                            callback(null, "Error: No token")
                        }
                    }
                } else {
                    Log.e("UserRepository.checkToken", "" + response.errorBody()?.string())
                    val filteredError = response.errorBody()?.string()?.filterString()
                    callback(null, filteredError)
                }
            }

            override fun onFailure(call: Call<Pair<String, CustomerInfo>>, t: Throwable) {
                val filteredError = t.message.filterString()
                callback(null, filteredError)
                Log.e("UserRepository.checkClientToken", t.message.toString())
            }
        })
    }

    /**
     * Sends a password reminder email to the user.
     *
     * @param email The email address associated with the account.
     * @param onSuccess Callback function that gets invoked if the email is sent successfully.
     * @param onFailure Callback function that handles errors, receiving an error message string.
     */
    fun remindPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (message: String?) -> Unit
    ) {
        apiService.remindPassword(email).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val filteredError = response.errorBody()?.string()?.filterString()
                    onFailure(filteredError)
                    Log.e("UserRepository.remindPassword", "" + response.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                val filteredError = t.message.filterString()
                onFailure(filteredError)
                Log.e("UserRepository.checkClientToken", t.message.toString())
            }
        })
    }
}
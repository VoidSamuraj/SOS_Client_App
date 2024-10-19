package com.pollub.awpfoc.repository

import android.util.Log
import com.pollub.awpfoc.data.ApiService
import com.pollub.awpfoc.data.SessionManager
import com.pollub.awpfoc.data.models.Credentials
import com.pollub.awpfoc.data.models.CustomerInfo
import com.pollub.awpfoc.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class UserRepository {
    private val apiService: ApiService = RetrofitClient.instance

    private fun normalizeResponse(code: Int):String{
       return when (code) {
            400 -> "Błąd: Niepoprawne żądanie"
            401 -> "Błąd: Nieautoryzowany dostęp"
            403 -> "Błąd: Zabroniony dostęp"
            404 -> "Błąd: Nie znaleziono zasobu"
            500 -> "Błąd: Wewnętrzny błąd serwera"
            else -> "Błąd: Nieznany błąd"
        }
    }


    fun login(login: String, password: String, callback: (CustomerInfo?, String?) -> Unit) {
        val credentials = Credentials(login, password)
        apiService.loginClient(credentials).enqueue(object : Callback<CustomerInfo> {
            override fun onResponse(call: Call<CustomerInfo>, response: Response<CustomerInfo>) {
                if (response.isSuccessful) {
                    response.body()?.let { client ->
                        if (client.token != null) {
                            SessionManager.saveUserInfo(client)
                            callback(client, null)
                        } else {
                            callback(null, "Error: No token")
                        }
                    }
                } else {
                    callback(null, normalizeResponse(response.code()))
                }
            }

            override fun onFailure(call: Call<CustomerInfo>, t: Throwable) {
                Log.e("UserRepository.login", t.message.toString())
                callback(null, t.message)
            }
        })
    }

    fun logout(callback: (Boolean, String?) -> Unit) {
        apiService.logoutClient().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    SessionManager.clear()
                    callback(true, null)
                } else {
                    callback(false, normalizeResponse(response.code()))
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("UserRepository.logout", t.message.toString())
                callback(false, t.message)
            }
        })
    }
    fun checkClientToken(token:String, onSuccess:()->Unit){
        apiService.checkClientToken(token).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    onSuccess()
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("UserRepository.checkClientToken", t.message.toString())
            }
        })
    }
}
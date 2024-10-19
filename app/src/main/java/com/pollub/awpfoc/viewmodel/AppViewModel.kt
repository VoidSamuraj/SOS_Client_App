package com.pollub.awpfoc.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.pollub.awpfoc.repository.UserRepository

class AppViewModel : ViewModel() {
    private val userRepository = UserRepository()

    fun login(login: String, password: String, onSuccess:()->Unit, onFailure:(message:String)->Unit) {
        userRepository.login(login, password) { client, error ->
            if (client != null) {
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }

    fun logout(onSuccess: () -> Unit, onFailure:(message:String)->Unit) {
        userRepository.logout { success, error ->
            if (success) {
                onSuccess()
            } else {
                error?.let {
                    onFailure(error)
                }
                Log.e("AuthViewModel", error ?: "Unknown error")
            }
        }
    }
    fun checkClientToken(token:String, onSuccess: () -> Unit) {
        userRepository.checkClientToken(token) {
            onSuccess()
        }
    }


}
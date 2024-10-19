package com.pollub.awpfoc.data

import android.content.Context
import android.content.SharedPreferences
import com.pollub.awpfoc.data.models.CustomerInfo

object SessionManager {
    private const val PREF_NAME = "app_session"
    private const val KEY_ID = "id"
    private const val KEY_NAME = "name"
    private const val KEY_SURNAME = "surname"
    private const val KEY_PHONE = "phone"
    private const val KEY_EMAIL = "email"
    private const val KEY_PESEL = "pesel"
    private const val KEY_TOKEN = "token"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUserInfo(customerInfo: CustomerInfo) {
        sharedPreferences.edit()
            .putString(KEY_ID, customerInfo.id.toString())
            .putString(KEY_NAME, customerInfo.name)
            .putString(KEY_SURNAME, customerInfo.surname)
            .putString(KEY_PHONE, customerInfo.phone)
            .putString(KEY_EMAIL, customerInfo.email)
            .putString(KEY_PESEL, customerInfo.pesel)
            .putString(KEY_TOKEN, customerInfo.token)
            .apply()
    }
    fun getCustomerInfo(): CustomerInfo{
       return sharedPreferences.let {
            return@let CustomerInfo(
                it.getString(KEY_ID,"-1").toString().toInt(),
                it.getString(KEY_NAME,"").toString(),
                it.getString(KEY_SURNAME,"").toString(),
                it.getString(KEY_PHONE,"").toString(),
                it.getString(KEY_PESEL,"").toString(),
                it.getString(KEY_EMAIL,"").toString(),
                false,
                null,
                it.getString(KEY_TOKEN,""),

            )
        }
    }
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun clear() {
        sharedPreferences.edit().remove(KEY_TOKEN).apply()
    }
}
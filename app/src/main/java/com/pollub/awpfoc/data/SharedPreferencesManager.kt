package com.pollub.awpfoc.data

import android.content.Context
import android.content.SharedPreferences
import com.pollub.awpfoc.data.models.Customer

/**
 * Singleton object to manage user session data using SharedPreferences.
 */
object SharedPreferencesManager {
    private const val PREF_NAME = "app_session"
    private const val LOGIN_ID = "login"
    private const val KEY_ID = "id"
    private const val KEY_NAME = "name"
    private const val KEY_SURNAME = "surname"
    private const val KEY_PHONE = "phone"
    private const val KEY_EMAIL = "email"
    private const val KEY_PESEL = "pesel"
    private const val KEY_TOKEN = "token"

    private lateinit var sharedPreferences: SharedPreferences

    /**
     * Initializes the SessionManager with the provided context.
     *
     * @param context The application context to access SharedPreferences.
     */
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Saves user information to SharedPreferences.
     *
     * @param customerInfo The Customer object containing user details to save.
     */
    fun saveUser(customerInfo: Customer) {
        sharedPreferences.edit()
            .putString(KEY_ID, customerInfo.id.toString())
            .putString(LOGIN_ID, customerInfo.login.toString())
            .putString(KEY_NAME, customerInfo.name)
            .putString(KEY_SURNAME, customerInfo.surname)
            .putString(KEY_PHONE, customerInfo.phone)
            .putString(KEY_EMAIL, customerInfo.email)
            .putString(KEY_PESEL, customerInfo.pesel)
            .putString(KEY_TOKEN, customerInfo.token)
            .apply()
    }

    /**
     * Retrieves the stored user information as a Customer object.
     *
     * @return A Customer object populated with user details from SharedPreferences.
     */
    fun getUser(): Customer {
        return sharedPreferences.let {
            return@let Customer(
                id = it.getString(KEY_ID, "-1").toString().toInt(),
                login = it.getString(LOGIN_ID, "-").toString(),
                password = "",
                name = it.getString(KEY_NAME, "").toString(),
                surname = it.getString(KEY_SURNAME, "").toString(),
                phone = it.getString(KEY_PHONE, "").toString(),
                pesel = it.getString(KEY_PESEL, "").toString(),
                email = it.getString(KEY_EMAIL, "").toString(),
                account_deleted = false,
                protection_expiration_date = null,
                token = it.getString(KEY_TOKEN, "")
            )
        }
    }

    /**
     * Retrieves the full name of the user by combining the first name and surname.
     *
     * @return A String containing the user's full name.
     */
    fun getUserName(): String {
        return sharedPreferences.getString(KEY_NAME, "") + " " + sharedPreferences.getString(
            KEY_SURNAME,
            ""
        )
    }

    /**
     * Retrieves the stored token from SharedPreferences.
     *
     * @return The token string, or null if not present.
     */
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    /**
     * Clears all data from SharedPreferences.
     */
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
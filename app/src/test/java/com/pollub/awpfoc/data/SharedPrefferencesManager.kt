package com.pollub.awpfoc.data

import com.pollub.awpfoc.data.models.Customer

/**
 * Mock implementation of SharedPreferencesManager for testing or non-persistent scenarios.
 * No context or shared preferences are used, it simply stores data in a mutable map.
 */
object SharedPreferencesManager {
    private val dataStore: MutableMap<String, String?> = mutableMapOf()

    private const val LOGIN_ID = "login"
    private const val KEY_ID = "id"
    private const val KEY_NAME = "name"
    private const val KEY_SURNAME = "surname"
    private const val KEY_PHONE = "phone"
    private const val KEY_EMAIL = "email"
    private const val KEY_PESEL = "pesel"
    private const val KEY_TOKEN = "token"
    private const val KEY_PROTECTION_EXPIRATION_DATE = "protection_expiration_date"
    private const val SECURED_JWT = "secure_jwt"

    /**
     * Saves user information to the mock data store.
     *
     * @param customerInfo The Customer object containing user details to save.
     */
    fun saveUser(customerInfo: Customer) {
        dataStore[KEY_ID] = customerInfo.id.toString()
        dataStore[LOGIN_ID] = customerInfo.login.toString()
        dataStore[KEY_NAME] = customerInfo.name
        dataStore[KEY_SURNAME] = customerInfo.surname
        dataStore[KEY_PHONE] = customerInfo.phone
        dataStore[KEY_EMAIL] = customerInfo.email
        dataStore[KEY_PESEL] = customerInfo.pesel
        dataStore[KEY_PROTECTION_EXPIRATION_DATE] = customerInfo.protection_expiration_date
        dataStore[KEY_TOKEN] = customerInfo.token
    }

    /**
     * Saves user token to the mock data store.
     *
     * @param token The Customer access token.
     */
    fun saveToken(token: String) {
        dataStore[KEY_TOKEN] = token
    }

    /**
     * Saves long-term token in the mock data store.
     *
     * @param token The token to be saved.
     */
    fun saveSecureToken(token: String) {
        dataStore[SECURED_JWT] = token
    }

    /**
     * Retrieves the stored long-term token from the mock data store.
     *
     * @return The stored token or null if not found.
     */
    fun getSecureToken(): String? {
        return dataStore[SECURED_JWT]
    }

    fun removeSecureToken() {
        dataStore.clear()
    }
    /**
     * Retrieves the stored user information as a Customer object.
     *
     * @return A Customer object populated with user details from the mock data store.
     */
    fun getUser(): Customer {
        return Customer(
            id = dataStore[KEY_ID]?.toIntOrNull() ?: -1,
            login = dataStore[LOGIN_ID].orEmpty(),
            password = "",  // Assuming password is handled elsewhere
            name = dataStore[KEY_NAME].orEmpty(),
            surname = dataStore[KEY_SURNAME].orEmpty(),
            phone = dataStore[KEY_PHONE].orEmpty(),
            pesel = dataStore[KEY_PESEL].orEmpty(),
            email = dataStore[KEY_EMAIL].orEmpty(),
            account_deleted = false,  // Assuming this field is not in the mock
            protection_expiration_date = dataStore[KEY_PROTECTION_EXPIRATION_DATE],
            token = dataStore[KEY_TOKEN].orEmpty()
        )
    }

    /**
     * Retrieves the full name of the user by combining the first name and surname.
     *
     * @return A String containing the user's full name.
     */
    fun getUserName(): String {
        return "${dataStore[KEY_NAME].orEmpty()} ${dataStore[KEY_SURNAME].orEmpty()}".trim()
    }

    /**
     * Retrieves the stored token from the mock data store.
     *
     * @return The token string, or null if not present.
     */
    fun getToken(): String? {
        return dataStore[KEY_TOKEN]
    }

    /**
     * Clears all data from the mock data store.
     */
    fun clear() {
        dataStore.clear()
    }
}

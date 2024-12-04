package com.pollub.awpfoc.data.models

import kotlinx.serialization.Serializable

/**
 * Data class representing basic information about a customer.
 *
 * @property id Unique identifier for the customer.
 * @property name The first name of the customer.
 * @property surname The last name of the customer.
 * @property phone The phone number of the customer.
 * @property pesel The PESEL number of the customer, which is a unique identifier in Poland.
 * @property email The email address of the customer.
 * @property account_deleted Indicates whether the customer's account has been deleted.
 * @property protection_expiration_date The expiration date for the protection of the customer, if applicable.
 * @property token Optional token for session management or authentication purposes.
 */
@Serializable
data class CustomerInfo(
    val id: Int,
    val name: String,
    val surname: String,
    val phone: String,
    val pesel: String,
    val email: String,
    val account_deleted: Boolean,
    val protection_expiration_date: String? = null,
    var token: String? = null
)

/**
 * Data class representing a customer in the system.
 *
 * @property id Unique identifier for the customer.
 * @property login The username or email used for customer authentication.
 * @property password The password associated with the customer's account.
 * @property name The first name of the customer.
 * @property surname The last name of the customer.
 * @property phone The phone number of the customer.
 * @property pesel The PESEL number of the customer, which is a unique identifier in Poland.
 * @property email The email address of the customer.
 * @property account_deleted Indicates whether the customer's account has been deleted.
 * @property protection_expiration_date The expiration date for the protection of the customer, if applicable.
 * @property token Optional token for session management or authentication purposes.
 */
@Serializable
data class Customer(
    val id: Int,
    val login: String,
    val password: String,
    val name: String,
    val surname: String,
    val phone: String,
    val pesel: String,
    val email: String,
    val account_deleted: Boolean,
    val protection_expiration_date: String? = null,
    var token: String? = null
) {
    companion object {
        /**
         * Creates a Customer instance from CustomerInfo, allowing optional login and password.
         *
         * @param customerInfo The CustomerInfo object containing the basic details of the customer.
         * @param login Optional login for the customer. Defaults to an empty string if not provided.
         * @param password Optional password for the customer. Defaults to an empty string if not provided.
         * @return A Customer object populated with data from the provided CustomerInfo and optional credentials.
         */
        fun fromCustomerInfo(
            customerInfo: CustomerInfo,
            login: String? = null,
            password: String? = null
        ): Customer {
            return Customer(
                id = customerInfo.id,
                name = customerInfo.name,
                login = login ?: "",
                password = password ?: "",
                surname = customerInfo.surname,
                phone = customerInfo.phone,
                email = customerInfo.email,
                pesel = customerInfo.pesel,
                account_deleted = customerInfo.account_deleted,
                protection_expiration_date = customerInfo.protection_expiration_date,
                token = customerInfo.token
            )
        }
    }

    /**
     * Converts the Customer instance to an CustomerInfo instance.
     *
     * @return An CustomerInfo instance containing the customer's details.
     */
    fun toCustomerInfo(): CustomerInfo {
        return CustomerInfo(
            id,
            name,
            surname,
            phone,
            pesel,
            email,
            account_deleted,
            protection_expiration_date,
            token
        )
    }
}

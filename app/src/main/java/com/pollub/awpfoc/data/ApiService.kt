package com.pollub.awpfoc.data

import com.pollub.awpfoc.data.models.Credentials
import com.pollub.awpfoc.data.models.CustomerInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query


/**
 * Interface defining API endpoints for client-related operations.
 */
interface ApiService {
    /**
     * Checks if a given login is already in use.
     *
     * @param login The login to check for availability.
     * @return A [Call] that returns true if the login is used, otherwise false.
     */
    @GET("auth/client/isLoginUsed")
    fun isLoginUsed(@Query("login") login: String): Call<Boolean>

    /**
     * Registers a new client with the provided details.
     *
     * @param login The login for the new client.
     * @param password The password for the new client.
     * @param name The first name of the new client.
     * @param surname The last name of the new client.
     * @param email The email address of the new client.
     * @param phone The phone number of the new client.
     * @param pesel The PESEL number of the new client.
     * @return A [Call] that returns a pair containing a status message and the registered [CustomerInfo].
     */
    @FormUrlEncoded
    @POST("auth/client/register")
    fun registerClient(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("surname") surname: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("pesel") pesel: String
    ): Call<Pair<String, CustomerInfo>>

    /**
     * Logs in a client using their credentials.
     *
     * @param credentials The login credentials of the client.
     * @return A [Call] that returns a pair containing a status message and the [CustomerInfo] of the logged-in client.
     */
    @POST("auth/client/login")
    fun loginClient(@Body credentials: Credentials): Call<Pair<String, CustomerInfo>>

    /**
     * Checks the validity of the client's session token.
     *
     * @param token The session token to check.
     * @return A [Call] that returns a pair containing a status message and the associated [CustomerInfo].
     */
    @POST("auth/client/checkToken")
    fun checkClientToken(@Body token: String): Call<Pair<String, CustomerInfo>>

    /**
     * Sends a password reminder to the specified email address.
     *
     * @param email The email address to send the password reminder to.
     * @return A [Call] that returns a status message regarding the reminder request.
     */
    @FormUrlEncoded
    @POST("auth/client/remind-password")
    fun remindPassword(@Field("email") email: String): Call<String>

    /**
     * Logs out the currently authenticated client.
     *
     * @return A [Call] that returns a status message indicating the result of the logout operation.
     */
    @POST("auth/client/logout")
    fun logoutClient(): Call<String>

    /**
     * Edits the details of an existing client.
     *
     * @param id The unique identifier of the client to edit.
     * @param login The new login for the client, if changing.
     * @param password The current password of the client.
     * @param newPassword The new password for the client, if changing.
     * @param name The new first name for the client, if changing.
     * @param surname The new last name for the client, if changing.
     * @param email The new email address for the client, if changing.
     * @param phone The new phone number for the client, if changing.
     * @param pesel The new PESEL number for the client, if changing.
     * @return A [Call] that returns a pair containing a status message and the updated [CustomerInfo].
     */
    @FormUrlEncoded
    @PATCH("auth/client/edit")
    fun editClient(
        @Field("id") id: Int,
        @Field("login") login: String?,
        @Field("password") password: String,
        @Field("newPassword") newPassword: String?,
        @Field("name") name: String?,
        @Field("surname") surname: String?,
        @Field("email") email: String?,
        @Field("phone") phone: String?,
        @Field("pesel") pesel: String?
    ): Call<Pair<String, CustomerInfo>>
}

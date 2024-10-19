package com.pollub.awpfoc.data

import com.pollub.awpfoc.data.models.Credentials
import com.pollub.awpfoc.data.models.CustomerInfo
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path



interface ApiService {
    @POST("auth/client/register")
    fun registerClient(@Body client: CustomerInfo): Call<CustomerInfo>

    @POST("auth/client/login")
    fun loginClient(@Body credentials: Credentials): Call<CustomerInfo>

    @POST("auth/client/checkToken")
    fun checkClientToken(@Body token: String):Call<String>

    @POST("auth/client/logout")
    fun logoutClient(): Call<String>

    @GET("client/{id}")
    fun getClient(@Path("id") id: Int): Call<CustomerInfo>

    @PUT("client/{id}")
    fun editClient(@Path("id") id: Int, @Body client: CustomerInfo): Call<CustomerInfo>

    @DELETE("client/{id}")
    fun deleteClient(@Path("id") id: Int): Call<Void>

}

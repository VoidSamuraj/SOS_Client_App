package com.pollub.awpfoc.network

import com.pollub.awpfoc.data.ApiService
import com.pollub.awpfoc.data.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitClient {
    private const val BASE_URL = "https://10.0.2.2:8443/"

    private val client = OkHttpClient.Builder()
        //TODO REMOVE THIS After Usage of trusted ssl keys
        //TEMPORARY ALLOW ALL CERTS START
        .sslSocketFactory(createTrustAllSslSocketFactory(), object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        .hostnameVerifier { hostname, session -> true }
        // END
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val token = SessionManager.getToken()

            val requestBuilder = originalRequest.newBuilder()
                .apply {
                    if (token != null) {
                        header("Authorization", "Bearer $token")
                    }
                }

            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    //Custom sslFactory to allow all certs
    private fun createTrustAllSslSocketFactory(): SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        return sslContext.socketFactory
    }
}


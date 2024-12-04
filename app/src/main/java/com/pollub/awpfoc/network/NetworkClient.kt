package com.pollub.awpfoc.network

import androidx.compose.runtime.mutableStateOf
import com.google.gson.JsonParser
import com.pollub.awpfoc.BASE_URL
import com.pollub.awpfoc.data.ApiService
import com.pollub.awpfoc.data.SharedPreferencesManager
import com.pollub.awpfoc.repository.UserRepository
import com.pollub.awpfoc.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Singleton object to manage the Retrofit client configuration.
 */
object NetworkClient {

    val userRepository: UserRepository by lazy {
        UserRepository()
    }

    // OkHttpClient instance configured with SSL settings and request interceptors.
    private val client by lazy {
        OkHttpClient.Builder()
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

                val accessToken = SharedPreferencesManager.getToken()

                val requestBuilder = originalRequest.newBuilder()
                    .apply {
                        if (accessToken != null) {
                            header("Authorization", "Bearer $accessToken")
                        }
                    }

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    /**
     * Lazily initializes the Retrofit instance with the configured OkHttpClient and converters.
     */
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    object WebSocketManager {
        private var viewModel: AppViewModel? = null

        val isConnecting = mutableStateOf(false)

        fun setViewModel(vm: AppViewModel) {
            viewModel = vm
        }

        var onReportFinish: () -> Unit = {}

        fun setOnReportFinished(onFinish: () -> Unit) {
            onReportFinish = onFinish
        }

        var isServiceStopping = true

        var lastReportId: Int = -1
            private set
        private var closeCode: Int? = null

        fun setCloseCode(code: Int) {
            closeCode = code
        }

        private var executeOnStart: (() -> Unit)? = null
        private var executeOnClose: (() -> Unit)? = null

        fun executeOnStart(onStart: () -> Unit) {
            executeOnStart = onStart
        }

        fun executeOnClose(onClose: () -> Unit) {
            executeOnClose = onClose
        }

        private lateinit var webSocket: WebSocket
        private var isConnected = false
        private fun setIsConnected(connected: Boolean) {
            isConnected = connected
            viewModel?.isSystemConnected?.value = connected
        }

        private fun reconnectWithDelay(url: String) {
            isConnecting.value = true
            CoroutineScope(Dispatchers.IO).launch() {
                delay(5_000L)
                if (!isServiceStopping) {
                    connect(url)
                }
            }
        }

        fun connect(url: String) {
            isServiceStopping = false
            closeCode = null
            if (!isConnected) {
                val request = Request.Builder().url(url)
                    .addHeader("Authorization", "Bearer ${SharedPreferencesManager.getToken()}")
                    .build()
                webSocket = client.newWebSocket(request, object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                        setIsConnected(true)
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        val jsonObject = JsonParser.parseString(text).asJsonObject
                        //confirmed
                        if (jsonObject.has("reportId")) {
                            lastReportId = jsonObject.get("reportId").asInt
                            executeOnStart?.invoke()
                            setIsConnected(true)
                            isConnecting.value = false
                        }
                        if (jsonObject.has("status")) {
                            when (jsonObject.get("status").asString) {
                                "finished" -> {
                                    lastReportId = -1
                                    viewModel?.isSosActive?.value = false
                                    viewModel?.reportState?.value =
                                        AppViewModel.Companion.ReportState.NONE
                                    onReportFinish()
                                }

                                "reconnected" -> {
                                    setIsConnected(true)
                                    isConnecting.value = false
                                    executeOnStart?.invoke()
                                }

                                "confirmed" -> {
                                    viewModel?.reportState?.value =
                                        AppViewModel.Companion.ReportState.CONFIRMED
                                }

                                "waiting" -> {
                                    viewModel?.reportState?.value =
                                        AppViewModel.Companion.ReportState.WAITING
                                }
                            }
                        }
                    }

                    override fun onFailure(
                        webSocket: WebSocket,
                        t: Throwable,
                        response: okhttp3.Response?
                    ) {
                        setIsConnected(false)
                        t.printStackTrace()
                        if (!isServiceStopping) {
                            reconnectWithDelay(url)
                        }
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        setIsConnected(false)
                        if (!isServiceStopping) {
                            reconnectWithDelay(url)
                        } else {
                            viewModel?.checkIfConnected(
                                onSuccess = { viewModel!!.isSystemConnected.value = true },
                                onFailure = { viewModel!!.isSystemConnected.value = false })
                        }
                    }
                })
            }
        }

        fun sendMessage(message: String) {
            if (isConnected) {
                webSocket.send(message)
            } else {
                // Optional: Handle disconnected state or reconnect
            }
        }


        fun disconnect() {
            isServiceStopping = true
            if (isConnected) {
                executeOnClose?.invoke()
                if (closeCode != null)
                    if (closeCode == 4000) {
                        webSocket.close(closeCode!!, """{"reportId": $lastReportId}""")
                    } else {
                        webSocket.close(closeCode!!, "Disconnect")
                    }
                else
                    webSocket.close(1000, "Disconnect")
            }
            setIsConnected(false)
            lastReportId = -1
            closeCode = null
        }
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


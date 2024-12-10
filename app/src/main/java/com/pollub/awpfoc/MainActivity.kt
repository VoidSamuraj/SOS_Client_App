package com.pollub.awpfoc

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModelProvider
import com.pollub.awpfoc.data.SharedPreferencesManager
import com.pollub.awpfoc.network.NetworkClient
import com.pollub.awpfoc.ui.components.PermissionsInfoScreen
import com.pollub.awpfoc.ui.components.PhonePermissionPopup
import com.pollub.awpfoc.ui.main.AppUI
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.utils.CheckPermissions
import com.pollub.awpfoc.utils.EnableEdgeToEdgeAndSetBarTheme
import com.pollub.awpfoc.utils.TokenManager
import com.pollub.awpfoc.utils.WearOsListener
import com.pollub.awpfoc.utils.makePhoneCall
import com.pollub.awpfoc.viewmodel.AppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val supportPhoneNumber = "+48123456789"
//debug
//10.0.2.2:8443
const val address="10.0.2.2:8443"
const val BASE_URL = "https://$address/"
const val BASE_WEBSOCKET_URL = "wss://$address/clientSocket"

class MainActivity : ComponentActivity() {

    var showDialog = mutableStateOf(false)
    var isDarkMode = true
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestCallPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var viewModel: AppViewModel

    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = TokenManager.TOKEN_EXPIRATION_THRESHOLD*500

    private val refreshTask = object : Runnable {
        override fun run() {

            CoroutineScope(Dispatchers.Default).launch{
                TokenManager.refreshTokenIfNeeded()
            }
            handler.postDelayed(this, refreshInterval)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SharedPreferencesManager.init(this)
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]
        NetworkClient.WebSocketManager.setViewModel(viewModel)
        WearOsListener.viewModelInstance = viewModel

        lifecycle.addObserver(
            viewModel.ObserveIfConnectionAvailable(
                context = this,
                lifecycleOwner =  this,
                invokeIfConnected = {
                    viewModel.isSystemConnected.value=true
                },
                invokeIfDisconnected = {
                    viewModel.isSystemConnected.value=false
                },
                invokeIfWatchConnected = {
                    viewModel.isSmartWatchConnected.value=true
                },
                invokeIfWatchDisconnected = {
                    viewModel.isSmartWatchConnected.value=false
                })
        )

        handler.post(refreshTask)

        requestCallPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (!isGranted) {
                showDialog.value = true
            } else {
                makePhoneCall(this, requestCallPermissionLauncher, supportPhoneNumber)
            }
        }

        requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            setContent {
                AwpfocTheme(dynamicColor = false) {
                    val lighterColor = MaterialTheme.colorScheme.secondary.toArgb()
                    val darkerColor = MaterialTheme.colorScheme.primary.toArgb()
                    EnableEdgeToEdgeAndSetBarTheme(lighterColor, darkerColor)
                    if (allGranted) {
                        AppUI(
                            this,
                            viewModel,
                            requestCallPermissionLauncher
                        )
                    } else {
                        PermissionsInfoScreen()
                    }
                    PhonePermissionPopup(showDialog)
                }
            }
        }
        setContent {
            AwpfocTheme(dynamicColor = false) {
                CheckPermissions(this, requestPermissionsLauncher) {
                    val lighterColor = MaterialTheme.colorScheme.secondary.toArgb()
                    val darkerColor = MaterialTheme.colorScheme.primary.toArgb()
                    EnableEdgeToEdgeAndSetBarTheme(lighterColor, darkerColor)
                    AppUI(
                        this,
                        viewModel,
                        requestCallPermissionLauncher
                    )
                }
                PhonePermissionPopup(showDialog)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(refreshTask)
    }
}

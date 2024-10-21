package com.pollub.awpfoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModelProvider
import com.pollub.awpfoc.data.SharedPreferencesManager
import com.pollub.awpfoc.ui.components.PermissionsInfoScreen
import com.pollub.awpfoc.ui.components.PhonePermissionPopup
import com.pollub.awpfoc.ui.main.AppUI
import com.pollub.awpfoc.ui.theme.AwpfocTheme
import com.pollub.awpfoc.utils.CheckPermissions
import com.pollub.awpfoc.utils.EnableEdgeToEdgeAndSetBarTheme
import com.pollub.awpfoc.utils.makePhoneCall
import com.pollub.awpfoc.viewmodel.AppViewModel

val supportPhoneNumber = "+48123456789"

class MainActivity : ComponentActivity() {

    var showDialog = mutableStateOf(false)
    var isDarkMode = true
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestCallPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var viewModel: AppViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPreferencesManager.init(this)
        viewModel = ViewModelProvider(this).get(AppViewModel::class.java)
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
                    CheckPermissions(this, requestPermissionsLauncher) {
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
}

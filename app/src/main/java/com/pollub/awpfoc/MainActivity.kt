package com.pollub.awpfoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import com.pollub.awpfoc.ui.components.PermissionsInfoScreen
import com.pollub.awpfoc.ui.main.AppUI
import com.pollub.awpfoc.utils.makePhoneCall

val supportPhoneNumber="+48123456789"

class MainActivity : ComponentActivity() {

    var showDialog = mutableStateOf(false)
    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var requestCallPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestCallPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                showDialog.value = true
            }else{
                makePhoneCall(this, requestCallPermissionLauncher, supportPhoneNumber)
            }
        }

        requestPermissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allGranted = permissions.all { it.value }
            if (allGranted) {
                setContent {  AppUI(this,showDialog,requestCallPermissionLauncher, requestPermissionsLauncher) }
            } else {
                setContent { PermissionsInfoScreen() }
            }
        }
        setContent {
            AppUI(this,showDialog,requestCallPermissionLauncher, requestPermissionsLauncher)
        }
    }
}

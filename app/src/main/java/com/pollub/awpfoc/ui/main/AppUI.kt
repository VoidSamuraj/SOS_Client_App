package com.pollub.awpfoc.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pollub.awpfoc.MainActivity
import com.pollub.awpfoc.navigation.NavRoutes
import com.pollub.awpfoc.supportPhoneNumber
import com.pollub.awpfoc.ui.components.PhonePermissionPopup
import com.pollub.awpfoc.ui.login.LoginScreen
import com.pollub.awpfoc.ui.login.RegistrationScreen
import com.pollub.awpfoc.ui.theme.AwpfocTheme


/**
 * Composable function that sets up the main user interface and handles permissions.
 *
 * @param mainActivity The MainActivity instance for context and permission handling.
 * @param showDialog State to control the visibility of the permission dialog.
 * @param requestCallPermissionLauncher Launcher for requesting CALL_PHONE permission.
 * @param requestPermissionsLauncher Launcher for requesting multiple permissions.
 */
@Composable
fun AppUI(mainActivity: MainActivity,showDialog:MutableState<Boolean>, requestCallPermissionLauncher:ActivityResultLauncher<String>, requestPermissionsLauncher:ActivityResultLauncher<Array<String>>) {

    fun checkPermission(permissionToCheck:String, context: Context,permissionsList: MutableList<String>){
        if (ContextCompat.checkSelfPermission(context, permissionToCheck)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsList.add(permissionToCheck)
        }
    }

    val permissionsToRequest = mutableListOf<String>()

    checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, mainActivity, permissionsToRequest)
    checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, mainActivity, permissionsToRequest)
    checkPermission(Manifest.permission.INTERNET, mainActivity, permissionsToRequest)

    if (permissionsToRequest.isNotEmpty()) {
        requestPermissionsLauncher.launch(permissionsToRequest.toTypedArray())
    } else {

        val navController = rememberNavController()
        AwpfocTheme(dynamicColor = false) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                NavHost(navController = navController, startDestination = NavRoutes.LoginScreen.route) {
                    composable(NavRoutes.MainScreen.route) {
                        MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            phoneNumber = supportPhoneNumber,
                            requestCallPermissionLauncher = requestCallPermissionLauncher,
                            onLogout = {
                                navController.navigate(NavRoutes.LoginScreen.route)
                            })
                    }
                    composable(NavRoutes.LoginScreen.route) {
                        LoginScreen(
                            modifier = Modifier.padding(innerPadding),
                            onLoginPress = {
                                navController.navigate(NavRoutes.MainScreen.route)
                            },
                            onRemindPasswordPress = {

                            },
                            navToRegister = {
                                navController.navigate(NavRoutes.RegisterScreen.route)
                            }
                        )
                    }
                    composable(NavRoutes.RegisterScreen.route) {
                        RegistrationScreen(
                            modifier = Modifier.padding(innerPadding),
                            navToLogin = {
                                navController.navigate(NavRoutes.LoginScreen.route)
                            },
                            onSignUp = {

                            }
                        )
                    }
                }
                PhonePermissionPopup(showDialog)
            }
        }
    }
}

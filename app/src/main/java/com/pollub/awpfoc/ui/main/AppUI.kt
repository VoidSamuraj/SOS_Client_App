package com.pollub.awpfoc.ui.main

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.pollub.awpfoc.MainActivity
import com.pollub.awpfoc.R
import com.pollub.awpfoc.data.SessionManager
import com.pollub.awpfoc.navigation.NavRoutes
import com.pollub.awpfoc.navigation.RegisterNavRoutes
import com.pollub.awpfoc.service.LocationService
import com.pollub.awpfoc.supportPhoneNumber
import com.pollub.awpfoc.ui.components.EditUserDataScreen
import com.pollub.awpfoc.ui.components.TopBar
import com.pollub.awpfoc.ui.login.LoginScreen
import com.pollub.awpfoc.ui.login.RegistrationScreen
import com.pollub.awpfoc.ui.login.RegistrationScreenPersonalInformation
import com.pollub.awpfoc.ui.login.RemindPasswordScreen
import com.pollub.awpfoc.utils.CustomSnackbar
import com.pollub.awpfoc.viewmodel.AppViewModel

/**
 * Composable function that sets up the main user interface.
 *
 * @param mainActivity The MainActivity instance for context and permission handling.
 * @param viewModel The AppViewModel to interact with data layer.
 * @param requestCallPermissionLauncher Launcher for requesting CALL_PHONE permission.
 */
@Composable
fun AppUI(mainActivity: MainActivity, viewModel: AppViewModel, requestCallPermissionLauncher:ActivityResultLauncher<String>) {
    val locationServiceIntent = Intent(mainActivity, LocationService::class.java)
    val navController = rememberNavController()

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val density = LocalDensity.current.density
    val screenWidthPx = screenWidth.value * density

    var isNavigatingToLogin by remember { mutableStateOf(false) }
    var isNavigatingToHome by remember { mutableStateOf(false) }

    val isSnackBarVisible = remember { mutableStateOf(false) }
    val snackBarMessage = remember { mutableStateOf("") }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(navController = navController, startDestination = NavRoutes.LoginScreen.route) {
            composable(NavRoutes.MainScreen.route) {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(onLogout = {
                            viewModel.logout(onSuccess = {
                                navController.navigate(NavRoutes.LoginScreen.route)
                            },
                                onFailure = { message ->
                                    snackBarMessage.value = message
                                    isSnackBarVisible.value = true
                                })
                        })
                    }
                ) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding),
                        phoneNumber = supportPhoneNumber,
                        requestCallPermissionLauncher = requestCallPermissionLauncher,
                        onCallSOS = {
                            mainActivity.startService(locationServiceIntent)
                        },
                        onCancelSOS = {
                            mainActivity.stopService(locationServiceIntent)
                        }
                    )
                }
            }
            composable(NavRoutes.LoginScreen.route) {
                val token = SessionManager.getToken()
                LaunchedEffect(token) {
                    if (token != null) {
                        viewModel.checkClientToken(token) {
                            navController.navigate(NavRoutes.MainScreen.route) {
                                popUpTo(NavRoutes.LoginScreen.route) { inclusive = true }
                            }
                        }
                    }
                }
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    onLoginPress = { login, password ->
                        viewModel.login(login = login, password, onSuccess = {
                            navController.navigate(NavRoutes.MainScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                            onFailure = { message ->
                                snackBarMessage.value = message
                                isSnackBarVisible.value = true
                            })
                    },
                    onRemindPasswordPress = {
                        navController.navigate(NavRoutes.RemindPasswordScreen.route)
                    },
                    navToRegister = {
                        navController.navigate(NavRoutes.RegisterScreen.route)
                    }
                )
            }
            navigation(
                startDestination = RegisterNavRoutes.RegisterScreen1.route,
                route = NavRoutes.RegisterScreen.route
            ) {
                composable(
                    RegisterNavRoutes.RegisterScreen1.route,
                    enterTransition = {
                        isNavigatingToLogin = false
                        fadeIn(animationSpec = tween(300))
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { -screenWidthPx.toInt() },
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        if (!isNavigatingToLogin)
                            slideOutHorizontally(
                                targetOffsetX = { -screenWidthPx.toInt() },
                                animationSpec = tween(500)
                            )
                        else
                            fadeOut(animationSpec = tween(300))
                    }
                ) {
                    RegistrationScreen(
                        modifier = Modifier.padding(innerPadding),
                        navToLogin = {
                            isNavigatingToLogin = true
                            navController.popBackStack()
                        },
                        navToNextScreen = { login, password ->
                            navController.navigate(
                                RegisterNavRoutes.getRegisterScreen2Route(
                                    login,
                                    password
                                )
                            )
                        }
                    )
                }
                composable(
                    RegisterNavRoutes.RegisterScreen2.route,
                    enterTransition = {
                        isNavigatingToHome = false
                        slideInHorizontally(
                            initialOffsetX = { screenWidthPx.toInt() },
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        if (!isNavigatingToHome)
                            slideOutHorizontally(
                                targetOffsetX = { screenWidthPx.toInt() },
                                animationSpec = tween(500)
                            )
                        else
                            fadeOut(animationSpec = tween(300))
                    }
                ) { backStackEntry ->
                    val login = backStackEntry.arguments?.getString("login")
                    val password = backStackEntry.arguments?.getString("password")

                    RegistrationScreenPersonalInformation(
                        modifier = Modifier.padding(innerPadding),
                        navBack = {
                            navController.popBackStack()
                        },
                        onSignUp = { _, _, _, _, _, _, _ ->
                            isNavigatingToHome = true
                            navController.navigate(NavRoutes.MainScreen.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
            composable(NavRoutes.RemindPasswordScreen.route) {
                RemindPasswordScreen(
                    modifier = Modifier.padding(innerPadding),
                    navBack = {
                        navController.popBackStack()
                    },
                    onSendPress = {
                        navController.popBackStack()
                    })
            }
            composable(NavRoutes.EditUserDataScreen.route) {

                EditUserDataScreen(
                    modifier = Modifier.padding(innerPadding),
                    onSavePress = { login, password, name, surname, email, phone, pesel ->

                    })
            }
        }
        if (isSnackBarVisible.value)
            CustomSnackbar(
                modifier = Modifier.padding(innerPadding),
                snackBarMessage.value,
                MaterialTheme.colorScheme.error,
                R.drawable.baseline_error_outline_24
            ) {
                isSnackBarVisible.value = false
            }
    }
}

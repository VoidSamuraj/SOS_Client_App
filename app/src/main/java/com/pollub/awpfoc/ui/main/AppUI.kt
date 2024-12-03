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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.pollub.awpfoc.MainActivity
import com.pollub.awpfoc.R
import com.pollub.awpfoc.data.SharedPreferencesManager
import com.pollub.awpfoc.data.models.CustomerInfo
import com.pollub.awpfoc.navigation.NavRoutes
import com.pollub.awpfoc.navigation.RegisterNavRoutes
import com.pollub.awpfoc.network.NetworkClient
import com.pollub.awpfoc.service.LocationService
import com.pollub.awpfoc.supportPhoneNumber
import com.pollub.awpfoc.ui.components.EditUserDataScreen
import com.pollub.awpfoc.ui.components.TopBar
import com.pollub.awpfoc.ui.login.LoginScreen
import com.pollub.awpfoc.ui.login.RegistrationScreen
import com.pollub.awpfoc.ui.login.RegistrationScreenPersonalInformation
import com.pollub.awpfoc.ui.login.RemindPasswordScreen
import com.pollub.awpfoc.utils.CustomSnackBar
import com.pollub.awpfoc.utils.TokenManager
import com.pollub.awpfoc.viewmodel.AppViewModel
import com.pollub.awpfoc.viewmodel.RegisterScreenViewModel
import kotlinx.coroutines.runBlocking

/**
 * Composable function that sets up the main user interface.
 *
 * @param mainActivity The MainActivity instance for context and permission handling.
 * @param viewModel The AppViewModel to interact with data layer.
 * @param requestCallPermissionLauncher Launcher for requesting CALL_PHONE permission.
 */
@Composable
fun AppUI(
    mainActivity: MainActivity,
    viewModel: AppViewModel,
    requestCallPermissionLauncher: ActivityResultLauncher<String>
) {
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

    val registerScreenViewModel: RegisterScreenViewModel = viewModel()

    val snackBarIcon = remember { mutableStateOf(R.drawable.baseline_error_outline_24) }

    var defaultColor: Color = MaterialTheme.colorScheme.error
    val snackBarColor = remember { mutableStateOf(defaultColor) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        NavHost(navController = navController, startDestination = NavRoutes.LoginScreen.route) {
            composable(NavRoutes.MainScreen.route) {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(
                            clientName = SharedPreferencesManager.getUserName(),
                            iconId = R.drawable.baseline_account_circle_24,
                            onIconClick = {
                                navController.navigate(NavRoutes.EditUserDataScreen.route)
                            },
                            onLogout = {
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
                        viewModel = viewModel,
                        phoneNumber = supportPhoneNumber,
                        requestCallPermissionLauncher = requestCallPermissionLauncher,
                        onCallSOS = { onSuccess ->
                            NetworkClient.WebSocketManager.executeOnStart { onSuccess() }
                            mainActivity.startService(locationServiceIntent)
                        },
                        onCancelSOS = { onSuccess ->
                            NetworkClient.WebSocketManager.executeOnClose { onSuccess() }
                            NetworkClient.WebSocketManager.setCloseCode(4000)
                            mainActivity.stopService(locationServiceIntent)
                        }
                    )
                }
            }
            composable(NavRoutes.LoginScreen.route) {
                val token = SharedPreferencesManager.getToken()
                LaunchedEffect(token) {
                    if (token != null) {
                        viewModel.checkClientToken(token,
                            onSuccess = {
                                navController.navigate(NavRoutes.MainScreen.route) {
                                    popUpTo(NavRoutes.LoginScreen.route) { inclusive = true }
                                }
                            },
                            onFailure = { message ->
                                val securedToken = SharedPreferencesManager.getSecureToken()
                                if (securedToken != null) {
                                    viewModel.checkClientToken(securedToken,
                                        onSuccess = {
                                            runBlocking {
                                                if (!TokenManager.isRefreshTokenExpired()) {
                                                    TokenManager.refreshTokenIfNeeded()
                                                    navController.navigate(NavRoutes.MainScreen.route) {
                                                        popUpTo(NavRoutes.LoginScreen.route) {
                                                            inclusive = true
                                                        }
                                                    }
                                                } else {
                                                    snackBarMessage.value = "Sesja wygasła"
                                                    isSnackBarVisible.value = true
                                                }

                                            }

                                        },
                                        onFailure = { message ->
                                            snackBarMessage.value = message
                                            isSnackBarVisible.value = true
                                        }
                                    )
                                } else {
                                    snackBarMessage.value = "Sesja wygasła"
                                    isSnackBarVisible.value = true
                                }
                            })
                    }
                }
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    onLoginPress = { login, password ->
                        viewModel.login(login = login, password, onSuccess = {
                            navController.navigate(NavRoutes.MainScreen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = true
                                }
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
                        registerScreenViewModel = registerScreenViewModel,
                        navToLogin = {
                            isNavigatingToLogin = true
                            navController.popBackStack()
                        },
                        navToNextScreen = {
                            viewModel.isLoginNotUsed(
                                registerScreenViewModel.login,
                                onSuccess = {
                                    navController.navigate(RegisterNavRoutes.RegisterScreen2.route)
                                }, onFailure = { message ->
                                    snackBarMessage.value = message
                                    isSnackBarVisible.value = true
                                })
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
                    RegistrationScreenPersonalInformation(
                        modifier = Modifier.padding(innerPadding),
                        registerScreenViewModel = registerScreenViewModel,
                        navBack = {
                            navController.popBackStack()
                        },
                        onSignUp = { customer: CustomerInfo ->
                            viewModel.register(login = registerScreenViewModel.login,
                                password = registerScreenViewModel.password,
                                customer = customer,
                                onSuccess = {
                                    registerScreenViewModel.clearAllFields()
                                    isNavigatingToHome = true
                                    navController.navigate(NavRoutes.MainScreen.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                onFailure = { message ->
                                    snackBarMessage.value = message
                                    isSnackBarVisible.value = true
                                })


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
                    onSendPress = { email ->
                        viewModel.remindPassword(email, onSuccess = {
                            snackBarMessage.value = "Email został wysłany na podany adres"
                            snackBarColor.value = Color(0xFF5FBF2F)
                            snackBarIcon.value = R.drawable.outline_check_circle_outline_24
                            isSnackBarVisible.value = true
                            navController.popBackStack()
                        },
                            onFailure = { message ->
                                snackBarMessage.value = message
                                isSnackBarVisible.value = true
                            })
                    })
            }
            composable(NavRoutes.EditUserDataScreen.route) {
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopBar(
                            clientName = SharedPreferencesManager.getUserName(),
                            iconId = R.drawable.baseline_arrow_back_24,
                            onIconClick = {
                                navController.navigate(NavRoutes.MainScreen.route)
                            },
                            onLogout = {
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
                    val customer = SharedPreferencesManager.getUser()
                    EditUserDataScreen(
                        modifier = Modifier.padding(innerPadding),
                        customer,
                        onSavePress = { login, password, newPassword, name, surname, email, phone, pesel ->
                            viewModel.editCustomer(id = customer.id, login = login,
                                password = password,
                                newPassword = newPassword,
                                name = name,
                                surname = surname,
                                email = email,
                                phone = phone,
                                pesel = pesel,
                                onSuccess = {
                                    snackBarMessage.value =
                                        "Użytkownik został zaktualizowany na podany adres"
                                    snackBarColor.value = Color(0xFF5FBF2F)
                                    snackBarIcon.value = R.drawable.outline_check_circle_outline_24
                                    isSnackBarVisible.value = true
                                    navController.popBackStack()
                                },
                                onFailure = { message ->
                                    snackBarMessage.value = message
                                    isSnackBarVisible.value = true
                                })
                        })
                }
            }
        }
        if (isSnackBarVisible.value)
            CustomSnackBar(
                modifier = Modifier.padding(innerPadding),
                message = snackBarMessage.value,
                backgroundColor = snackBarColor.value,
                iconResId = snackBarIcon.value
            ) {
                isSnackBarVisible.value = false
                snackBarColor.value = defaultColor
                snackBarIcon.value = R.drawable.baseline_error_outline_24
            }
    }
}
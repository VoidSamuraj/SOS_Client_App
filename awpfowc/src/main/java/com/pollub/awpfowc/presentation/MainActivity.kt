/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.pollub.awpfowc.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.tooling.preview.devices.WearDevices
import com.pollub.awpfowc.presentation.theme.AwpfocTheme
import com.pollub.awpfowc.presentation.ui.MainScreen
import com.pollub.awpfowc.presentation.ui.MessageScreen
import com.pollub.awpfowc.presentation.nav.NavRoutes
import com.pollub.awpfowc.presentation.ui.SOSScreen
import com.pollub.awpfowc.presentation.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ViewModel::class.java]

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp(viewModel)
        }
    }
}

@Composable
fun WearApp(viewModel: ViewModel) {
    val navController = rememberNavController()
    val isSystemConnected = remember { mutableStateOf(false) }
    val isScreenRound = LocalContext.current.resources.configuration.isScreenRound
    AwpfocTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            LaunchedEffect(true) {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.addListener(
                        onTokenActive = {
                            if (
                                navController.currentDestination?.route == NavRoutes.MessageScreen.route ||
                                navController.currentDestination?.route == NavRoutes.LoadingScreen.route
                            )
                                navController.navigate(NavRoutes.MainScreen.route)
                        },
                        onTokenExpire = {
                            navController.navigate(NavRoutes.MessageScreen.route)
                        },
                        onStartedSOS = {
                            isSystemConnected.value = true
                            navController.navigate(NavRoutes.WaitingScreen.route)
                        },
                        onStoppedSOS = {
                            isSystemConnected.value = true
                            navController.navigate(NavRoutes.MainScreen.route)
                        },
                        onProtectExpiration = {
                            navController.navigate(NavRoutes.MessageScreen.route)
                        },
                        onFailure = {
                            isSystemConnected.value = false
                        })
                    viewModel.checkToken(
                        callOnConnection = {
                            isSystemConnected.value = true
                        },
                        callOnNoConnection = {
                            isSystemConnected.value = false
                        })
                }
            }
            NavHost(
                navController = navController,
                startDestination = NavRoutes.LoadingScreen.route
            ) {
                composable(NavRoutes.MainScreen.route) {
                    MainScreen(isScreenRound = isScreenRound,
                        isConnected = isSystemConnected.value,
                        onSOSClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.sendSOSRequest(start = true,
                                    onSuccess = {
                                        isSystemConnected.value = true
                                    },
                                    onFailure = {
                                        isSystemConnected.value = false
                                    })
                            }
                        },
                        onCheckConnectionClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.checkToken(
                                    callOnConnection = {
                                        isSystemConnected.value = true
                                    },
                                    callOnNoConnection = {
                                        isSystemConnected.value = false
                                    })
                            }
                        })
                }
                composable(NavRoutes.WaitingScreen.route) {
                    SOSScreen(
                        isScreenRound = isScreenRound,
                        viewModel = viewModel,
                        onDismiss = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.sendSOSRequest(start = false,
                                    onSuccess = {
                                        isSystemConnected.value = true
                                        navController.navigate(NavRoutes.MainScreen.route)
                                    },
                                    onFailure = {
                                        isSystemConnected.value = false
                                    })
                            }
                        })
                }
                composable(NavRoutes.MessageScreen.route) {
                    MessageScreen(isScreenRound = isScreenRound, viewModel = viewModel)
                }
                composable(NavRoutes.LoadingScreen.route) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.fillMaxSize(0.7f),
                            indicatorColor = Color(0xD8FFFFFF),
                            trackColor = Color.Transparent,
                            strokeWidth = 24.dp
                        )
                    }
                }
            }
        }
    }
}


@Preview(device = WearDevices.RECT, showSystemUi = true)
@Composable
fun DefaultPreview() {
    val isScreenRound = LocalContext.current.resources.configuration.isScreenRound
    MainScreen(isScreenRound, false, {}, {})
}
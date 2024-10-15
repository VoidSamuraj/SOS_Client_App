package com.pollub.awpfoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pollub.awpfoc.ui.theme.AwpfocTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            AwpfocTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(navController = navController, startDestination = NavRoutes.LoginScreen.route) {
                        composable(NavRoutes.MainScreen.route) {
                            MainScreen(
                                modifier = Modifier.padding(innerPadding),
                                onLogout = {
                                    navController.navigate(NavRoutes.LoginScreen.route)
                                })
                        }
                        composable(NavRoutes.LoginScreen.route) {
                            LoginScreen(
                                modifier=  Modifier.padding(innerPadding),
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
                                modifier=  Modifier.padding(innerPadding),
                                navToLogin = {
                                    navController.navigate(NavRoutes.LoginScreen.route)
                                },
                                onSignUp = {

                                }
                            )
                        }
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AwpfocTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            MainScreen(modifier=  Modifier.padding(innerPadding))
        }
    }
}
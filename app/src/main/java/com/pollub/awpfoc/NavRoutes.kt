package com.pollub.awpfoc

enum class Screen {
    LOGIN_SCREEN,
    REGISTER_SCREEN,
    MAIN_SCREEN
}
sealed class NavRoutes(val route: String) {
    object MainScreen : NavRoutes(Screen.MAIN_SCREEN.name)
    object LoginScreen : NavRoutes(Screen.LOGIN_SCREEN.name)
    object RegisterScreen : NavRoutes(Screen.REGISTER_SCREEN.name)
}
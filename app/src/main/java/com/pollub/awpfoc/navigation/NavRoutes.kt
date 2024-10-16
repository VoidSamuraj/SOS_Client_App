package com.pollub.awpfoc.navigation

/**
 * Enum representing the different screens in the application.
 */
enum class Screen {
    LOGIN_SCREEN,
    REGISTER_SCREEN,
    MAIN_SCREEN
}

/**
 * Sealed class representing the navigation routes in the application.
 *
 * @param route The string route associated with the navigation destination.
 */
sealed class NavRoutes(val route: String) {
    object MainScreen : NavRoutes(Screen.MAIN_SCREEN.name)
    object LoginScreen : NavRoutes(Screen.LOGIN_SCREEN.name)
    object RegisterScreen : NavRoutes(Screen.REGISTER_SCREEN.name)
}
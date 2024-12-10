package com.pollub.awpfowc.presentation.nav

enum class Screen {
    MAIN_SCREEN,
    WAITING_SCREEN,
    MESSAGE_SCREEN,
    LOADING_SCREEN
}
sealed class NavRoutes(val route: String) {
    object MainScreen : NavRoutes(Screen.MAIN_SCREEN.name)
    object MessageScreen : NavRoutes(Screen.MESSAGE_SCREEN.name)
    object WaitingScreen : NavRoutes(Screen.WAITING_SCREEN.name)
    object LoadingScreen : NavRoutes(Screen.LOADING_SCREEN.name)
}
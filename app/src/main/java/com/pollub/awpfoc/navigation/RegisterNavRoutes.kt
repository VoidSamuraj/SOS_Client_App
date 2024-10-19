package com.pollub.awpfoc.navigation


/**
 * Sealed class representing the navigation routes in the Register Screen.
 *
 * @param route The string route associated with the navigation destination.
 */
sealed class RegisterNavRoutes(val route: String) {
    object RegisterScreen1 : RegisterNavRoutes("register_screen1")
    object RegisterScreen2 : RegisterNavRoutes("register_screen2/{login}/{password}")
    companion object {
        fun getRegisterScreen2Route(login: String, password: String): String {
            return "register_screen2/$login/$password"
        }
    }
}
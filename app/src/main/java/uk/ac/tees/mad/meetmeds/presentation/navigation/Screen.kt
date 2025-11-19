package uk.ac.tees.mad.meetmeds.presentation.navigation

sealed class Screen(val route: String) {

    // The merged Splash + Authentication screen
    object Auth : Screen("auth_screen")

    // The main screen after logging in, showing the medicine list
    object Main : Screen("main_screen")
}
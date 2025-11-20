package uk.ac.tees.mad.meetmeds.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.meetmeds.presentation.auth.AuthScreen

@Composable
fun NavGraph(
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Screen
        composable(Screen.Auth.route) {
            AuthScreen(navController = navController)
        }

        // Main Screen
        composable(route = Screen.Main.route) {
            MainScreen(navController = navController)
        }
    }
}
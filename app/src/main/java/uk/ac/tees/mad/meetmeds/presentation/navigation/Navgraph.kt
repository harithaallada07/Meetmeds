package uk.ac.tees.mad.meetmeds.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uk.ac.tees.mad.meetmeds.presentation.auth.AuthScreen
import uk.ac.tees.mad.meetmeds.presentation.checkout.CheckoutScreen

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

        // Checkout Screen
        composable(
            route = Screen.Checkout.route,
            arguments = listOf(
                navArgument("prescriptionUri") {
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val uriString = backStackEntry.arguments?.getString("prescriptionUri")
            // Convert empty string back to null if needed
            val finalUri = if (uriString.isNullOrEmpty()) null else uriString

            // Note: Navigation Compose automatically decodes the URI for you here
            CheckoutScreen(
                navController = navController,
                prescriptionUri = finalUri
            )
        }
    }
}
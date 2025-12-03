package uk.ac.tees.mad.meetmeds.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Auth : Screen("auth_screen")
    object Main : Screen("main_screen")

    // Route definition with an optional query parameter
    object Checkout : Screen("checkout_screen?prescriptionUri={prescriptionUri}") {
        fun createRoute(uri: String?): String {
            // IMPORTANT: Encode the URI because it contains slashes '/'
            // If uri is null, we pass an empty string
            val encodedUri = if (uri != null) Uri.encode(uri) else ""
            return "checkout_screen?prescriptionUri=$encodedUri"
        }
    }
}
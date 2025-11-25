package uk.ac.tees.mad.meetmeds.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist.MedicineListScreen

@Composable
fun MainScreen(
    navController: NavController
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Cart", "Profile")
    val icons = listOf(Icons.Default.Home, Icons.Default.ShoppingCart, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        // Switch content based on selection
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (selectedItem) {
                0 -> MedicineListScreen(navController = navController)
                1 -> CartPlaceholder() // To be implemented in Sprint 2/3
                2 -> ProfilePlaceholder()
            }
        }
    }
}

// Placeholder for Cart (Requested to keep complete code structure)
@Composable
fun CartPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Cart Screen - Coming Soon", style = MaterialTheme.typography.headlineMedium)
    }
}

// Placeholder for Profile
@Composable
fun ProfilePlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Profile Screen", style = MaterialTheme.typography.headlineMedium)
    }
}
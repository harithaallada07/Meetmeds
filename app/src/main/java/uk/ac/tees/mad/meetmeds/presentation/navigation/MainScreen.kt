package uk.ac.tees.mad.meetmeds.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import uk.ac.tees.mad.meetmeds.presentation.cart.CartScreen
import uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist.MedicineListScreen
import uk.ac.tees.mad.meetmeds.presentation.theme.MeetMedsTheme

@Composable
fun MainScreen(
    navController: NavController
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    MainScreenContent(
        selectedItem = selectedItem,
        onItemSelected = { selectedItem = it },
        content = {
            when (selectedItem) {
                0 -> MedicineListScreen(navController = navController)
                1 -> CartScreen(
                    navController = navController,
                    onCheckoutClick = { uriString ->
                        // FIX: Use createRoute to pass the specific URI
                        navController.navigate(Screen.Checkout.createRoute(uriString))
                    }
                )
                2 -> OrdersPlaceholder()
            }
        }
    )
}

// --- STATELESS COMPOSABLE ---
@Composable
fun MainScreenContent(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    content: @Composable () -> Unit
) {
    val items = listOf("Home", "Cart", "Orders")
    val icons = listOf(Icons.Default.Home, Icons.Default.ShoppingCart, Icons.Default.History)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { onItemSelected(index) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.background,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            content()
        }
    }
}

// --- PLACEHOLDERS ---

@Composable
fun OrdersPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Orders Screen",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

// --- PREVIEW ---

@Preview(showBackground = true, showSystemUi = true, name = "Main Screen Shell")
@Composable
fun MainScreenPreview() {
    MeetMedsTheme {
        var selectedItem by remember { mutableIntStateOf(0) }
        MainScreenContent(
            selectedItem = selectedItem,
            onItemSelected = { selectedItem = it },
            content = {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Content Area",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        )
    }
}
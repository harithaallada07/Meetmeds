package uk.ac.tees.mad.meetmeds.presentation.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.meetmeds.domain.model.CartItem
import uk.ac.tees.mad.meetmeds.presentation.navigation.Screen
import uk.ac.tees.mad.meetmeds.util.Resource

// 1. The Stateful Composable (Connects to ViewModel)
@Composable
fun CheckoutScreen(
    navController: NavController,
    prescriptionUri: String?,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val orderState = viewModel.orderState.value

    // Handle Side Effects
    LaunchedEffect(orderState) {
        when (orderState) {
            is Resource.Success -> {
                Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_LONG).show()
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            }
            is Resource.Error -> {
                Toast.makeText(context, orderState.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // Pass state down to the stateless composable
    CheckoutContent(
        street = viewModel.street.value,
        onStreetChange = { viewModel.street.value = it },
        city = viewModel.city.value,
        onCityChange = { viewModel.city.value = it },
        postcode = viewModel.postcode.value,
        onPostcodeChange = { viewModel.postcode.value = it },
        cartItems = viewModel.cartItems.value,
        total = viewModel.total,
        isLoading = orderState is Resource.Loading,
        onPlaceOrder = { viewModel.placeOrder(prescriptionUri) }
    )
}

// 2. The Stateless Composable (Pure UI - Easy to Preview)
@Composable
fun CheckoutContent(
    street: String,
    onStreetChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    postcode: String,
    onPostcodeChange: (String) -> Unit,
    cartItems: List<CartItem>,
    total: Double,
    isLoading: Boolean,
    onPlaceOrder: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp, bottom = 32.dp, end = 16.dp, top = 48.dp)
    ) {
        Text(
            text = "Checkout",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Address Form
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Delivery Address", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = street,
                    onValueChange = onStreetChange,
                    label = { Text("Street Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = onCityChange,
                        label = { Text("City") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = postcode,
                        onValueChange = onPostcodeChange,
                        label = { Text("Postcode") },
                        modifier = Modifier.weight(0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order Summary
        Text("Order Summary", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems.size) { index ->
                val item = cartItems[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.quantity}x ${item.name}", color = MaterialTheme.colorScheme.onBackground)
                    Text("£${String.format("%.2f", item.price * item.quantity)}", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "£${String.format("%.2f", total)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (street.isNotBlank() && city.isNotBlank()) {
                    onPlaceOrder()
                } else {
                    Toast.makeText(context, "Please enter address", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Confirm & Place Order")
            }
        }
    }
}

// 3. The Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutScreenPreview() {
    // Mock Data for Preview
    val mockCartItems = listOf(
        CartItem(
            medicineId = "1",
            name = "Paracetamol 500mg",
            price = 4.99,
            imageUrl = "",
            quantity = 2
        ),
        CartItem(
            medicineId = "2",
            name = "Ibuprofen 200mg",
            price = 3.50,
            imageUrl = "",
            quantity = 1
        )
    )

    MaterialTheme {
        CheckoutContent(
            street = "123 Baker Street",
            onStreetChange = {},
            city = "London",
            onCityChange = {},
            postcode = "NW1 6XE",
            onPostcodeChange = {},
            cartItems = mockCartItems,
            total = 13.48,
            isLoading = false,
            onPlaceOrder = {}
        )
    }
}
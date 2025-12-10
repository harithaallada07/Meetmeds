package uk.ac.tees.mad.meetmeds.presentation.orders

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.meetmeds.domain.model.Address
import uk.ac.tees.mad.meetmeds.domain.model.CartItem
import uk.ac.tees.mad.meetmeds.domain.model.Order
import uk.ac.tees.mad.meetmeds.util.Resource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 1. Stateful Composable (Connects to VM)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    OrderHistoryContent(state = state)
}

// 2. Stateless Composable (Pure UI)
@Composable
fun OrderHistoryContent(
    state: Resource<List<Order>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        when (state) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.message ?: "Error loading orders",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is Resource.Success -> {
                val orders = state.data ?: emptyList()
                if (orders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No past orders found.")
                    }
                } else {
                    LazyColumn {
                        items(orders) { order ->
                            OrderItem(order = order)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItem(order: Order) {
    var expanded by remember { mutableStateOf(false) }
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }, // Toggle expansion on tap
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // --- Header (Always Visible) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormatter.format(Date(order.timestamp)),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusChip(status = order.status)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "£${String.format("%.2f", order.totalPrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            // --- Expanded Details (Collapsible) ---
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(8.dp))

                    // Items List
                    Text("Items:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    order.items.forEach { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.name}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "£${String.format("%.2f", item.price * item.quantity)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Delivery Info
                    Text("Delivery To:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        text = "${order.address.street}, ${order.address.city}, ${order.address.postcode}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Prescription Status
                    if (!order.prescriptionUri.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = "Prescription Uploaded",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status.lowercase()) {
        "delivered" -> Color(0xFF4CAF50) // Green
        "pending" -> Color(0xFFFF9800)   // Orange
        else -> MaterialTheme.colorScheme.primary
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, color)
    ) {
        Text(
            text = status.uppercase(),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

// 3. Preview
@Preview(showBackground = true)
@Composable
fun OrderHistoryPreview() {
    // Mock Address
    val mockAddress = Address(
        street = "123 Baker Street",
        city = "London",
        postcode = "NW1 6XE"
    )

    // Mock Items
    val mockItems = listOf(
        CartItem("1", "Paracetamol 500mg", 2.50, "", 2),
        CartItem("2", "Vitamin C", 5.00, "", 1)
    )

    // Mock Orders
    val mockOrders = listOf(
        Order(
            userId = "user1",
            items = mockItems,
            address = mockAddress,
            totalPrice = 10.00,
            prescriptionUri = "http://example.com", // Has prescription
            status = "Pending",
            timestamp = System.currentTimeMillis()
        ),
        Order(
            userId = "user1",
            items = listOf(CartItem("3", "Ibuprofen", 3.50, "", 1)),
            address = mockAddress,
            totalPrice = 3.50,
            prescriptionUri = null, // No prescription
            status = "Delivered",
            timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
        )
    )

    MaterialTheme {
        OrderHistoryContent(state = Resource.Success(mockOrders))
    }
}
package uk.ac.tees.mad.meetmeds.presentation.cart

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uk.ac.tees.mad.meetmeds.domain.model.CartItem
import uk.ac.tees.mad.meetmeds.presentation.theme.MeetMedsTheme

// --- STATEFUL COMPOSABLE (Use this in Navigation) ---
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel(),
    onCheckoutClick: () -> Unit
) {
    val cartItems = viewModel.cartItems.value
    val total = viewModel.totalPrice.value
    val prescriptionUri = viewModel.prescriptionUri.value

    // Logic for image picker resides here in the stateful parent
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setPrescriptionUri(uri)
    }

    CartContent(
        cartItems = cartItems,
        totalPrice = total,
        prescriptionUri = prescriptionUri,
        onIncrease = { item -> viewModel.updateQuantity(item, item.quantity + 1) },
        onDecrease = { item -> viewModel.updateQuantity(item, item.quantity - 1) },
        onDelete = { item -> viewModel.removeItem(item) },
        onUploadClick = { launcher.launch("image/*") },
        onRemovePrescriptionClick = { viewModel.setPrescriptionUri(null) },
        onCheckoutClick = onCheckoutClick
    )
}

// --- STATELESS COMPOSABLE (Use this for Preview) ---
@Composable
fun CartContent(
    cartItems: List<CartItem>,
    totalPrice: Double,
    prescriptionUri: Uri?,
    onIncrease: (CartItem) -> Unit,
    onDecrease: (CartItem) -> Unit,
    onDelete: (CartItem) -> Unit,
    onUploadClick: () -> Unit,
    onRemovePrescriptionClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (cartItems.isEmpty()) {
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {

                // Scrollable Item List + Prescription Section
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item = item,
                            onIncrease = { onIncrease(item) },
                            onDecrease = { onDecrease(item) },
                            onDelete = { onDelete(item) }
                        )
                    }

                    // --- Prescription Upload Section (Inside Cart) ---
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Prescription (Optional)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (prescriptionUri != null) {
                                    // Show Preview
                                    AsyncImage(
                                        model = prescriptionUri,
                                        contentDescription = "Prescription Preview",
                                        modifier = Modifier
                                            .height(150.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(onClick = onRemovePrescriptionClick) {
                                        Text("Remove Prescription", color = MaterialTheme.colorScheme.error)
                                    }
                                } else {
                                    // Show Upload Button
                                    OutlinedButton(
                                        onClick = onUploadClick,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.UploadFile, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Upload Prescription")
                                    }
                                    Text(
                                        text = "Upload only if the medicines require it.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom Checkout Section
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "£${String.format("%.2f", totalPrice)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onCheckoutClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Proceed to Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "£${String.format("%.2f", item.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Quantity Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = MaterialTheme.colorScheme.onSurface)
                }
                Text(
                    text = "${item.quantity}",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onIncrease, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// --- PREVIEWS ---

// We create dummy data for the preview
private val dummyCartItems = listOf(
    CartItem(medicineId = "1", name = "Paracetamol", price = 2.50, quantity = 2, imageUrl = ""),
    CartItem(medicineId = "2", name = "Vitamin C", price = 5.00, quantity = 1, imageUrl = "")
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CartScreenContentPreview() {
    MeetMedsTheme {
        CartContent(
            cartItems = dummyCartItems,
            totalPrice = 10.00,
            prescriptionUri = null,
            onIncrease = {},
            onDecrease = {},
            onDelete = {},
            onUploadClick = {},
            onRemovePrescriptionClick = {},
            onCheckoutClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenEmptyPreview() {
    MeetMedsTheme {
        CartContent(
            cartItems = emptyList(),
            totalPrice = 0.00,
            prescriptionUri = null,
            onIncrease = {},
            onDecrease = {},
            onDelete = {},
            onUploadClick = {},
            onRemovePrescriptionClick = {},
            onCheckoutClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CartItemRowPreview() {
    MeetMedsTheme {
        CartItemRow(
            item = dummyCartItems[0],
            onIncrease = {},
            onDecrease = {},
            onDelete = {}
        )
    }
}
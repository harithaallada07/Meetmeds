package uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import uk.ac.tees.mad.meetmeds.domain.model.Medicine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListScreen(
    navController: NavController,
    viewModel: MedicineListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val searchQuery = viewModel.searchQuery.value

    // State to manage Bottom Sheet visibility and selected item
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // Light grey background for e-commerce feel
    ) {
        // Custom Search Bar Area
        Surface(
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Column {
                Text(
                    text = "Browse Medicines",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearch(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search pills, syrups...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color(0xFFF9F9F9)
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Content List
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error.isNotBlank()) {
                // Even if error, if we have cache (medicines not empty), we show it
                if (state.medicines.isNotEmpty()) {
                    // Show toast or snackbar logic here ideally
                } else {
                    Text(
                        text = state.error,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            if (state.medicines.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.medicines) { medicine ->
                        MedicineItem(
                            medicine = medicine,
                            onClick = {
                                selectedMedicine = medicine
                                showBottomSheet = true
                            }
                        )
                    }
                }
            } else if (!state.isLoading && state.error.isBlank()) {
                Text(
                    text = "No medicines found",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }
        }
    }

    // Bottom Sheet Logic
    if (showBottomSheet && selectedMedicine != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            MedicineDetailContent(
                medicine = selectedMedicine!!,
                onAddToCart = { quantity ->
                    // TODO: Call ViewModel to add to cart (Sprint 2)
                    showBottomSheet = false
                }
            )
        }
    }
}

@Composable
fun MedicineItem(
    medicine: Medicine,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Placeholder (Rounded Square)
            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFFE0E0E0)
            ) {
                // In real app, use Coil: AsyncImage(model = medicine.imageUrl, ...)
                Box(contentAlignment = Alignment.Center) {
                    Text("IMG", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = medicine.dosage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if(medicine.inStock) "In Stock" else "Out of Stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = if(medicine.inStock) Color(0xFF4CAF50) else Color.Red
                )
            }

            // Price & Add Button visual
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "£${String.format("%.2f", medicine.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Mini Add Button visual
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicineDetailContent(
    medicine: Medicine,
    onAddToCart: (Int) -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = medicine.dosage,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }
            Text(
                text = "£${String.format("%.2f", medicine.price)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray)

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = medicine.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quantity Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = { if (quantity > 1) quantity-- },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.Black)
            }

            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            FilledIconButton(
                onClick = { quantity++ },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add to Cart Button
        Button(
            onClick = { onAddToCart(quantity) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = medicine.inStock
        ) {
            Icon(Icons.Default.AddShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (medicine.inStock) "Add $quantity to Cart - £${String.format("%.2f", medicine.price * quantity)}" else "Unavailable",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
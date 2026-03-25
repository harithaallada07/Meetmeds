package uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uk.ac.tees.mad.meetmeds.domain.model.Medicine
import uk.ac.tees.mad.meetmeds.presentation.theme.MeetMedsTheme

@Composable
fun MedicineListScreen(
    navController: NavController,
    viewModel: MedicineListViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val searchQuery = viewModel.searchQuery.value

    // Pass real state to the content composable
    MedicineListContent(
        state = state,
        searchQuery = searchQuery,
        onSearch = { viewModel.onSearch(it) },
        onAddToCart = { medicine, quantity ->
            viewModel.addToCart(medicine, quantity)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineListContent(
    state: MedicineListState,
    searchQuery: String,
    onSearch: (String) -> Unit,
    onAddToCart: (Medicine, Int) -> Unit
) {
    // Local state for Bottom Sheet
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar Area
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search pills, syrups...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
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
                if (state.medicines.isNotEmpty()) {
                    // Show cached data logic
                } else {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Bottom Sheet Logic
    if (showBottomSheet && selectedMedicine != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MedicineDetailContent(
                medicine = selectedMedicine!!,
                onAddToCart = { quantity ->
                    onAddToCart(selectedMedicine!!, quantity)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = medicine.imageUrl,
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = medicine.dosage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Stock Status - Semantic Colors
                val stockColor = if (medicine.inStock) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                Text(
                    text = if (medicine.inStock) "In Stock" else "Out of Stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = stockColor
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "£${String.format("%.2f", medicine.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.background,
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
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicine.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = medicine.dosage,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "£${String.format("%.2f", medicine.price)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = medicine.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = { if (quantity > 1) quantity-- },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }
            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            FilledIconButton(
                onClick = { quantity++ },
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MedicineListScreenPreview() {
    MeetMedsTheme {
        MedicineListContent(
            state = MedicineListState(
                isLoading = false,
                medicines = listOf(
                    Medicine("1", "Paracetamol", "500mg", 2.50, "Pain reliever", inStock = true),
                    Medicine("2", "Ibuprofen", "400mg", 5.00, "Anti-inflammatory", inStock = false),
                    Medicine("3", "Amoxicillin", "250mg", 8.99, "Antibiotic", inStock = true)
                )
            ),
            searchQuery = "",
            onSearch = {},
            onAddToCart = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MedicineItemPreview() {
    MeetMedsTheme {
        MedicineItem(
            medicine = Medicine("1", "Paracetamol", "500mg", 2.50, "Pain reliever", inStock = true),
            onClick = {}
        )
    }
}
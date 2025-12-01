package uk.ac.tees.mad.meetmeds.domain.model

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val address: Address = Address(),
    val totalPrice: Double = 0.0,
    val status: String = "Pending", // Pending, Confirmed, Delivered
    val timestamp: Long = System.currentTimeMillis(),
    val prescriptionUri: String? = null // Storing the URI string for now
)
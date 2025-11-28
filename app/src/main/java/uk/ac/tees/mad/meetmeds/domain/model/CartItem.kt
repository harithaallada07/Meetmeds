package uk.ac.tees.mad.meetmeds.domain.model

data class CartItem(
    val medicineId: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int
)
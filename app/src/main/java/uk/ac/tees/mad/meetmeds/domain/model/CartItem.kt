package uk.ac.tees.mad.meetmeds.domain.model

data class CartItem(
    val medicineId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val quantity: Int = 0
)
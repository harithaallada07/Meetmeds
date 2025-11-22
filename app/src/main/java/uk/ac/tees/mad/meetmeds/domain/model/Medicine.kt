package uk.ac.tees.mad.meetmeds.domain.model

data class Medicine(
    val id: String = "",
    val name: String = "",
    val dosage: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = "",
    val inStock: Boolean = true
)
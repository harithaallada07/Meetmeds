package uk.ac.tees.mad.meetmeds.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import uk.ac.tees.mad.meetmeds.domain.model.CartItem

@Entity(tableName = "cart_items")
data class CartEntity(
    @PrimaryKey val medicineId: String,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int
)

fun CartEntity.toDomain(): CartItem {
    return CartItem(
        medicineId = medicineId,
        name = name,
        price = price,
        imageUrl = imageUrl,
        quantity = quantity
    )
}

fun CartItem.toEntity(): CartEntity {
    return CartEntity(
        medicineId = medicineId,
        name = name,
        price = price,
        imageUrl = imageUrl,
        quantity = quantity
    )
}
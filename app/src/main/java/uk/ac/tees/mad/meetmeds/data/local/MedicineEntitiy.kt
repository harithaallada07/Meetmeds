package uk.ac.tees.mad.meetmeds.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import uk.ac.tees.mad.meetmeds.domain.model.Medicine

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val dosage: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
    val inStock: Boolean
)

// Mapper functions
fun MedicineEntity.toDomain(): Medicine {
    return Medicine(
        id = id,
        name = name,
        dosage = dosage,
        price = price,
        description = description,
        imageUrl = imageUrl,
        inStock = inStock
    )
}

fun Medicine.toEntity(): MedicineEntity {
    return MedicineEntity(
        id = id,
        name = name,
        dosage = dosage,
        price = price,
        description = description,
        imageUrl = imageUrl,
        inStock = inStock
    )
}
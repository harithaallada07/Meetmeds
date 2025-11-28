package uk.ac.tees.mad.meetmeds.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    fun getCartItems(): Flow<List<CartEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartEntity)

    @Query("DELETE FROM cart_items WHERE medicineId = :medicineId")
    suspend fun removeCartItem(medicineId: String)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    // Helper to check if item exists
    @Query("SELECT * FROM cart_items WHERE medicineId = :id LIMIT 1")
    suspend fun getCartItemById(id: String): CartEntity?
}
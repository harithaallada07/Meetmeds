package uk.ac.tees.mad.meetmeds.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.meetmeds.domain.model.CartItem
import uk.ac.tees.mad.meetmeds.domain.model.Medicine

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(medicine: Medicine, quantity: Int)
    suspend fun removeFromCart(medicineId: String)
    suspend fun updateQuantity(medicineId: String, newQuantity: Int)
    suspend fun clearCart()
}
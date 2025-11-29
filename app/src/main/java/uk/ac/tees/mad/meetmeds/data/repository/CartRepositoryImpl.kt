package uk.ac.tees.mad.meetmeds.data.repository

import uk.ac.tees.mad.meetmeds.data.local.CartDao
import uk.ac.tees.mad.meetmeds.data.local.toDomain
import uk.ac.tees.mad.meetmeds.data.local.toEntity
import uk.ac.tees.mad.meetmeds.domain.model.CartItem
import uk.ac.tees.mad.meetmeds.domain.model.Medicine
import uk.ac.tees.mad.meetmeds.domain.repository.CartRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addToCart(medicine: Medicine, quantity: Int) {
        val existingItem = cartDao.getCartItemById(medicine.id)
        val newQuantity = if (existingItem != null) {
            existingItem.quantity + quantity
        } else {
            quantity
        }

        val item = CartItem(
            medicineId = medicine.id,
            name = medicine.name,
            price = medicine.price,
            imageUrl = medicine.imageUrl,
            quantity = newQuantity
        )
        cartDao.insertCartItem(item.toEntity())
    }

    override suspend fun updateQuantity(medicineId: String, newQuantity: Int) {
        val existingItem = cartDao.getCartItemById(medicineId)
        existingItem?.let {
            if (newQuantity > 0) {
                cartDao.insertCartItem(it.copy(quantity = newQuantity))
            } else {
                cartDao.removeCartItem(medicineId)
            }
        }
    }

    override suspend fun removeFromCart(medicineId: String) {
        cartDao.removeCartItem(medicineId)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }
}
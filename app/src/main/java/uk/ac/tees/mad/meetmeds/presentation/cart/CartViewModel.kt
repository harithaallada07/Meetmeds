package uk.ac.tees.mad.meetmeds.presentation.cart

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.meetmeds.domain.model.CartItem
import uk.ac.tees.mad.meetmeds.domain.repository.CartRepository
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cartItems = mutableStateOf<List<CartItem>>(emptyList())
    val cartItems: State<List<CartItem>> = _cartItems

    private val _totalPrice = mutableDoubleStateOf(0.0)
    val totalPrice: State<Double> = _totalPrice

    // State to hold the selected prescription image
    private val _prescriptionUri = mutableStateOf<Uri?>(null)
    val prescriptionUri: State<Uri?> = _prescriptionUri

    init {
        getCartItems()
    }

    private fun getCartItems() {
        cartRepository.getCartItems().onEach { items ->
            _cartItems.value = items
            calculateTotal(items)
        }.launchIn(viewModelScope)
    }

    private fun calculateTotal(items: List<CartItem>) {
        _totalPrice.doubleValue = items.sumOf { it.price * it.quantity }
    }

    fun updateQuantity(item: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item.medicineId, newQuantity)
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            cartRepository.removeFromCart(item.medicineId)
        }
    }

    fun setPrescriptionUri(uri: Uri?) {
        _prescriptionUri.value = uri
    }
}
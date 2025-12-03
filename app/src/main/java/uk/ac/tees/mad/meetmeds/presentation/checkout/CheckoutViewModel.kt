package uk.ac.tees.mad.meetmeds.presentation.checkout

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.meetmeds.domain.model.Address
import uk.ac.tees.mad.meetmeds.domain.model.CartItem
import uk.ac.tees.mad.meetmeds.domain.model.Order
import uk.ac.tees.mad.meetmeds.domain.repository.CartRepository
import uk.ac.tees.mad.meetmeds.domain.repository.OrderRepository
import uk.ac.tees.mad.meetmeds.util.Resource
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    // UI States
    var street = mutableStateOf("")
    var city = mutableStateOf("")
    var postcode = mutableStateOf("")

    private val _orderState = mutableStateOf<Resource<Boolean>?>(null)
    val orderState: State<Resource<Boolean>?> = _orderState

    private val _cartItems = mutableStateOf<List<CartItem>>(emptyList())
    val cartItems: State<List<CartItem>> = _cartItems

    // Total calculation
    val total: Double
        get() = _cartItems.value.sumOf { it.price * it.quantity }

    init {
        loadCart()
    }

    private fun loadCart() {
        cartRepository.getCartItems().onEach { items ->
            _cartItems.value = items
        }.launchIn(viewModelScope)
    }

    fun placeOrder(prescriptionUri: String?) {
        val userId = firebaseAuth.currentUser?.uid ?: return

        val address = Address(street.value, city.value, postcode.value)

        val order = Order(
            userId = userId,
            items = _cartItems.value,
            address = address,
            totalPrice = total,
            prescriptionUri = prescriptionUri
        )

        viewModelScope.launch {
            orderRepository.placeOrder(order).collect { result ->
                _orderState.value = result
                if (result is Resource.Success) {
                    // Clear local cart after successful server order
                    cartRepository.clearCart()
                }
            }
        }
    }
}
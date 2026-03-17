package uk.ac.tees.mad.meetmeds.presentation.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.meetmeds.domain.model.Order
import uk.ac.tees.mad.meetmeds.domain.repository.AuthRepository
import uk.ac.tees.mad.meetmeds.domain.repository.OrderRepository
import uk.ac.tees.mad.meetmeds.util.Resource

class OrderHistoryViewModel(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf<Resource<List<Order>>>(Resource.Loading())
    val state: State<Resource<List<Order>>> = _state

    private val _logoutState = mutableStateOf<Resource<Boolean>?>(null)
    val logoutState: State<Resource<Boolean>?> = _logoutState

    init {
        getOrders()
    }

    private fun getOrders() {
        orderRepository.getOrders().onEach { result ->
            _state.value = result
        }.launchIn(viewModelScope)
    }

    fun logout() {
        authRepository.logout().onEach { result ->
            _logoutState.value = result
        }.launchIn(viewModelScope)
    }

    class Factory(
        private val orderRepository: OrderRepository,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OrderHistoryViewModel(orderRepository, authRepository) as T
        }
    }
}
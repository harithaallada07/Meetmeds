package uk.ac.tees.mad.meetmeds.presentation.orders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.meetmeds.domain.model.Order
import uk.ac.tees.mad.meetmeds.domain.repository.AuthRepository
import uk.ac.tees.mad.meetmeds.domain.repository.OrderRepository
import uk.ac.tees.mad.meetmeds.util.Resource
import javax.inject.Inject

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = mutableStateOf<Resource<List<Order>>>(Resource.Loading())
    val state: State<Resource<List<Order>>> = _state

    // State to track logout status
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
}
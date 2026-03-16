package uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uk.ac.tees.mad.meetmeds.domain.model.Medicine
import uk.ac.tees.mad.meetmeds.domain.repository.CartRepository
import uk.ac.tees.mad.meetmeds.domain.repository.MedicineRepository
import uk.ac.tees.mad.meetmeds.util.Resource

class MedicineListViewModel(
    private val repository: MedicineRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _state = mutableStateOf(MedicineListState())
    val state: State<MedicineListState> = _state

    private var allMedicines = listOf<Medicine>()

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    init {
        getMedicines()
    }

    private fun getMedicines() {
        repository.getMedicines().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    allMedicines = result.data ?: emptyList()
                    if (_searchQuery.value.isEmpty()) {
                        _state.value = MedicineListState(medicines = allMedicines)
                    } else {
                        onSearch(_searchQuery.value)
                    }
                }
                is Resource.Error -> {
                    if (allMedicines.isNotEmpty()) {
                        _state.value = _state.value.copy(
                            error = result.message ?: "Sync failed",
                            isLoading = false
                        )
                    } else {
                        _state.value = MedicineListState(error = result.message ?: "An unexpected error occurred")
                    }
                }
                is Resource.Loading -> {
                    if (allMedicines.isEmpty()) {
                        _state.value = MedicineListState(isLoading = true)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onSearch(query: String) {
        _searchQuery.value = query
        if (query.isEmpty()) {
            _state.value = _state.value.copy(medicines = allMedicines)
        } else {
            val filtered = allMedicines.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            _state.value = _state.value.copy(medicines = filtered)
        }
    }

    fun addToCart(medicine: Medicine, quantity: Int) {
        viewModelScope.launch {
            cartRepository.addToCart(medicine, quantity)
        }
    }

    class Factory(
        private val medicineRepository: MedicineRepository,
        private val cartRepository: CartRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MedicineListViewModel(medicineRepository, cartRepository) as T
        }
    }
}
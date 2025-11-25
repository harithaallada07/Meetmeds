package uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uk.ac.tees.mad.meetmeds.domain.model.Medicine
import uk.ac.tees.mad.meetmeds.domain.repository.MedicineRepository
import uk.ac.tees.mad.meetmeds.util.Resource
import javax.inject.Inject

@HiltViewModel
class MedicineListViewModel @Inject constructor(
    private val repository: MedicineRepository
) : ViewModel() {

    private val _state = mutableStateOf(MedicineListState())
    val state: State<MedicineListState> = _state

    // Hold full data for memory filtering
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
                    // Re-apply search query if exists
                    if (_searchQuery.value.isEmpty()) {
                        _state.value = MedicineListState(medicines = allMedicines)
                    } else {
                        onSearch(_searchQuery.value)
                    }
                }
                is Resource.Error -> {
                    // If we have data from cache (allMedicines not empty), keep showing it but set error
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
                    // Only show loading if we have no data yet
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
}
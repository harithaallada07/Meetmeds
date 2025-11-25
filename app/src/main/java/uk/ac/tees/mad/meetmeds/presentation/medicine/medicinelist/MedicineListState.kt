package uk.ac.tees.mad.meetmeds.presentation.medicine.medicinelist

import uk.ac.tees.mad.meetmeds.domain.model.Medicine

data class MedicineListState(
    val isLoading: Boolean = false,
    val medicines: List<Medicine> = emptyList(),
    val error: String = ""
)
package uk.ac.tees.mad.meetmeds.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.meetmeds.domain.model.Medicine
import uk.ac.tees.mad.meetmeds.util.Resource

interface MedicineRepository {
    fun getMedicines(): Flow<Resource<List<Medicine>>>
}
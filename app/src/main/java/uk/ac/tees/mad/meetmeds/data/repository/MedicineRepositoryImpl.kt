package uk.ac.tees.mad.meetmeds.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uk.ac.tees.mad.meetmeds.domain.model.Medicine
import uk.ac.tees.mad.meetmeds.domain.repository.MedicineRepository
import uk.ac.tees.mad.meetmeds.util.Resource
import javax.inject.Inject

class MedicineRepositoryImpl @Inject constructor() : MedicineRepository {

    // Simulating a network/database call with dummy data
    override fun getMedicines(): Flow<Resource<List<Medicine>>> = flow {
        emit(Resource.Loading())
        delay(1000) // Simulate network delay

        val dummyList = listOf(
            Medicine("1", "Paracetamol", "500mg", 2.50, "Pain reliever and fever reducer.", inStock = true),
            Medicine("2", "Amoxicillin", "250mg", 8.99, "Antibiotic used to treat bacterial infections.", inStock = true),
            Medicine("3", "Ibuprofen", "400mg", 3.20, "Nonsteroidal anti-inflammatory drug (NSAID).", inStock = true),
            Medicine("4", "Cetirizine", "10mg", 4.50, "Antihistamine used to treat hay fever and allergies.", inStock = false),
            Medicine("5", "Metformin", "500mg", 5.00, "First-line medication for the treatment of type 2 diabetes.", inStock = true)
        )

        emit(Resource.Success(dummyList))
    }
}
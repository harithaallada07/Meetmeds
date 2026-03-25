package uk.ac.tees.mad.meetmeds.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.meetmeds.data.local.MedicineDao
import uk.ac.tees.mad.meetmeds.data.local.toDomain
import uk.ac.tees.mad.meetmeds.data.local.toEntity
import uk.ac.tees.mad.meetmeds.domain.model.Medicine
import uk.ac.tees.mad.meetmeds.domain.repository.MedicineRepository
import uk.ac.tees.mad.meetmeds.util.Resource
import javax.inject.Inject

class MedicineRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dao: MedicineDao
) : MedicineRepository {

    override fun getMedicines(): Flow<Resource<List<Medicine>>> = channelFlow {
        // 1. Emit Loading
        send(Resource.Loading())

        // 2. Launch a coroutine to observe Local Database (Single Source of Truth)
        val localJob = launch {
            dao.getMedicines().collect { entities ->
                send(Resource.Success(entities.map { it.toDomain() }))
            }
        }

        // 3. Fetch from Firestore to update Local Database
        try {
            val snapshot = firestore.collection("medicines").get().await()
            val medicines = snapshot.toObjects(Medicine::class.java)

            // Clear old cache and insert new data
            // (In a real app, you might want more complex diffing, but this ensures freshness)
            dao.clearMedicines()
            dao.insertMedicines(medicines.map { it.toEntity() })

        } catch (e: Exception) {
            // If network fails, we just emit Error but the local flow continues to show cached data
            send(Resource.Error(e.localizedMessage ?: "Failed to sync data", null))
        }
    }
}
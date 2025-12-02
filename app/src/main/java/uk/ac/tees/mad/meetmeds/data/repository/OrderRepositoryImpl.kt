package uk.ac.tees.mad.meetmeds.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.meetmeds.domain.model.Order
import uk.ac.tees.mad.meetmeds.domain.repository.OrderRepository
import uk.ac.tees.mad.meetmeds.util.Resource
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : OrderRepository {

    override suspend fun placeOrder(order: Order): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            // Generate a new document reference to get an ID
            val docRef = firestore.collection("orders").document()

            // Create the final order object with the generated ID
            val finalOrder = order.copy(id = docRef.id)

            // Save to Firestore
            docRef.set(finalOrder).await()

            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to place order"))
        }
    }
}
package uk.ac.tees.mad.meetmeds.domain.repository

import uk.ac.tees.mad.meetmeds.domain.model.Order
import uk.ac.tees.mad.meetmeds.util.Resource
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun placeOrder(order: Order): Flow<Resource<Boolean>>
}
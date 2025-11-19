package uk.ac.tees.mad.meetmeds.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.meetmeds.domain.model.AuthResult
import uk.ac.tees.mad.meetmeds.util.Resource

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<AuthResult>
    fun registerUser(email: String, password: String): Flow<AuthResult>
    fun getCurrentUser(): Flow<Boolean>
    fun logout(): Flow<Resource<Boolean>>
    fun resetPassword(email: String): Flow<AuthResult>
}
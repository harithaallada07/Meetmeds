package uk.ac.tees.mad.meetmeds.domain.repository

import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.meetmeds.domain.model.UserProfile
import uk.ac.tees.mad.meetmeds.util.Resource

interface UserRepository {
    suspend fun saveUserProfile(userProfile: UserProfile): Flow<Resource<Boolean>>
    fun getUserProfile(userId: String): Flow<Resource<UserProfile>>
    suspend fun updateUserProfile(userProfile: UserProfile): Resource<Boolean>
}
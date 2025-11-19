package uk.ac.tees.mad.meetmeds.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.meetmeds.domain.model.UserProfile
import uk.ac.tees.mad.meetmeds.domain.repository.UserRepository
import uk.ac.tees.mad.meetmeds.util.Constants
import uk.ac.tees.mad.meetmeds.util.Resource
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
    override suspend fun saveUserProfile(userProfile: UserProfile): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection(Constants.USERS_COLLECTION)
                .document(userProfile.uid)
                .set(userProfile)
                .await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to save profile"))
        }
    }

    override fun getUserProfile(userId: String): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())
        try {
            val document = firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val userProfile = document.toObject(UserProfile::class.java)
                emit(Resource.Success(userProfile!!))
            } else {
                // Create default profile if doesn't exist
                val defaultProfile = UserProfile(
                    uid = userId,
                    email = firebaseAuth.currentUser?.email ?: "",
                    name = "MeetMeds User"
                )
                // Use the flow-based save operation
                saveUserProfile(defaultProfile).collect {
                    if (it is Resource.Success) {
                        emit(Resource.Success(defaultProfile))
                    } else if (it is Resource.Error) {
                        emit(Resource.Error(it.message ?: "Failed to create default profile"))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to get profile"))
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile): Resource<Boolean> {
        return try {
            firestore.collection(Constants.USERS_COLLECTION).document(userProfile.uid)
                .set(userProfile)
                .await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Failed to update user profile")
        }
    }
}
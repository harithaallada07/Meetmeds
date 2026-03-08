package uk.ac.tees.mad.meetmeds.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import uk.ac.tees.mad.meetmeds.data.local.MeetMedsDatabase
import uk.ac.tees.mad.meetmeds.data.repository.AuthRepositoryImpl
import uk.ac.tees.mad.meetmeds.data.repository.CartRepositoryImpl
import uk.ac.tees.mad.meetmeds.data.repository.MedicineRepositoryImpl
import uk.ac.tees.mad.meetmeds.data.repository.OrderRepositoryImpl
import uk.ac.tees.mad.meetmeds.data.repository.UserRepositoryImpl
import uk.ac.tees.mad.meetmeds.domain.repository.AuthRepository
import uk.ac.tees.mad.meetmeds.domain.repository.CartRepository
import uk.ac.tees.mad.meetmeds.domain.repository.MedicineRepository
import uk.ac.tees.mad.meetmeds.domain.repository.OrderRepository
import uk.ac.tees.mad.meetmeds.domain.repository.UserRepository

/**
 * Manual dependency container — replaces Hilt's SingletonComponent.
 * All dependencies are lazy singletons, created only when first accessed.
 */
class AppContainer(private val application: Application) {

    // Firebase
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firebaseFirestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Room Database
    val database: MeetMedsDatabase by lazy {
        Room.databaseBuilder(
            application,
            MeetMedsDatabase::class.java,
            "meetmeds_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(firebaseAuth)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(firebaseFirestore, firebaseAuth)
    }

    val medicineRepository: MedicineRepository by lazy {
        MedicineRepositoryImpl(firebaseFirestore, database.medicineDao())
    }

    val cartRepository: CartRepository by lazy {
        CartRepositoryImpl(database.cartDao())
    }

    val orderRepository: OrderRepository by lazy {
        OrderRepositoryImpl(firebaseFirestore, firebaseAuth)
    }
}
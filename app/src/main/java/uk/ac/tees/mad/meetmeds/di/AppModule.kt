package uk.ac.tees.mad.meetmeds.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //Provides a singleton instance of FirebaseAuth
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // Provides a singleton instance of FirebaseFirestore
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    //Provides a singleton instance of our AuthRepository
    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth
    ): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideDatabase(app: Application): MeetMedsDatabase {
        return Room.databaseBuilder(
            app,
            MeetMedsDatabase::class.java,
            "meetmeds_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): UserRepository = UserRepositoryImpl(firestore, auth)

    @Provides
    @Singleton
    fun provideMedicineRepository(
        firestore: FirebaseFirestore,
        db: MeetMedsDatabase
    ): MedicineRepository = MedicineRepositoryImpl(firestore, db.medicineDao())

    @Provides
    @Singleton
    fun provideCartRepository(
        db: MeetMedsDatabase
    ): CartRepository = CartRepositoryImpl(db.cartDao())

    @Provides
    @Singleton
    fun provideOrderRepository(
        firestore: FirebaseFirestore
    ): OrderRepository = OrderRepositoryImpl(firestore)
}
package uk.ac.tees.mad.meetmeds.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MedicineEntity::class, CartEntity::class], version = 2, exportSchema = false)
abstract class MeetMedsDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun cartDao(): CartDao
}
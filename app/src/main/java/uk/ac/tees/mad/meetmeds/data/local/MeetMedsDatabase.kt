package uk.ac.tees.mad.meetmeds.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.ac.tees.mad.meetmeds.data.local.MedicineEntity

@Database(entities = [MedicineEntity::class], version = 2, exportSchema = false)
abstract class MeetMedsDatabase : RoomDatabase() {
    abstract fun medicineDao(): MedicineDao
    abstract fun cartDao(): CartDao
}
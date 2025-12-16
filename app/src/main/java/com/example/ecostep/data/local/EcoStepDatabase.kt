package com.example.ecostep.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DailyLog::class],
    version = 4, // Incrementat pentru noua coloană userId
    exportSchema = false
)
abstract class EcoStepDatabase : RoomDatabase() {

    abstract fun dailyLogDao(): DailyLogDao

    companion object {
        @Volatile
        private var INSTANCE: EcoStepDatabase? = null

        fun getInstance(context: Context): EcoStepDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    EcoStepDatabase::class.java,
                    "ecostep_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

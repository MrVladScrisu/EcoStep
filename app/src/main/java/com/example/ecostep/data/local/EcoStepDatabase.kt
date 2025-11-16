package com.example.ecostep.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DailyLog::class],
    version = 1,
    exportSchema = false
)
abstract class EcoStepDatabase : RoomDatabase() {
    abstract fun dailyLogDao(): DailyLogDao
}

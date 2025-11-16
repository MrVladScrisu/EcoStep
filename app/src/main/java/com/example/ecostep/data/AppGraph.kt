package com.example.ecostep.data

import android.content.Context
import androidx.room.Room
import com.example.ecostep.data.local.EcoStepDatabase
import com.example.ecostep.data.repository.DailyLogRepository
import com.example.ecostep.data.repository.DailyLogRepositoryImpl

object AppGraph {

    lateinit var database: EcoStepDatabase
        private set

    lateinit var dailyLogRepository: DailyLogRepository
        private set

    fun init(context: Context) {
        database = Room.databaseBuilder(
            context,
            EcoStepDatabase::class.java,
            "ecostep_db"
        ).build()

        dailyLogRepository = DailyLogRepositoryImpl(
            database.dailyLogDao()
        )
    }
}

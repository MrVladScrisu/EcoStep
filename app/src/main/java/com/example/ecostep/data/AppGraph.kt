package com.example.ecostep.data

import android.content.Context
import com.example.ecostep.data.local.EcoStepDatabase
import com.example.ecostep.data.repository.DailyLogRepository
import com.example.ecostep.data.repository.DailyLogRepositoryImpl

object AppGraph {

    lateinit var dailyLogRepository: DailyLogRepository
        private set

    fun provide(context: Context) {
        val database = EcoStepDatabase.getInstance(context)
        dailyLogRepository = DailyLogRepositoryImpl(database.dailyLogDao())
    }
}

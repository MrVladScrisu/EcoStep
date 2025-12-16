package com.example.ecostep.data.repository

import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.data.local.DailyLogDao
import kotlinx.coroutines.flow.Flow

class DailyLogRepositoryImpl(
    private val dao: DailyLogDao
) : DailyLogRepository {

    override suspend fun insertDailyLog(log: DailyLog) {
        dao.insertDailyLog(log)
    }

    override suspend fun updateDailyLog(log: DailyLog) {
        dao.updateDailyLog(
            id = log.id,
            transportType = log.transportType,
            transportDistanceKm = log.transportDistanceKm,
            meatPortions = log.meatPortions,
            veggiesPortions = log.veggiesPortions,
            dairyPortions = log.dairyPortions,
            junkFoodPortions = log.junkFoodPortions,
            electricityKwh = log.electricityKwh,
            waterLiters = log.waterLiters,
            wasteBags = log.wasteBags,
            recycledToday = log.recycledToday,
            steps = log.steps,
            ecoScore = log.ecoScore
        )
    }

    override suspend fun getDailyLogByDate(userId: String, date: String): DailyLog? {
        return dao.getDailyLogByDate(userId, date)
    }

    override suspend fun getDailyLogById(id: Long): DailyLog? {
        return dao.getDailyLogById(id)
    }

    override fun getDailyLogs(userId: String): Flow<List<DailyLog>> = dao.getDailyLogs(userId)

    override suspend fun deleteDailyLog(log: DailyLog) {
        dao.deleteDailyLog(log)
    }
}

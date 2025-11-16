package com.example.ecostep.data.repository

import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.data.local.DailyLogDao
import kotlinx.coroutines.flow.Flow

class DailyLogRepositoryImpl(
    private val dao: DailyLogDao
) : DailyLogRepository {

    override fun getAllLogs(): Flow<List<DailyLog>> = dao.getAllLogs()

    override suspend fun getLogById(id: Long): DailyLog? = dao.getLogById(id)

    override suspend fun upsertLog(log: DailyLog) {
        dao.insertLog(log)
    }

    override suspend fun deleteLog(log: DailyLog) {
        dao.deleteLog(log)
    }
}

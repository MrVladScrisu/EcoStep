package com.example.ecostep.data.repository

import com.example.ecostep.data.local.DailyLog
import kotlinx.coroutines.flow.Flow

interface DailyLogRepository {

    suspend fun insertDailyLog(log: DailyLog)

    suspend fun updateDailyLog(log: DailyLog)

    suspend fun getDailyLogByDate(userId: String, date: String): DailyLog?

    suspend fun getDailyLogById(id: Long): DailyLog?

    fun getDailyLogs(userId: String): Flow<List<DailyLog>>

    suspend fun deleteDailyLog(log: DailyLog)
}

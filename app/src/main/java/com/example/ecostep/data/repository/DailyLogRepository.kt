package com.example.ecostep.data.repository

import com.example.ecostep.data.local.DailyLog
import kotlinx.coroutines.flow.Flow

interface DailyLogRepository {

    // READ toate log-urile, ca Flow pentru a putea fi observate în UI
    fun getAllLogs(): Flow<List<DailyLog>>

    // READ dupa id (pentru ecranul de detaliu/edit)
    suspend fun getLogById(id: Long): DailyLog?

    // CREATE / UPDATE (upsert = insert sau replace)
    suspend fun upsertLog(log: DailyLog)

    // DELETE
    suspend fun deleteLog(log: DailyLog)
}

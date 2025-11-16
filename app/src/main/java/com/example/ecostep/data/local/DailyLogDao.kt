package com.example.ecostep.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {

    @Query("SELECT * FROM daily_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE id = :id LIMIT 1")
    suspend fun getLogById(id: Long): DailyLog?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLog)

    @Update
    suspend fun updateLog(log: DailyLog)

    @Delete
    suspend fun deleteLog(log: DailyLog)
}

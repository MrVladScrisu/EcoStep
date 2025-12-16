package com.example.ecostep.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyLog(log: DailyLog)

    @Query("UPDATE daily_logs SET " +
            "transportType = :transportType, " +
            "transportDistanceKm = :transportDistanceKm, " +
            "meatPortions = :meatPortions, " +
            "veggiesPortions = :veggiesPortions, " +
            "dairyPortions = :dairyPortions, " +
            "junkFoodPortions = :junkFoodPortions, " +
            "electricityKwh = :electricityKwh, " +
            "waterLiters = :waterLiters, " +
            "wasteBags = :wasteBags, " +
            "recycledToday = :recycledToday, " +
            "steps = :steps, " +
            "ecoScore = :ecoScore " +
            "WHERE id = :id")
    suspend fun updateDailyLog(
        id: Long,
        transportType: String,
        transportDistanceKm: Double,
        meatPortions: Int,
        veggiesPortions: Int,
        dairyPortions: Int,
        junkFoodPortions: Int,
        electricityKwh: Double,
        waterLiters: Double,
        wasteBags: Int,
        recycledToday: Boolean,
        steps: Int,
        ecoScore: Double
    )

    @Query("SELECT * FROM daily_logs WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getDailyLogByDate(userId: String, date: String): DailyLog?

    @Query("SELECT * FROM daily_logs WHERE id = :id LIMIT 1")
    suspend fun getDailyLogById(id: Long): DailyLog?

    @Query("SELECT * FROM daily_logs WHERE userId = :userId ORDER BY date DESC, id DESC")
    fun getDailyLogs(userId: String): Flow<List<DailyLog>>

    @Delete
    suspend fun deleteDailyLog(log: DailyLog)
}

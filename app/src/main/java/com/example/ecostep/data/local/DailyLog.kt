package com.example.ecostep.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val date: String,
    val transportType: String,
    val transportDistanceKm: Double,
    val meatPortions: Int,
    val energyLevel: Int,          // 1–5
    val wasteLevel: Int,           // 1–5
    val steps: Int,
    val ecoScore: Double           // kg CO2
)

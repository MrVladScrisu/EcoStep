package com.example.ecostep.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    val userId: String = "", // ID-ul utilizatorului căruia îi aparține log-ul

    val date: String,                 // ex: 2025-11-28

    val transportType: String,        // walk, bike, bus, car, ev
    val transportDistanceKm: Double,

    val meatPortions: Int,
    val veggiesPortions: Int,
    val dairyPortions: Int,
    val junkFoodPortions: Int,

    val electricityKwh: Double,
    val waterLiters: Double,
    val wasteBags: Int,
    val recycledToday: Boolean,

    val steps: Int,

    val ecoScore: Double
)

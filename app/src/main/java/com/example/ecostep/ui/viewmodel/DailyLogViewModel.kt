package com.example.ecostep.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.data.repository.DailyLogRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

// asta este forma datelor care vin din UI (DailyLogScreen)
data class DailyLogFormData(
    val transportType: String,
    val transportDistanceKm: Double,

    val meatPortions: Int,
    val veggiesPortions: Int,
    val dairyPortions: Int,
    val junkFoodPortions: Int,

    val electricityKwh: Double,
    val waterLiters: Double,
    val wasteBags: Int,
    val recycledToday: Boolean,

    val steps: Int
)

class DailyLogViewModel(
    private val repository: DailyLogRepository
) : ViewModel() {
    
    private val _currentUserId = MutableStateFlow<String?>(null)
    
    fun setUserId(userId: String) {
        _currentUserId.value = userId
    }

    val allLogs: StateFlow<List<DailyLog>> = _currentUserId
        .flatMapLatest { userId ->
            if (userId != null) {
                repository.getDailyLogs(userId)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    suspend fun getTodayLog(): DailyLog? {
        val userId = _currentUserId.value ?: return null
        val today = LocalDate.now().toString()
        return repository.getDailyLogByDate(userId, today)
    }

    fun saveDailyLog(form: DailyLogFormData) {
        viewModelScope.launch {
            val userId = _currentUserId.value ?: return@launch
            val date = LocalDate.now().toString()
            val existingLog = repository.getDailyLogByDate(userId, date)
            val ecoScore = calculateEcoScore(form)

            val log = if (existingLog != null) {
                // Update existing log
                existingLog.copy(
                    transportType = form.transportType,
                    transportDistanceKm = form.transportDistanceKm,
                    meatPortions = form.meatPortions,
                    veggiesPortions = form.veggiesPortions,
                    dairyPortions = form.dairyPortions,
                    junkFoodPortions = form.junkFoodPortions,
                    electricityKwh = form.electricityKwh,
                    waterLiters = form.waterLiters,
                    wasteBags = form.wasteBags,
                    recycledToday = form.recycledToday,
                    steps = form.steps,
                    ecoScore = ecoScore
                )
            } else {
                // Create new log
                DailyLog(
                    userId = userId,
                    date = date,
                    transportType = form.transportType,
                    transportDistanceKm = form.transportDistanceKm,
                    meatPortions = form.meatPortions,
                    veggiesPortions = form.veggiesPortions,
                    dairyPortions = form.dairyPortions,
                    junkFoodPortions = form.junkFoodPortions,
                    electricityKwh = form.electricityKwh,
                    waterLiters = form.waterLiters,
                    wasteBags = form.wasteBags,
                    recycledToday = form.recycledToday,
                    steps = form.steps,
                    ecoScore = ecoScore
                )
            }

            if (existingLog != null) {
                repository.updateDailyLog(log)
            } else {
                repository.insertDailyLog(log)
            }
        }
    }

    fun addOrUpdateLog(log: DailyLog) {
        viewModelScope.launch {
            if (log.id == 0L) {
                repository.insertDailyLog(log)
            } else {
                repository.updateDailyLog(log)
            }
        }
    }

    fun deleteLog(log: DailyLog) {
        viewModelScope.launch {
            repository.deleteDailyLog(log)
        }
    }

    private fun calculateEcoScore(form: DailyLogFormData): Double {
        val transportFactor = when (form.transportType) {
            "walk" -> 0.0
            "bike" -> 0.0
            "bus"  -> 0.08
            "car"  -> 0.21
            "ev"   -> 0.05
            else   -> 0.15
        }
        val transportEmissions = transportFactor * form.transportDistanceKm

        val foodEmissions =
            form.meatPortions * 5.0 +
                    form.veggiesPortions * 0.5 +
                    form.dairyPortions * 1.5 +
                    form.junkFoodPortions * 3.0

        val energyEmissions =
            form.electricityKwh * 0.5 +
                    form.waterLiters * 0.0004

        val wasteEmissions =
            form.wasteBags * 0.7 - if (form.recycledToday) 0.5 else 0.0

        val stepsBonus = -(form.steps / 10000.0)

        val total = transportEmissions + foodEmissions + energyEmissions + wasteEmissions + stepsBonus
        return total.coerceAtLeast(0.0)
    }
}

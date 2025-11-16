package com.example.ecostep.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.data.repository.DailyLogRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DailyLogViewModel(
    private val repository: DailyLogRepository
) : ViewModel() {

    val allLogs: StateFlow<List<DailyLog>> =
        repository.getAllLogs()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun addOrUpdateLog(log: DailyLog) {
        viewModelScope.launch {
            repository.upsertLog(log)
        }
    }

    fun deleteLog(log: DailyLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }
}

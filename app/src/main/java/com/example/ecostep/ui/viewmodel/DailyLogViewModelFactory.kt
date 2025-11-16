package com.example.ecostep.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecostep.data.repository.DailyLogRepository

class DailyLogViewModelFactory(
    private val repository: DailyLogRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DailyLogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DailyLogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

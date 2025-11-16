package com.example.ecostep.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import com.example.ecostep.data.AppGraph
import com.example.ecostep.ui.screens.dashboard.DashboardScreen
import com.example.ecostep.ui.viewmodel.DailyLogViewModel
import com.example.ecostep.ui.viewmodel.DailyLogViewModelFactory

@Composable
fun EcoStepApp() {

    // ⚡ Obținem ViewModel-ul folosind factory-ul nostru
    val dailyLogViewModel: DailyLogViewModel = viewModel(
        factory = DailyLogViewModelFactory(AppGraph.dailyLogRepository)
    )

    // ⚡ Observăm logs-urile din DB
    val logs by dailyLogViewModel.allLogs.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        DashboardScreen(
            totalLogs = logs.size
        )
    }
}

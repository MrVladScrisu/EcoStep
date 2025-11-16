package com.example.ecostep.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ecostep.data.AppGraph
import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.ui.navigation.AppNavHost
import com.example.ecostep.ui.viewmodel.DailyLogViewModel
import com.example.ecostep.ui.viewmodel.DailyLogViewModelFactory

@Composable
fun EcoStepApp() {

    // 🔹 ViewModel-ul nostru, creat cu factory-ul care primește repository din AppGraph
    val dailyLogViewModel: DailyLogViewModel = viewModel(
        factory = DailyLogViewModelFactory(AppGraph.dailyLogRepository)
    )

    // 🔹 Observăm lista de log-uri din baza de date (state pentru Compose)
    val logs by dailyLogViewModel.allLogs.collectAsState()

    // 🔹 Controller-ul de navigație pentru Compose
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AppNavHost(
            navController = navController,
            totalLogs = logs.size,
            onSaveLog = { textEntered ->
                // AICI testăm CRUD-ul: introducem o înregistrare simplă în DB
                val log = DailyLog(
                    date = "2025-01-01",           // deocamdată hardcodat
                    transportType = "none",
                    transportDistanceKm = 0.0,
                    meatPortions = 0,
                    energyLevel = 1,
                    wasteLevel = 1,
                    steps = 0,
                    ecoScore = 0.0
                )
                dailyLogViewModel.addOrUpdateLog(log)
            }
        )
    }
}

package com.example.ecostep.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.ecostep.ui.navigation.EcoBottomBar
import com.example.ecostep.ui.screens.dailylog.DailyLogFormData
import com.example.ecostep.ui.viewmodel.DailyLogViewModel
import com.example.ecostep.ui.viewmodel.DailyLogViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EcoStepApp() {

    val dailyLogViewModel: DailyLogViewModel = viewModel(
        factory = DailyLogViewModelFactory(AppGraph.dailyLogRepository)
    )

    val logs by dailyLogViewModel.allLogs.collectAsState()
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = { EcoBottomBar(navController = navController) }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                logs = logs,
                onSaveLog = { formData ->
                    val ecoScore = computeEcoScore(formData)
                    val today = getTodayDateString()

                    val log = DailyLog(
                        id = 0L, // auto-generate
                        date = today,
                        transportType = formData.transportType,
                        transportDistanceKm = formData.transportDistanceKm,
                        meatPortions = formData.meatPortions,
                        energyLevel = formData.energyLevel,
                        wasteLevel = formData.wasteLevel,
                        steps = formData.steps,
                        ecoScore = ecoScore
                    )

                    dailyLogViewModel.addOrUpdateLog(log)
                },
                onDeleteLog = { log ->
                    dailyLogViewModel.deleteLog(log)
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

// calcul simplu ecoScore (kg CO₂ / zi)
private fun computeEcoScore(form: DailyLogFormData): Double {
    val transportFactor = when (form.transportType.lowercase(Locale.getDefault())) {
        "car", "masina", "mașină" -> 0.192
        "bus", "autobuz" -> 0.105
        "train", "tren" -> 0.041
        "bike", "bicicleta", "bicicletă" -> 0.0
        "walk", "mers" -> 0.0
        "ev", "electric", "masina electrica", "mașină electrică" -> 0.053
        else -> 0.15
    }

    val transportCO2 = form.transportDistanceKm * transportFactor
    val foodCO2 = form.meatPortions * 5.0
    val energyCO2 = when (form.energyLevel) {
        1 -> 0.5
        2 -> 1.0
        3 -> 2.0
        4 -> 3.0
        5 -> 5.0
        else -> 2.0
    }
    val wasteCO2 = when (form.wasteLevel) {
        1 -> 0.2
        2 -> 0.5
        3 -> 1.0
        4 -> 1.2
        5 -> 1.5
        else -> 1.0
    }

    val activityBonus = (form.steps / 2000) * 0.1 // -0.1 kg la fiecare 2000 pași

    return transportCO2 + foodCO2 + energyCO2 + wasteCO2 - activityBonus
}

private fun getTodayDateString(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

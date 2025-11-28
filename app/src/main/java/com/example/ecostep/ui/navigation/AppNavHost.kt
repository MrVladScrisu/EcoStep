package com.example.ecostep.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.ui.screens.dashboard.DashboardScreen
import com.example.ecostep.ui.screens.dailylog.DailyLogFormData
import com.example.ecostep.ui.screens.dailylog.DailyLogScreen
import com.example.ecostep.ui.screens.history.HistoryScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    logs: List<DailyLog>,
    onSaveLog: (DailyLogFormData) -> Unit,
    onDeleteLog: (DailyLog) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        composable("dashboard") {
            DashboardScreen(
                totalLogs = logs.size,
                onAddTodayClick = { navController.navigate("daily_log") }
            )
        }

        composable("daily_log") {
            DailyLogScreen(
                onSaveClick = { formData ->
                    onSaveLog(formData)
                    navController.popBackStack()
                }
            )
        }

        composable("history") {
            HistoryScreen(
                logs = logs,
                onDeleteLog = { log -> onDeleteLog(log) }
            )
        }
    }
}

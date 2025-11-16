package com.example.ecostep.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.ecostep.ui.screens.dashboard.DashboardScreen
import com.example.ecostep.ui.screens.dailylog.DailyLogScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    totalLogs: Int,
    onSaveLog: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                totalLogs = totalLogs,
                onAddTodayClick = { navController.navigate("daily_log") }
            )
        }

        composable("daily_log") {
            DailyLogScreen(
                onSaveClick = { text ->
                    onSaveLog(text)
                    navController.popBackStack()
                }
            )
        }
    }
}

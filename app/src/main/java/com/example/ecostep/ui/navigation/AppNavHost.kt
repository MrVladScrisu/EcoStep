package com.example.ecostep.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.ui.screens.dashboard.DashboardScreen
import com.example.ecostep.ui.screens.dailylog.DailyLogScreen
import com.example.ecostep.ui.screens.history.HistoryScreen
import com.example.ecostep.ui.viewmodel.DailyLogFormData

@Composable
fun AppNavHost(
    navController: NavHostController,
    logs: List<DailyLog>,
    onSaveLog: (DailyLogFormData) -> Unit,
    onDeleteLog: (DailyLog) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = modifier
    ) {
        composable("dashboard") {
            DashboardScreen(
                logs = logs,
                onAddTodayClick = { navController.navigate("daily_log") },
                onLogoutClick = onLogout
            )
        }

        composable("daily_log") {
            val todayLog = logs.firstOrNull { it.date == java.time.LocalDate.now().toString() }
            DailyLogScreen(
                existingLog = todayLog,
                onSaveClick = { formData ->
                    onSaveLog(formData)
                    navController.popBackStack()
                }
            )
        }

        composable("history") {
            HistoryScreen(
                logs = logs,
                onDeleteLog = { log -> onDeleteLog(log) },
                onEditLog = { log ->
                    navController.navigate("daily_log_edit/${log.id}")
                }
            )
        }

        composable("daily_log_edit/{logId}") { backStackEntry ->
            val logId = backStackEntry.arguments?.getString("logId")?.toLongOrNull()
            val logToEdit = logs.firstOrNull { it.id == logId }
            DailyLogScreen(
                existingLog = logToEdit,
                onSaveClick = { formData ->
                    onSaveLog(formData)
                    navController.popBackStack()
                }
            )
        }
    }
}

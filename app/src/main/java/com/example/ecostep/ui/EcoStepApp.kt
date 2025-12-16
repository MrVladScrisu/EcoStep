package com.example.ecostep.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.ecostep.data.AppGraph
import com.example.ecostep.ui.navigation.AppNavHost
import com.example.ecostep.ui.navigation.EcoBottomBar
import com.example.ecostep.ui.screens.auth.BiometricLoginScreen
import com.example.ecostep.ui.screens.auth.EmailLoginScreen
import com.example.ecostep.ui.viewmodel.DailyLogViewModel
import com.example.ecostep.ui.viewmodel.DailyLogViewModelFactory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun EcoStepApp() {
    val auth = remember { FirebaseAuth.getInstance() }
    var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
    var showEmailLogin by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!isLoggedIn) {
            // Ecran de login
            if (showEmailLogin) {
                EmailLoginScreen(
                    onLoginSuccess = { 
                        isLoggedIn = true 
                    },
                    onBackToBiometric = {
                        showEmailLogin = false
                    }
                )
            } else {
                BiometricLoginScreen(
                    onLoginSuccess = { 
                        isLoggedIn = true 
                    },
                    onSwitchToEmail = {
                        showEmailLogin = true
                    }
                )
            }
        } else {
            // Aplicația principală
            MainApp(
                onLogout = {
                    auth.signOut()
                    isLoggedIn = false
                }
            )
        }
    }
}

@Composable
private fun MainApp(onLogout: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val dailyLogViewModel: DailyLogViewModel = viewModel(
        factory = DailyLogViewModelFactory(AppGraph.dailyLogRepository)
    )
    
    // Setează userId-ul curent în ViewModel
    LaunchedEffect(auth.currentUser?.uid) {
        auth.currentUser?.uid?.let { userId ->
            dailyLogViewModel.setUserId(userId)
        }
    }

    val logs by dailyLogViewModel.allLogs.collectAsState(initial = emptyList())
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { EcoBottomBar(navController = navController) }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            logs = logs,
            onSaveLog = { formData ->
                dailyLogViewModel.saveDailyLog(formData)
            },
            onDeleteLog = { log ->
                dailyLogViewModel.deleteLog(log)
            },
            onLogout = onLogout,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

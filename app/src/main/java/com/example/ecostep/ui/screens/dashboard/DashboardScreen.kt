package com.example.ecostep.ui.screens.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    totalLogs: Int,
    onAddTodayClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "EcoStep",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier
                .padding(top = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Today's eco score:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "0.0 kg CO₂",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        // 🔹 aici afișăm numărul total de zile salvate în DB
        Text(
            text = "Total days logged: $totalLogs",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            onClick = onAddTodayClick,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(text = "Fill today's log")
        }
    }
}

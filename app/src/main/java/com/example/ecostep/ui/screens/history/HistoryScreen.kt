package com.example.ecostep.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ecostep.data.local.DailyLog

@Composable
fun HistoryScreen(
    logs: List<DailyLog>,
    onDeleteLog: (DailyLog) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Istoric zile",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (logs.isEmpty()) {
            Text("Nu există încă înregistrări.", style = MaterialTheme.typography.bodyMedium)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "Data: ${log.date}")
                            Text(text = "Eco score: ${"%.2f".format(log.ecoScore)} kg CO₂")
                            Text(text = "Transport: ${log.transportType} (${log.transportDistanceKm} km)")
                            Text(text = "Carne: ${log.meatPortions} porții")
                            Text(text = "Pași: ${log.steps}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(onClick = { onDeleteLog(log) }) {
                                Text("Șterge")
                            }
                        }
                    }
                }
            }
        }
    }
}

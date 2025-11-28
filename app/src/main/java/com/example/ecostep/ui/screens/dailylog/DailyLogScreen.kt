package com.example.ecostep.ui.screens.dailylog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// Datele pe care le completăm în formular
data class DailyLogFormData(
    val transportType: String,
    val transportDistanceKm: Double,
    val meatPortions: Int,
    val energyLevel: Int,
    val wasteLevel: Int,
    val steps: Int
)

@Composable
fun DailyLogScreen(
    onSaveClick: (DailyLogFormData) -> Unit
) {
    var transportType by remember { mutableStateOf("") }
    var transportDistanceText by remember { mutableStateOf("") }
    var meatPortionsText by remember { mutableStateOf("") }
    var energyLevelText by remember { mutableStateOf("") }
    var wasteLevelText by remember { mutableStateOf("") }
    var stepsText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Completează log-ul zilnic",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = transportType,
            onValueChange = { transportType = it },
            label = { Text("Tip transport (ex: car, bus, walk)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = transportDistanceText,
            onValueChange = { transportDistanceText = it },
            label = { Text("Distanță transport (km)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = meatPortionsText,
            onValueChange = { meatPortionsText = it },
            label = { Text("Porții de carne (0–5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = energyLevelText,
            onValueChange = { energyLevelText = it },
            label = { Text("Nivel consum energie (1–5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = wasteLevelText,
            onValueChange = { wasteLevelText = it },
            label = { Text("Nivel deșeuri (1–5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = stepsText,
            onValueChange = { stepsText = it },
            label = { Text("Număr pași (temporar manual)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val distance = transportDistanceText.toDoubleOrNull() ?: 0.0
                val meat = meatPortionsText.toIntOrNull() ?: 0
                val energy = (energyLevelText.toIntOrNull() ?: 1).coerceIn(1, 5)
                val waste = (wasteLevelText.toIntOrNull() ?: 1).coerceIn(1, 5)
                val steps = stepsText.toIntOrNull() ?: 0

                val formData = DailyLogFormData(
                    transportType = transportType.ifBlank { "unknown" },
                    transportDistanceKm = distance,
                    meatPortions = meat,
                    energyLevel = energy,
                    wasteLevel = waste,
                    steps = steps
                )
                onSaveClick(formData)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvează")
        }
    }
}

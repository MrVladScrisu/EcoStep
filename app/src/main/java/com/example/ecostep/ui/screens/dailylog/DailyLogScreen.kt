package com.example.ecostep.ui.screens.dailylog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DailyLogScreen(
    onSaveClick: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Add Daily Log (Test CRUD)",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Enter any text") },
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(
            onClick = { onSaveClick(text) },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Save")
        }
    }
}

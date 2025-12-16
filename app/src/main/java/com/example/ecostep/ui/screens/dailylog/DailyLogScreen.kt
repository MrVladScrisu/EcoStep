package com.example.ecostep.ui.screens.dailylog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ecostep.ai.FoodDetectionService
import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.ui.viewmodel.DailyLogFormData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

private enum class TransportOption(val label: String, val value: String) {
    WALK("Walk", "walk"),
    BIKE("Bike", "bike"),
    BUS("Bus", "bus"),
    CAR("Car", "car"),
    EV("EV", "ev")
}

@Composable
fun DailyLogScreen(
    existingLog: DailyLog? = null,
    onSaveClick: (DailyLogFormData) -> Unit
) {
    // --------- state ---------
    var selectedTransport by remember { 
        mutableStateOf(
            existingLog?.let { 
                TransportOption.values().find { opt -> opt.value == it.transportType } 
                    ?: TransportOption.WALK
            } ?: TransportOption.WALK
        )
    }
    var distanceKm by remember { 
        mutableStateOf(existingLog?.transportDistanceKm?.toFloat() ?: 0f) 
    }

    var meatPortions by remember { 
        mutableStateOf(existingLog?.meatPortions?.toFloat() ?: 0f) 
    }
    var veggiesPortions by remember { 
        mutableStateOf(existingLog?.veggiesPortions?.toFloat() ?: 0f) 
    }
    var dairyPortions by remember { 
        mutableStateOf(existingLog?.dairyPortions?.toFloat() ?: 0f) 
    }
    var junkFoodPortions by remember { 
        mutableStateOf(existingLog?.junkFoodPortions?.toFloat() ?: 0f) 
    }

    var electricityKwhText by remember { 
        mutableStateOf(existingLog?.electricityKwh?.toString() ?: "") 
    }
    var waterLitersText by remember { 
        mutableStateOf(existingLog?.waterLiters?.toString() ?: "") 
    }
    var wasteBagsText by remember { 
        mutableStateOf(existingLog?.wasteBags.toString()) 
    }
    var recycledToday by remember { 
        mutableStateOf(existingLog?.recycledToday ?: false) 
    }

    var stepsText by remember { 
        mutableStateOf(existingLog?.steps?.toString() ?: "") 
    }

    var transportPhoto by remember { mutableStateOf<Uri?>(null) }
    var foodPhoto by remember { mutableStateOf<Uri?>(null) }

    val scrollState = rememberScrollState()
    
    // Context și scope pentru AI
    val context = LocalContext.current
    
    // Tracking pași automat
    val stepCounterService = remember { 
        com.example.ecostep.sensors.StepCounterService(context) 
    }
    val trackedSteps by stepCounterService.steps.collectAsState()
    val trackedDistance by stepCounterService.distanceKm.collectAsState()
    
    // Pornește tracking-ul
    LaunchedEffect(Unit) {
        if (stepCounterService.hasStepPermission()) {
            stepCounterService.startTracking()
        }
    }
    
    // Oprește tracking-ul când ieșim din ecran
    DisposableEffect(Unit) {
        onDispose {
            stepCounterService.stopTracking()
        }
    }
    val scope = rememberCoroutineScope()
    val foodDetectionService = remember { FoodDetectionService() }

    var isProcessingTransportImage by remember { mutableStateOf(false) }
    var transportDetectionResult by remember { mutableStateOf<String?>(null) }
    
    val transportImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        transportPhoto = uri
        if (uri != null) {
            isProcessingTransportImage = true
            transportDetectionResult = null
            
            scope.launch {
                try {
                    // Convertim URI în Bitmap
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    
                    if (bitmap != null) {
                        // Detectăm vehiculul cu AI
                        val result = withContext(Dispatchers.IO) {
                            foodDetectionService.detectVehicle(bitmap)
                        }
                        
                        // Actualizăm tipul de transport
                        selectedTransport = when (result.vehicleType) {
                            "car" -> TransportOption.CAR
                            "bike" -> TransportOption.BIKE
                            "bus" -> TransportOption.BUS
                            "ev" -> TransportOption.EV
                            else -> TransportOption.WALK
                        }
                        
                        // Afișăm rezultatul
                        transportDetectionResult = buildString {
                            append("Detectat: ${selectedTransport.label}")
                            append("\nÎncredere: ${(result.confidence * 100).toInt()}%")
                            if (result.detectedItems.isNotEmpty()) {
                                append("\nObiecte: ${result.detectedItems.take(3).joinToString(", ")}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    transportDetectionResult = "Eroare la procesare: ${e.message}"
                } finally {
                    isProcessingTransportImage = false
                }
            }
        }
    }

    var isProcessingFoodImage by remember { mutableStateOf(false) }
    var foodDetectionResult by remember { mutableStateOf<String?>(null) }

    val foodImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        foodPhoto = uri
        if (uri != null) {
            isProcessingFoodImage = true
            foodDetectionResult = null
            
            scope.launch {
                try {
                    // Convertim URI în Bitmap
                    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    
                    if (bitmap != null) {
                        // Detectăm mâncarea cu AI
                        val result = withContext(Dispatchers.IO) {
                            foodDetectionService.detectFood(bitmap)
                        }
                        
                        // Convertim rezultatul în porții
                        val portions = foodDetectionService.convertToPortions(result)
                        
                        // Actualizăm formularul cu rezultatele
                        meatPortions = portions["meatPortions"]?.toFloat() ?: 0f
                        veggiesPortions = portions["veggiesPortions"]?.toFloat() ?: 0f
                        dairyPortions = portions["dairyPortions"]?.toFloat() ?: 0f
                        junkFoodPortions = portions["junkFoodPortions"]?.toFloat() ?: 0f
                        
                        // Afișăm rezultatul
                        foodDetectionResult = buildString {
                            append("Detectat: ")
                            result.categories.forEach { (category, portions) ->
                                if (portions > 0) {
                                    append("${category.label}: ${String.format("%.1f", portions)} porții, ")
                                }
                            }
                            append("\nÎncredere: ${(result.confidence * 100).toInt()}%")
                            if (result.detectedItems.isNotEmpty()) {
                                append("\nObiecte: ${result.detectedItems.take(3).joinToString(", ")}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    foodDetectionResult = "Eroare la procesare: ${e.message}"
                } finally {
                    isProcessingFoodImage = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Text(
            text = if (existingLog != null) "Editează log-ul zilnic" else "Completează log-ul zilnic",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Înregistrează-ți activitățile pentru a calcula impactul ecologic",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // ------- TRANSPORT -------
        SectionCard(
            title = "Transport",
            icon = Icons.Default.Star
        ) {
            Text(
                text = "Tip transport",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransportOption.values().forEach { option ->
                    FilterChip(
                        selected = selectedTransport == option,
                        onClick = { selectedTransport = option },
                        label = { Text(option.label) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Distanță (km): ${"%.1f".format(distanceKm)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = distanceKm,
                onValueChange = { distanceKm = it },
                valueRange = 0f..100f,
                steps = 99
            )

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { transportImagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessingTransportImage
            ) {
                if (isProcessingTransportImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    when {
                        isProcessingTransportImage -> "Procesare AI..."
                        transportPhoto == null -> "Încarcă poză transport (AI detect)"
                        else -> "Schimbă poza transportului"
                    }
                )
            }
            
            // Afișăm rezultatul AI
            if (transportDetectionResult != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "🤖 AI Detection:\n$transportDetectionResult",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ------- ALIMENTAȚIE -------
        SectionCard(
            title = "Alimentație",
            icon = Icons.Default.Star
        ) {
            Text(
                text = "Porții carne: ${meatPortions.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = meatPortions,
                onValueChange = { meatPortions = it },
                valueRange = 0f..5f,
                steps = 4
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Porții legume & fructe: ${veggiesPortions.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = veggiesPortions,
                onValueChange = { veggiesPortions = it },
                valueRange = 0f..10f,
                steps = 9
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Porții lactate: ${dairyPortions.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = dairyPortions,
                onValueChange = { dairyPortions = it },
                valueRange = 0f..5f,
                steps = 4
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Porții fast-food / procesate: ${junkFoodPortions.toInt()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = junkFoodPortions,
                onValueChange = { junkFoodPortions = it },
                valueRange = 0f..5f,
                steps = 4
            )

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { foodImagePicker.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessingFoodImage
            ) {
                if (isProcessingFoodImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Procesare AI...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (foodPhoto == null)
                            "Încarcă poză cu mâncarea (AI detect)"
                        else
                            "Schimbă poza cu mâncarea"
                    )
                }
            }
            
            if (foodDetectionResult != null) {
                Spacer(Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "🤖 Rezultat AI:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = foodDetectionResult ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else if (foodPhoto != null && !isProcessingFoodImage) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "✅ Poză încărcată - porțiile au fost actualizate automat",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ------- ENERGIE & DEȘEURI -------
        SectionCard(
            title = "Acasă: energie & deșeuri",
            icon = Icons.Default.Home
        ) {
            OutlinedTextField(
                value = electricityKwhText,
                onValueChange = { newVal ->
                    if (newVal.isEmpty() || (newVal.all { it.isDigit() || it == '.' } && newVal.toDoubleOrNull() != null)) {
                        electricityKwhText = newVal
                    }
                },
                label = { Text("Consum electricitate (kWh)") },
                placeholder = { Text("ex: 15.5") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = null)
                },
                supportingText = {
                    Text("Introdu consumul zilnic de electricitate în kWh")
                }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = waterLitersText,
                onValueChange = { newVal ->
                    if (newVal.isEmpty() || (newVal.all { it.isDigit() || it == '.' } && newVal.toDoubleOrNull() != null)) {
                        waterLitersText = newVal
                    }
                },
                label = { Text("Consum apă (litri)") },
                placeholder = { Text("ex: 120.0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = null)
                },
                supportingText = {
                    Text("Introdu consumul zilnic de apă în litri")
                }
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = wasteBagsText,
                onValueChange = { newVal ->
                    if (newVal.isEmpty() || (newVal.all { it.isDigit() } && (newVal.toIntOrNull() ?: 0) <= 5)) {
                        wasteBagsText = newVal
                    }
                },
                label = { Text("Număr saci de gunoi azi (0–5)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = recycledToday,
                    onCheckedChange = { recycledToday = it }
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Am reciclat azi",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ------- MIȘCARE -------
        SectionCard(
            title = "Mișcare",
            icon = Icons.Default.Star
        ) {
            OutlinedTextField(
                value = stepsText,
                onValueChange = { newVal ->
                    if (newVal.all { it.isDigit() }) {
                        stepsText = newVal
                    }
                },
                label = { Text("Număr pași") },
                placeholder = { Text("ex: 8500") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Star, contentDescription = null)
                },
                supportingText = {
                    Text("Introdu numărul de pași făcuți astăzi")
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val form = DailyLogFormData(
                    transportType = selectedTransport.value,
                    transportDistanceKm = distanceKm.toDouble(),
                    meatPortions = meatPortions.toInt(),
                    veggiesPortions = veggiesPortions.toInt(),
                    dairyPortions = dairyPortions.toInt(),
                    junkFoodPortions = junkFoodPortions.toInt(),
                    electricityKwh = electricityKwhText.toDoubleOrNull() ?: 0.0,
                    waterLiters = waterLitersText.toDoubleOrNull() ?: 0.0,
                    wasteBags = wasteBagsText.toIntOrNull() ?: 0,
                    recycledToday = recycledToday,
                    steps = stepsText.toIntOrNull() ?: 0
                )
                onSaveClick(form)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (existingLog != null) "Actualizează log-ul" else "Salvează log-ul",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            content()
        }
    }
}

package com.example.ecostep.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.ui.components.CategoryBarChart
import com.example.ecostep.ui.components.EcoScoreLineChart
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    logs: List<DailyLog>,
    onDeleteLog: (DailyLog) -> Unit,
    onEditLog: (DailyLog) -> Unit = {}
) {
    val totalDays = logs.size
    val totalEco = logs.sumOf { it.ecoScore }
    val avgEco = if (totalDays > 0) totalEco / totalDays else 0.0
    val bestDay = logs.minByOrNull { it.ecoScore }
    val worstDay = logs.maxByOrNull { it.ecoScore }

    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Text(
            text = "Istoric & Statistici",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Vezi progresul tău ecologic",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Zile",
                value = totalDays.toString(),
                icon = Icons.Default.Info,
                modifier = Modifier.weight(1f)
            )
            StatisticCard(
                title = "Medie",
                value = String.format("%.1f", avgEco),
                icon = Icons.Default.Star,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Best/Worst Days
        if (bestDay != null && worstDay != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BestWorstCard(
                    title = "Cea mai bună",
                    date = bestDay.date,
                    score = bestDay.ecoScore,
                    isBest = true,
                    modifier = Modifier.weight(1f)
                )
                BestWorstCard(
                    title = "Cea mai proastă",
                    date = worstDay.date,
                    score = worstDay.ecoScore,
                    isBest = false,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Grafice
        EcoScoreLineChart(
            logs = logs,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CategoryBarChart(
            logs = logs,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total Impact
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Impact total",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Toate zilele înregistrate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = String.format("%.1f", totalEco),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logs List
        Text(
            text = "Înregistrări",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nu există încă înregistrări",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                logs.forEach { log ->
                    LogItemCard(
                        log = log,
                        onDelete = { onDeleteLog(log) },
                        onEditLog = { onEditLog(log) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun BestWorstCard(
    title: String,
    date: String,
    score: Double,
    isBest: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBest) 
                MaterialTheme.colorScheme.secondaryContainer 
            else 
                MaterialTheme.colorScheme.tertiaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isBest) 
                    MaterialTheme.colorScheme.onSecondaryContainer 
                else 
                    MaterialTheme.colorScheme.onTertiaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatDate(date),
                style = MaterialTheme.typography.bodySmall,
                color = if (isBest) 
                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f) 
                else 
                    MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = String.format("%.2f", score),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isBest) 
                    MaterialTheme.colorScheme.secondary 
                else 
                    MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "kg CO₂",
                style = MaterialTheme.typography.bodySmall,
                color = if (isBest) 
                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f) 
                else 
                    MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun LogItemCard(
    log: DailyLog,
    onDelete: () -> Unit,
    onEditLog: (DailyLog) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatDate(log.date),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%.2f kg CO₂", log.ecoScore),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        FilledTonalIconButton(
                            onClick = { onEditLog(log) },
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editează",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        FilledTonalIconButton(
                            onClick = onDelete,
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Șterge",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Detalii log
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailChip(
                        icon = Icons.Default.Star,
                        label = "Transport",
                        value = "${getTransportLabel(log.transportType)} • ${String.format("%.1f", log.transportDistanceKm)} km",
                        modifier = Modifier.weight(1f)
                    )
                    DetailChip(
                        icon = Icons.Default.Star,
                        label = "Pași",
                        value = "${log.steps}",
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DetailChip(
                        icon = Icons.Default.Star,
                        label = "Alimentație",
                        value = "${log.meatPortions + log.veggiesPortions + log.dairyPortions + log.junkFoodPortions} porții",
                        modifier = Modifier.weight(1f)
                    )
                    DetailChip(
                        icon = if (log.recycledToday) Icons.Default.CheckCircle else Icons.Default.Close,
                        label = "Reciclare",
                        value = if (log.recycledToday) "Da" else "Nu",
                        color = if (log.recycledToday) 
                            MaterialTheme.colorScheme.tertiary 
                        else 
                            MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = modifier.padding(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = color
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

private fun getTransportLabel(type: String): String {
    return when (type) {
        "walk" -> "Mers"
        "bike" -> "Bicicletă"
        "bus" -> "Autobuz"
        "car" -> "Mașină"
        "ev" -> "Mașină electrică"
        else -> type
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", java.util.Locale.forLanguageTag("ro-RO"))
        date.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}

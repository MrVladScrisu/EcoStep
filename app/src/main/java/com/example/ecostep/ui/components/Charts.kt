package com.example.ecostep.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecostep.data.local.DailyLog
import kotlin.math.max

@Composable
fun EcoScoreLineChart(
    logs: List<DailyLog>,
    modifier: Modifier = Modifier
) {
    if (logs.isEmpty()) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nu există date pentru grafic",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        return
    }

    val animatedProgress by rememberInfiniteTransition(
        label = "chart_animation"
    ).animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )

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
                .padding(16.dp)
        ) {
            Text(
                text = "Evoluție Eco Score",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                LineChart(
                    data = logs.takeLast(14).map { it.ecoScore }, // Ultimele 14 zile
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    animatedProgress = animatedProgress
                )
            }
        }
    }
}

@Composable
fun CategoryBarChart(
    logs: List<DailyLog>,
    modifier: Modifier = Modifier
) {
    if (logs.isEmpty()) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nu există date pentru grafic",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        return
    }

    val recentLogs = logs.takeLast(7) // Ultimele 7 zile
    
    val transportAvg = recentLogs.map { it.transportDistanceKm }.average()
    val foodAvg = recentLogs.map { 
        (it.meatPortions * 5.0 + it.veggiesPortions * 0.5 + it.dairyPortions * 1.5 + it.junkFoodPortions * 3.0) 
    }.average()
    val energyAvg = recentLogs.map { 
        (it.electricityKwh * 0.5 + it.waterLiters * 0.0004) 
    }.average()
    val wasteAvg = recentLogs.map { 
        (it.wasteBags * 0.7 - if (it.recycledToday) 0.5 else 0.0) 
    }.average()

    val maxValue = maxOf(transportAvg, foodAvg, energyAvg, wasteAvg, 1.0)
    
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
                .padding(16.dp)
        ) {
            Text(
                text = "Impact pe Categorii (ultimele 7 zile)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            BarChartItem(
                label = "Transport",
                value = transportAvg,
                maxValue = maxValue,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            BarChartItem(
                label = "Alimentație",
                value = foodAvg,
                maxValue = maxValue,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            BarChartItem(
                label = "Energie",
                value = energyAvg,
                maxValue = maxValue,
                color = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.height(12.dp))
            BarChartItem(
                label = "Deșeuri",
                value = wasteAvg,
                maxValue = maxValue,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun BarChartItem(
    label: String,
    value: Double,
    maxValue: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (maxValue > 0) (value / maxValue).toFloat().coerceIn(0f, 1f) else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bar_animation"
    )
    
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = String.format("%.2f", value),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .background(
                        color = color,
                        shape = RoundedCornerShape(12.dp)
                    )
            )
        }
    }
}

@Composable
private fun LineChart(
    data: List<Double>,
    modifier: Modifier = Modifier,
    color: Color,
    animatedProgress: Float
) {
    if (data.isEmpty()) return
    
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)

    Canvas(modifier = modifier) {
        val padding = 40f
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        val maxValue = data.maxOrNull() ?: 1.0
        val minValue = data.minOrNull() ?: 0.0
        val range = maxValue - minValue
        val normalizedRange = if (range > 0) range else 1.0

        val points = data.mapIndexed { index, value ->
            val x = padding + (index.toFloat() / (data.size - 1).coerceAtLeast(1)) * chartWidth
            val normalizedValue = ((value - minValue) / normalizedRange).toFloat().coerceIn(0f, 1f)
            val y = padding + chartHeight - (normalizedValue * chartHeight)
            Offset(x, y)
        }

        // Draw grid lines
        for (i in 0..4) {
            val y = padding + (chartHeight / 4) * i
            drawLine(
                color = gridLineColor,
                start = Offset(padding, y),
                end = Offset(size.width - padding, y),
                strokeWidth = 1f
            )
        }

        // Draw line
        if (points.size > 1) {
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }

            val animatedPath = Path().apply {
                moveTo(points[0].x, points[0].y)
                val stopIndex = (points.size * animatedProgress).toInt().coerceAtMost(points.size - 1)
                for (i in 1..stopIndex) {
                    lineTo(points[i].x, points[i].y)
                }
            }

            drawPath(
                path = animatedPath,
                color = color,
                style = Stroke(width = 3f)
            )

            // Draw points
            points.forEachIndexed { index, point ->
                if (index <= (points.size * animatedProgress).toInt()) {
                    drawCircle(
                        color = color,
                        radius = 5f,
                        center = point
                    )
                }
            }
        }
    }
}


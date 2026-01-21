package com.example.ecostep.ui.screens.goals

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecostep.data.local.DailyLog

data class Goal(
    val id: Int,
    val title: String,
    val description: String,
    val targetValue: Int,
    val currentValue: Int,
    val unit: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isCompleted: Boolean = false
)

data class Challenge(
    val id: Int,
    val title: String,
    val description: String,
    val reward: String,
    val daysRemaining: Int,
    val progress: Float, // 0.0 to 1.0
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun GoalsScreen(
    logs: List<DailyLog>,
    onBackClick: () -> Unit = {}
) {
    // Calculate statistics from logs
    val totalSteps = logs.sumOf { it.steps }
    val daysWithPublicTransport = logs.count { it.transportType == "bus" }
    val daysLogged = logs.size
    val co2Reduction = if (logs.isNotEmpty()) {
        val avgCO2 = logs.sumOf { it.ecoScore } / logs.size
        ((50.0 - avgCO2) / 50.0 * 100).coerceIn(0.0, 100.0).toInt()
    } else 0
    
    // Goals
    val goals = remember(totalSteps, daysWithPublicTransport, daysLogged) {
        listOf(
            Goal(
                id = 1,
                title = "10,000 pași",
                description = "Mergi pe jos 10,000 pași astăzi",
                targetValue = 10000,
                currentValue = totalSteps.coerceAtMost(10000),
                unit = "pași",
                icon = Icons.Default.Favorite,
                isCompleted = totalSteps >= 10000
            ),
            Goal(
                id = 2,
                title = "Transport public",
                description = "Folosește transport public 5 zile",
                targetValue = 5,
                currentValue = daysWithPublicTransport.coerceAtMost(5),
                unit = "zile",
                icon = Icons.Default.Call,
                isCompleted = daysWithPublicTransport >= 5
            ),
            Goal(
                id = 3,
                title = "Reducere CO₂",
                description = "Reduc CO₂ cu 20% luna aceasta",
                targetValue = 20,
                currentValue = co2Reduction.coerceAtMost(20),
                unit = "%",
                icon = Icons.Default.Face,
                isCompleted = co2Reduction >= 20
            ),
            Goal(
                id = 4,
                title = "Streak de 7 zile",
                description = "Înregistrează activitatea 7 zile consecutiv",
                targetValue = 7,
                currentValue = daysLogged.coerceAtMost(7),
                unit = "zile",
                icon = Icons.Default.Notifications,
                isCompleted = daysLogged >= 7
            )
        )
    }
    
    // All available challenges
    val allChallenges = remember {
        listOf(
            Challenge(1, "Zero Plastic", "Nu folosi plastic de unică folosință o săptămână", "Badge Eco Warrior", 5, 0.3f, Icons.Default.Warning),
            Challenge(2, "Biciclist Urban", "Mergi cu bicicleta 50 km luna aceasta", "Badge Bike Master", 12, 0.6f, Icons.Default.Place),
            Challenge(3, "Reciclare Campion", "Reciclează minim 5 kg de deșeuri", "Badge Recycling Hero", 8, 0.8f, Icons.Default.Refresh),
            Challenge(4, "Transport Eco", "Folosește doar transport ecologic 10 zile", "Badge Green Traveler", 15, 0.4f, Icons.Default.Call),
            Challenge(5, "Economie Energie", "Reduce consumul electric cu 30%", "Badge Energy Saver", 20, 0.5f, Icons.Default.Face),
            Challenge(6, "Zero Deșeuri", "O zi fără deșeuri generate", "Badge Zero Waste", 3, 0.7f, Icons.Default.Delete),
            Challenge(7, "Plantare Copaci", "Plantează sau adopta 3 copaci", "Badge Tree Planter", 25, 0.2f, Icons.Default.Favorite),
            Challenge(8, "Apă Economisită", "Reduce consumul de apă cu 20%", "Badge Water Guardian", 10, 0.6f, Icons.Default.Warning)
        )
    }
    
    // Random 3 challenges for this week
    val challenges = remember {
        val seed = System.currentTimeMillis() / (7 * 24 * 60 * 60 * 1000) // Changes weekly
        allChallenges.shuffled(java.util.Random(seed)).take(3)
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Înapoi",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Obiective & Provocări",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Summary Card
        item {
            SummaryCard(
                completedGoals = goals.count { it.isCompleted },
                totalGoals = goals.size,
                activeChallenges = challenges.size
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // Goals Section
        item {
            Text(
                text = "Obiectivele tale",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        items(goals) { goal ->
            GoalCard(goal = goal)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // Challenges Section
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Provocări active",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        items(challenges) { challenge ->
            ChallengeCard(challenge = challenge)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SummaryCard(
    completedGoals: Int,
    totalGoals: Int,
    activeChallenges: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                value = "$completedGoals/$totalGoals",
                label = "Obiective",
                icon = Icons.Default.CheckCircle
            )
            Divider(
                modifier = Modifier
                    .height(60.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )
            SummaryItem(
                value = activeChallenges.toString(),
                label = "Provocări",
                icon = Icons.Default.Star
            )
        }
    }
}

@Composable
private fun SummaryItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
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
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun GoalCard(goal: Goal) {
    val progress = goal.currentValue.toFloat() / goal.targetValue.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "progress_animation"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted) 
                MaterialTheme.colorScheme.secondaryContainer 
            else 
                MaterialTheme.colorScheme.surface
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (goal.isCompleted)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.primaryContainer
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = goal.icon,
                        contentDescription = null,
                        tint = if (goal.isCompleted)
                            MaterialTheme.colorScheme.onSecondary
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = goal.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                if (goal.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completat",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${goal.currentValue} / ${goal.targetValue} ${goal.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (goal.isCompleted)
                        MaterialTheme.colorScheme.secondary
                    else
                        MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ChallengeCard(challenge: Challenge) {
    val animatedProgress by animateFloatAsState(
        targetValue = challenge.progress,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic),
        label = "challenge_progress"
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
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
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = challenge.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = challenge.reward,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${challenge.daysRemaining} zile rămase",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${(challenge.progress * 100).toInt()}% completat",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}


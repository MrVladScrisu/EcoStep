package com.example.ecostep.ui.screens.settings

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecostep.data.local.DailyLog
import com.example.ecostep.notifications.NotificationHelper
import com.example.ecostep.notifications.TestNotificationHelper
import com.example.ecostep.util.ExportHelper
import com.example.ecostep.util.PreferencesManager

@Composable
fun SettingsScreen(
    logs: List<DailyLog> = emptyList(),
    onBackClick: () -> Unit = {},
    onThemeChange: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    
    var notificationsEnabled by remember { mutableStateOf(PreferencesManager.areNotificationsEnabled(context)) }
    var dailyReminder by remember { mutableStateOf(PreferencesManager.isDailyReminderEnabled(context)) }
    var weeklyReport by remember { mutableStateOf(PreferencesManager.isWeeklyReportEnabled(context)) }
    var darkModeEnabled by remember { mutableStateOf(PreferencesManager.isDarkMode(context)) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            notificationsEnabled = true
            PreferencesManager.setNotificationsEnabled(context, true)
            if (dailyReminder) {
                NotificationHelper.scheduleDailyReminder(context)
            }
            Toast.makeText(context, "Notificări activate", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permisiune notificări refuzată", Toast.LENGTH_SHORT).show()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        // Header
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
                text = "Setări",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Notifications Section
        SectionTitle(text = "Notificări")
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingsSwitchItem(
            icon = Icons.Default.Notifications,
            title = "Activează notificările",
            description = "Primește notificări despre activitate",
            checked = notificationsEnabled,
            onCheckedChange = { enabled ->
                if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    notificationsEnabled = enabled
                    PreferencesManager.setNotificationsEnabled(context, enabled)
                    if (!enabled) {
                        NotificationHelper.cancelDailyReminder(context)
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        SettingsSwitchItem(
            icon = Icons.Default.DateRange,
            title = "Reminder zilnic",
            description = "Reamintește-mi să completez log-ul (20:00)",
            checked = dailyReminder,
            onCheckedChange = { enabled ->
                dailyReminder = enabled
                PreferencesManager.setDailyReminderEnabled(context, enabled)
                if (enabled && notificationsEnabled) {
                    NotificationHelper.scheduleDailyReminder(context)
                    Toast.makeText(context, "Reminder activat (20:00)", Toast.LENGTH_SHORT).show()
                } else {
                    NotificationHelper.cancelDailyReminder(context)
                }
            },
            enabled = notificationsEnabled
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        SettingsSwitchItem(
            icon = Icons.Default.Star,
            title = "Raport săptămânal",
            description = "Primește rezumatul săptămânii",
            checked = weeklyReport,
            onCheckedChange = { enabled ->
                weeklyReport = enabled
                PreferencesManager.setWeeklyReportEnabled(context, enabled)
            },
            enabled = notificationsEnabled
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Appearance Section
        SectionTitle(text = "Aspect")
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingsSwitchItem(
            icon = Icons.Default.Clear,
            title = "Mod întunecat",
            description = "Folosește tema întunecată",
            checked = darkModeEnabled,
            onCheckedChange = { enabled ->
                darkModeEnabled = enabled
                PreferencesManager.setDarkMode(context, enabled)
                onThemeChange(enabled)
                Toast.makeText(context, if (enabled) "Mod întunecat activat" else "Mod luminos activat", Toast.LENGTH_SHORT).show()
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Data Section
        SectionTitle(text = "Date")
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingsActionItem(
            icon = Icons.Default.Send,
            title = "Exportă datele",
            description = "Descarcă toate înregistrările tale (${logs.size} log-uri)",
            onClick = {
                if (logs.isEmpty()) {
                    Toast.makeText(context, "Nu ai log-uri de exportat", Toast.LENGTH_SHORT).show()
                } else {
                    val file = ExportHelper.exportToCSV(context, logs)
                    if (file != null) {
                        ExportHelper.shareCSV(context, file)
                        Toast.makeText(context, "Export creat: ${file.name}", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Eroare la export", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        SettingsActionItem(
            icon = Icons.Default.Delete,
            title = "Șterge toate datele",
            description = "Șterge toate log-urile înregistrate",
            onClick = { showDeleteDialog = true },
            isDestructive = true
        )
        
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Șterge toate datele?") },
                text = { Text("Această acțiune nu poate fi anulată. Toate log-urile vor fi șterse permanent.") },
                confirmButton = {
                    TextButton(onClick = {
                        // TODO: Implement delete all logs
                        showDeleteDialog = false
                        Toast.makeText(context, "Funcție în dezvoltare", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Șterge", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Anulează")
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // About Section
        SectionTitle(text = "Despre")
        
        Spacer(modifier = Modifier.height(12.dp))
        
        SettingsActionItem(
            icon = Icons.Default.Info,
            title = "Versiunea aplicației",
            description = "1.0.0",
            onClick = { }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        SettingsActionItem(
            icon = Icons.Default.Notifications,
            title = "Test notificare (DEMO)",
            description = "Trimite o notificare de test acum",
            onClick = {
                TestNotificationHelper.sendTestNotification(context)
                Toast.makeText(context, "Notificare trimisă! Verifică bara de notificări", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    }
}

@Composable
private fun SettingsActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDestructive) 
                MaterialTheme.colorScheme.errorContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isDestructive) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDestructive) 
                        MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = if (isDestructive) 
                    MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}


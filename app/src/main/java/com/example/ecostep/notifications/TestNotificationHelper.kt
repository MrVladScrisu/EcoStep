package com.example.ecostep.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.ecostep.MainActivity
import com.example.ecostep.R

object TestNotificationHelper {
    
    /**
     * Trimite o notificare de test imediat
     * Folosește pentru demo: TestNotificationHelper.sendTestNotification(context)
     */
    fun sendTestNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channelId = "test_notifications"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Test Notificări",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificări de test pentru demo"
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🌱 EcoStep Demo")
            .setContentText("Notificare de test - funcționează perfect!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(9999, notification)
    }
}


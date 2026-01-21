package com.example.ecostep.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.ecostep.data.local.DailyLog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object ExportHelper {
    
    fun exportToCSV(context: Context, logs: List<DailyLog>): File? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "ecostep_export_$timestamp.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            file.bufferedWriter().use { writer ->
                // Header
                writer.write("Date,Transport Type,Distance (km),Meat,Veggies,Dairy,Junk Food,Electricity (kWh),Water (L),Waste Bags,Recycled,Steps,CO2 Score\n")
                
                // Data rows
                logs.forEach { log ->
                    writer.write("${log.date},${log.transportType},${log.transportDistanceKm},${log.meatPortions},${log.veggiesPortions},${log.dairyPortions},${log.junkFoodPortions},${log.electricityKwh},${log.waterLiters},${log.wasteBags},${log.recycledToday},${log.steps},${log.ecoScore}\n")
                }
            }
            
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun shareCSV(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        context.startActivity(Intent.createChooser(shareIntent, "Exportă datele EcoStep"))
    }
}


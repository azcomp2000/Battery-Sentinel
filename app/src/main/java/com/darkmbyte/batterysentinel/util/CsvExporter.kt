package com.darkmbyte.batterysentinel.util

import android.content.Context
import com.darkmbyte.batterysentinel.data.entity.AppUsageLog
import com.darkmbyte.batterysentinel.data.entity.BatteryLog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CsvExporter(private val context: Context) {

    fun exportBatteryLogs(logs: List<BatteryLog>): File? {
        val fileName = "battery_history_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        return try {
            file.printWriter().use { out ->
                out.println("Timestamp,Level,Voltage,Temperature,Status")
                logs.forEach { log ->
                    out.println("${log.timestamp},${log.level},${log.voltage},${log.temperature},${log.status}")
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }

    fun exportAppUsageLogs(logs: List<AppUsageLog>): File? {
        val fileName = "app_usage_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        return try {
            file.printWriter().use { out ->
                out.println("Timestamp,PackageName,ForegroundTimeMs,EstimatedMah")
                logs.forEach { log ->
                    out.println("${log.timestamp},${log.packageName},${log.foregroundTimeMs},${log.estimatedMah}")
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }
}

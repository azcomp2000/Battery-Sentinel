package com.darkmbyte.batterysentinel.util

import android.content.Context
import com.darkmbyte.batterysentinel.data.AppDatabase
import com.darkmbyte.batterysentinel.data.entity.BatteryLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmergencyLogger(private val context: Context) {

    /**
     * Triggers an immediate, high-priority write to the database.
     * Used during Intent.ACTION_BATTERY_LOW to ensure the last known state is captured.
     */
    fun logEmergencyState(level: Int, voltage: Int, temperature: Int, status: Int) {
        val db = AppDatabase.getDatabase(context)
        val log = BatteryLog(
            timestamp = System.currentTimeMillis(),
            level = level,
            voltage = voltage,
            temperature = temperature,
            status = status
        )

        // Use GlobalScope or a dedicated scope to ensure the write completes even if the calling component is killed
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.batteryDao().insert(log)
            } catch (e: Exception) {
                // In a real scenario, we might log to logcat or a fail-safe file
            }
        }
    }
}

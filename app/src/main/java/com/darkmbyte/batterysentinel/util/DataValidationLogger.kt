package com.darkmbyte.batterysentinel.util

import android.app.usage.UsageStats
import android.util.Log

class DataValidationLogger {

    companion object {
        private const val TAG = "BatterySentinelDebug"
    }

    /**
     * Logs calculated app usage data to Logcat for manual verification 
     * against Android System Battery Settings.
     */
    fun logComparisonData(packageName: String, foregroundTimeMs: Long, estimatedMah: Double) {
        val minutes = foregroundTimeMs / (1000 * 60)
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        val timeString = if (hours > 0) "${hours}h ${remainingMinutes}m" else "${remainingMinutes}m"

        Log.d(TAG, "--------------------------------------------------")
        Log.d(TAG, "VALIDATION DATA FOR: $packageName")
        Log.d(TAG, "APP FOREGROUND TIME: $timeString ($foregroundTimeMs ms)")
        Log.d(TAG, "CALCULATED DRAIN: ${String.format("%.2f", estimatedMah)} mAh")
        Log.d(TAG, "ACTION: Compare 'APP FOREGROUND TIME' with Android Settings > Battery > $packageName")
        Log.d(TAG, "--------------------------------------------------")
    }
}

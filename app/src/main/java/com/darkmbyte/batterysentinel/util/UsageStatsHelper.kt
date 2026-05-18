package com.darkmbyte.batterysentinel.util

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.Calendar

class UsageStatsHelper(private val context: Context) {

    fun getUsageStats(startTime: Long, endTime: Long): List<UsageStats> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        // Use INTERVAL_DAILY for better historical accuracy if range is large
        val interval = if (endTime - startTime > 24 * 60 * 60 * 1000) {
            UsageStatsManager.INTERVAL_DAILY
        } else {
            UsageStatsManager.INTERVAL_BEST
        }
        
        val stats = usageStatsManager.queryUsageStats(
            interval,
            startTime,
            endTime
        )
        return stats.filter { it.totalTimeInForeground > 0 }
    }

    fun getUsageStatsForLast24Hours(): List<android.app.usage.UsageStats> {
        val calendar = java.util.Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis
        return getUsageStats(startTime, endTime)
    }
}

package com.darkmbyte.batterysentinel.util

import android.app.usage.UsageStats
import com.darkmbyte.batterysentinel.data.entity.AppUsageLog

class PowerEstimator {

    companion object {
        // Average discharge rates in mA (milliamperes) for different scenarios
        // These are heuristic values for a typical modern smartphone
        private const val AVG_SCREEN_ON_DRAIN_MA = 450.0 
        private const val AVG_CPU_INTENSIVE_DRAIN_MA = 800.0
        private const val AVG_IDLE_DRAIN_MA = 15.0
    }

    /**
     * Estimates power consumption in mAh based on foreground time.
     * Formula: (Time in Hours) * (Average Drain in mA)
     */
    fun estimateAppConsumptionMah(usageStats: UsageStats): Double {
        val foregroundTimeMs = usageStats.totalTimeInForeground
        val foregroundTimeHours = foregroundTimeMs / (1000.0 * 60.0 * 60.0)
        
        // We use a blended average for foreground apps
        // In a real scenario, this would be adjusted based on app category
        return foregroundTimeHours * AVG_SCREEN_ON_DRAIN_MA
    }

    /**
     * Estimates power consumption in mAh based on AppUsageLog.
     */
    fun estimateAppLogConsumptionMah(log: AppUsageLog): Double {
        val foregroundTimeHours = log.foregroundTimeMs / (1000.0 * 60.0 * 60.0)
        return foregroundTimeHours * AVG_SCREEN_ON_DRAIN_MA
    }

    /**
     * Estimates total consumption for a list of apps.
     */
    fun estimateTotalConsumptionMah(statsList: List<UsageStats>): Double {
        return statsList.sumOf { estimateAppConsumptionMah(it) }
    }

    /**
     * Estimates total consumption for a list of AppUsageLog.
     */
    fun estimateTotalLogConsumptionMah(logList: List<AppUsageLog>): Double {
        return logList.sumOf { estimateAppLogConsumptionMah(it) }
    }
}

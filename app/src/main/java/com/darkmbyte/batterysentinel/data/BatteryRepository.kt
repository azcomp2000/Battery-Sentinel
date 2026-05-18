package com.darkmbyte.batterysentinel.data

import com.darkmbyte.batterysentinel.data.dao.BatteryDao
import com.darkmbyte.batterysentinel.data.dao.AppUsageDao
import com.darkmbyte.batterysentinel.data.entity.BatteryLog
import com.darkmbyte.batterysentinel.data.entity.AppUsageLog
import kotlinx.coroutines.flow.Flow

class BatteryRepository(
    private val batteryDao: BatteryDao,
    private val appUsageDao: AppUsageDao
) {
    val latestBatteryLog: Flow<BatteryLog?> = batteryDao.getLatestLog()
    val allUsageLogs: Flow<List<AppUsageLog>> = appUsageDao.getAllUsage()

    suspend fun insertBatteryLog(log: BatteryLog) {
        batteryDao.insert(log)
    }

    suspend fun insertAppUsageLogs(logs: List<AppUsageLog>) {
        appUsageDao.insertAll(logs)
    }

    suspend fun getUsageLogs(startTime: Long): List<AppUsageLog> {
        return appUsageDao.getUsageForPeriod(startTime, System.currentTimeMillis())
    }

    suspend fun getBatteryHistory(startTime: Long, endTime: Long): List<BatteryLog> {
        return batteryDao.getHistoryForPeriod(startTime, endTime)
    }

    suspend fun calculateDischargeRate(hours: Int): Float {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (hours * 60 * 60 * 1000L)
        val logs = getBatteryHistory(startTime, endTime)
        
        if (logs.size < 2) return 0f
        
        val firstLog = logs.first()
        val lastLog = logs.last()
        
        val levelDiff = firstLog.level - lastLog.level
        val timeDiffHours = (lastLog.timestamp - firstLog.timestamp) / (1000.0 * 60 * 60)
        
        return if (timeDiffHours > 0) (levelDiff / timeDiffHours).toFloat() else 0f
    }

    suspend fun estimateRemainingTime(currentLevel: Int, isCharging: Boolean): Long {
        val rate = calculateDischargeRate(24) // Use 24h average
        if (rate <= 0) return -1L
        
        return if (isCharging) {
            val remainingPercent = 100 - currentLevel
            ((remainingPercent / rate) * 60 * 60 * 1000).toLong()
        } else {
            ((currentLevel / rate) * 60 * 60 * 1000).toLong()
        }
    }

    suspend fun estimateBatteryHealth(): String {
        val logs = getBatteryHistory(System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L), System.currentTimeMillis())
        if (logs.isEmpty()) return "Unknown"
        
        val avgVoltage = logs.map { it.voltage }.average()
        val voltageFluctuation = logs.map { Math.abs(it.voltage - avgVoltage) }.average()
        
        return when {
            voltageFluctuation < 50 -> "Excellent"
            voltageFluctuation < 150 -> "Good"
            voltageFluctuation < 300 -> "Fair"
            else -> "Poor"
        }
    }
}

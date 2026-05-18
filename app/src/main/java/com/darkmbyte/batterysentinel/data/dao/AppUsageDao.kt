package com.darkmbyte.batterysentinel.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.darkmbyte.batterysentinel.data.entity.AppUsageLog

@Dao
interface AppUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<AppUsageLog>)

    @Query("SELECT * FROM app_usage_logs ORDER BY timestamp DESC")
    fun getAllUsage(): kotlinx.coroutines.flow.Flow<List<AppUsageLog>>

    @Query("SELECT * FROM app_usage_logs WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY estimatedMah DESC")
    suspend fun getUsageForPeriod(startTime: Long, endTime: Long): List<AppUsageLog>

    @Query("DELETE FROM app_usage_logs WHERE timestamp < :threshold")
    suspend fun deleteOldLogs(threshold: Long)
}

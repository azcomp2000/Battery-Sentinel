package com.darkmbyte.batterysentinel.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.darkmbyte.batterysentinel.data.entity.BatteryLog

@Dao
interface BatteryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: BatteryLog)

    @Query("SELECT * FROM battery_history ORDER BY timestamp DESC LIMIT 1")
    fun getLatestLog(): kotlinx.coroutines.flow.Flow<BatteryLog?>

    @Query("SELECT * FROM battery_history WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp ASC")
    suspend fun getHistoryForPeriod(startTime: Long, endTime: Long): List<BatteryLog>

    @Query("DELETE FROM battery_history WHERE timestamp < :threshold")
    suspend fun deleteOldLogs(threshold: Long)
}

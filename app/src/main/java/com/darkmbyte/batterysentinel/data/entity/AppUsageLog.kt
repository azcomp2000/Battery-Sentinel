package com.darkmbyte.batterysentinel.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_usage_logs",
    indices = [Index(value = ["timestamp"])]
)
data class AppUsageLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val packageName: String,
    val foregroundTimeMs: Long,
    val estimatedMah: Double
)

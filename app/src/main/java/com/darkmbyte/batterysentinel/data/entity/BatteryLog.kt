package com.darkmbyte.batterysentinel.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "battery_history",
    indices = [Index(value = ["timestamp"])]
)
data class BatteryLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val level: Int,
    val voltage: Int,
    val temperature: Int,
    val status: Int,
    val currentMa: Int = 0,
    val powerW: Float = 0f
)

package com.darkmbyte.batterysentinel.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class BatteryReceiver(private val onBatteryChanged: (level: Int, status: String, voltage: Int, temperature: Int) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = (level * 100 / scale.toFloat()).toInt()
            
            val statusInt = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val status = when (statusInt) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "Full"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
                else -> "Unknown"
            }
            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)

            onBatteryChanged(batteryPct, status, voltage, temperature)
        }
    }
}

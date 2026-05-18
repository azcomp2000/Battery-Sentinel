package com.darkmbyte.batterysentinel.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings

class OptimizationHelper(private val context: Context) {

    /**
     * Checks if the app is already ignoring battery optimizations.
     */
    fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    /**
     * Launches the system dialog to request ignoring battery optimizations.
     * Note: This requires the REQUEST_IGNORE_BATTERY_OPTIMIZATIONS permission in Manifest.
     */
    fun requestIgnoreBatteryOptimizations() {
        if (!isIgnoringBatteryOptimizations()) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    fun togglePowerSaveMode() {
        // Note: Toggling power save mode programmatically usually requires system permissions
        // or secure settings access. For non-root, we can only open the settings page.
        val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun killBackgroundApps() {
        // Non-root apps cannot kill other apps' background processes directly.
        // We can open the usage access settings or battery usage settings.
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

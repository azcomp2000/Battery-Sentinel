package com.darkmbyte.batterysentinel.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.darkmbyte.batterysentinel.MainActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.darkmbyte.batterysentinel.data.AppDatabase
import com.darkmbyte.batterysentinel.data.entity.BatteryLog
import com.darkmbyte.batterysentinel.data.entity.AppUsageLog
import com.darkmbyte.batterysentinel.util.BatteryInfoProvider
import com.darkmbyte.batterysentinel.util.UsageStatsHelper
import com.darkmbyte.batterysentinel.util.PowerEstimator

import com.darkmbyte.batterysentinel.widget.BatterySentinelWidget
import androidx.glance.appwidget.updateAll

class BatteryMonitorService : Service() {

    companion object {
        const val CHANNEL_ID = "BatteryMonitorChannel"
        const val NOTIFICATION_ID = 1
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var batteryReceiver: BroadcastReceiver
    private val offlineCache = mutableListOf<BatteryLog>()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupBatteryReceiver()
    }

    private fun setupBatteryReceiver() {
        batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                    handleBatteryUpdate(intent)
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)
    }

    private fun handleBatteryUpdate(intent: Intent) {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val batteryPct = (level * 100 / scale.toFloat()).toInt()
        
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        
        val timestamp = System.currentTimeMillis()
        
        val batteryLog = BatteryLog(
            timestamp = timestamp,
            level = batteryPct,
            voltage = voltage,
            temperature = temperature,
            status = status
        )

        serviceScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                
                // Flush cache if not empty
                if (offlineCache.isNotEmpty()) {
                    offlineCache.forEach { db.batteryDao().insert(it) }
                    offlineCache.clear()
                }
                
                db.batteryDao().insert(batteryLog)
                updateUsageStats(timestamp)
            } catch (e: Exception) {
                // Step 8.3: Cache data if DB is locked or busy
                offlineCache.add(batteryLog)
            }
            
            // Update Widget
            BatterySentinelWidget().updateAll(applicationContext)
            
            // Charging Limit Notification (Step 6.5)
            if (status == BatteryManager.BATTERY_STATUS_CHARGING && level >= 80) {
                showChargingLimitNotification()
            }
        }
    }

    private fun showChargingLimitNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Charging Limit Reached")
            .setContentText("Battery is at 80%. Unplug to preserve health.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
    }

    private fun updateUsageStats(timestamp: Long) {
        val db = AppDatabase.getDatabase(applicationContext)
        val usageHelper = UsageStatsHelper(applicationContext)
        val estimator = PowerEstimator()
        
        val endTime = timestamp
        val startTime = endTime - (5 * 60 * 1000)

        val stats = usageHelper.getUsageStats(startTime, endTime)
        serviceScope.launch {
            stats.forEach { stat ->
                db.appUsageDao().insertAll(
                    listOf(
                        AppUsageLog(
                            packageName = stat.packageName,
                            foregroundTimeMs = stat.totalTimeInForeground,
                            timestamp = endTime,
                            estimatedMah = estimator.estimateAppConsumptionMah(stat)
                        )
                    )
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification("Battery Sentinel: Active Monitoring")
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryReceiver)
    }

    private fun createNotification(content: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Battery Sentinel // ACTIVE")
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_low_battery)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Battery Monitor Service"
            val descriptionText = "Provides persistent battery monitoring"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= TRIM_MEMORY_RUNNING_LOW) {
            // Proactively clear non-essential caches or trigger GC if necessary
            // to prevent service death during heavy system load (e.g., gaming)
            System.gc()
        }
    }
}

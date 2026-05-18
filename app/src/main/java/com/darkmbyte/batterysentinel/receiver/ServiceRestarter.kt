package com.darkmbyte.batterysentinel.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.darkmbyte.batterysentinel.service.BatteryMonitorService

class ServiceRestarter : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, BatteryMonitorService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    fun scheduleRestart(context: Context) {
        val intent = Intent(context, ServiceRestarter::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Schedule restart in 1 minute
        val restartTime = System.currentTimeMillis() + 60000
        alarmManager.set(AlarmManager.RTC_WAKEUP, restartTime, pendingIntent)
    }
}

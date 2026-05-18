package com.darkmbyte.batterysentinel.util

import android.os.Debug
import android.os.Process
import android.util.Log

class SelfConsumptionProfiler {

    companion object {
        private const val TAG = "BatterySentinelProfiler"
    }

    /**
     * Logs current process resource usage to Logcat.
     * Used for sanity check of the app's own energy footprint.
     */
    fun logSelfConsumption() {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        val totalPss = memoryInfo.totalPss / 1024 // MB

        // Note: Direct CPU usage per process is restricted in Android 8+, 
        // but we can track thread time as a proxy.
        val threadTime = Debug.threadCpuTimeNanos() / 1_000_000 // ms

        Log.d(TAG, "--------------------------------------------------")
        Log.d(TAG, "SELF-CONSUMPTION PROFILE")
        Log.d(TAG, "TOTAL PSS MEMORY: $totalPss MB")
        Log.d(TAG, "THREAD CPU TIME: $threadTime ms")
        Log.d(TAG, "PROCESS ID: ${Process.myPid()}")
        Log.d(TAG, "--------------------------------------------------")
    }
}

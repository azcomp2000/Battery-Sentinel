package com.darkmbyte.batterysentinel.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.darkmbyte.batterysentinel.data.AppDatabase
import java.util.Calendar

class CleanupWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(applicationContext)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7) // 7 days retention policy
        val threshold = calendar.timeInMillis

        return try {
            db.batteryDao().deleteOldLogs(threshold)
            db.appUsageDao().deleteOldLogs(threshold)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}

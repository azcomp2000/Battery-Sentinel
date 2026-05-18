package com.darkmbyte.batterysentinel.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.darkmbyte.batterysentinel.data.dao.AppUsageDao
import com.darkmbyte.batterysentinel.data.dao.BatteryDao
import com.darkmbyte.batterysentinel.data.entity.AppUsageLog
import com.darkmbyte.batterysentinel.data.entity.BatteryLog

@Database(entities = [BatteryLog::class, AppUsageLog::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun batteryDao(): BatteryDao
    abstract fun appUsageDao(): AppUsageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "battery_sentinel_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

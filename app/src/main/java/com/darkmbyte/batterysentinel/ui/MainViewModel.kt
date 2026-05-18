package com.darkmbyte.batterysentinel.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import android.os.BatteryManager
import android.content.Context
import com.darkmbyte.batterysentinel.data.AppDatabase
import com.darkmbyte.batterysentinel.data.BatteryRepository
import com.darkmbyte.batterysentinel.data.entity.BatteryLog
import com.darkmbyte.batterysentinel.ui.screen.AnalyticsData
import com.darkmbyte.batterysentinel.ui.screen.AppUsageInfo
import com.darkmbyte.batterysentinel.util.PowerEstimator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: BatteryRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = BatteryRepository(db.batteryDao(), db.appUsageDao())
    }

    private val estimator = PowerEstimator()

    private fun getEstimatedCapacityMah(): Double {
        val bm = getApplication<Application>().getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val chargeCounter = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) // in microampere-hours
        val level = latestBatteryLog.value?.level ?: 0
        
        return if (chargeCounter > 0 && level > 0) {
            (chargeCounter.toDouble() / level.toDouble() * 100.0) / 1000.0
        } else {
            4000.0 // Fallback to typical value
        }
    }

    private val _selectedPeriod = MutableStateFlow("24H")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    // Ticker to trigger updates every 30 seconds
    private val refreshTicker = flow {
        while (true) {
            emit(System.currentTimeMillis())
            kotlinx.coroutines.delay(30000) // 30 seconds
        }
    }

    fun setPeriod(period: String) {
        _selectedPeriod.value = period
    }

    val latestBatteryLog: StateFlow<BatteryLog?> = repository.latestBatteryLog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val appUsageList: StateFlow<List<AppUsageInfo>> = combine(_selectedPeriod, refreshTicker) { period, _ ->
        period
    }
        .flatMapLatest { period ->
            val hours = when (period) {
                "1H" -> 1L
                "24H" -> 24L
                "3D" -> 72L
                "1W" -> 168L
                "1M" -> 720L
                else -> 24L
            }
            val startTime = System.currentTimeMillis() - (hours * 60 * 60 * 1000L)
            
            flow {
                val logs = repository.getUsageLogs(startTime)
                val infoList = logs.groupBy { it.packageName }.map { (pkg, pkgLogs) ->
                    val maxMah = pkgLogs.maxOfOrNull { it.estimatedMah } ?: 0.0
                    val minMah = pkgLogs.minOfOrNull { it.estimatedMah } ?: 0.0
                    val diffMah = maxMah - minMah
                    
                    AppUsageInfo(
                        packageName = pkg,
                        appName = pkg.split(".").last().replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() },
                        consumptionPercentage = 0f,
                        estimatedMah = Math.max(0.0, diffMah)
                    )
                }.sortedByDescending { it.estimatedMah }
                emit(infoList)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analyticsData: StateFlow<AnalyticsData> = combine(_selectedPeriod, refreshTicker) { period, _ ->
        period
    }
        .flatMapLatest { period ->
            val hours = when (period) {
                "1H" -> 1L
                "24H" -> 24L
                "3D" -> 72L
                "1W" -> 168L
                "1M" -> 720L
                else -> 24L
            }
            val endTime = System.currentTimeMillis()
            val startTime = endTime - (hours * 60 * 60 * 1000L)
            
            flow {
                val logs = repository.getBatteryHistory(startTime, endTime).sortedBy { it.timestamp }
                if (logs.size < 2) {
                    emit(AnalyticsData(0.0, 0.0, 0.0, 0.0))
                    return@flow
                }

                var activeDrain = 0.0
                var standbyDrain = 0.0
                var activeTimeMs = 0L
                var standbyTimeMs = 0L

                val capacity = getEstimatedCapacityMah()

                for (i in 0 until logs.size - 1) {
                    val current = logs[i]
                    val next = logs[i + 1]
                    val timeDiff = next.timestamp - current.timestamp
                    val levelDiff = current.level - next.level

                    // If charging, ignore drain
                    val drain = if (levelDiff > 0) (levelDiff / 100.0) * capacity else 0.0

                    if (current.status == 3) { // Discharging
                        activeDrain += drain
                        activeTimeMs += timeDiff
                    } else if (current.status != 2) { // Standby (not charging)
                        standbyDrain += drain
                        standbyTimeMs += timeDiff
                    }
                }

                emit(AnalyticsData(
                    standbyDrainMah = standbyDrain,
                    activeDrainMah = activeDrain,
                    standbyTimeHours = standbyTimeMs / 3600000.0,
                    activeTimeHours = activeTimeMs / 3600000.0
                ))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnalyticsData(0.0, 0.0, 0.0, 0.0))
}
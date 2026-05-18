package com.darkmbyte.batterysentinel

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import androidx.activity.viewModels
import com.darkmbyte.batterysentinel.service.BatteryMonitorService
import com.darkmbyte.batterysentinel.ui.MainViewModel
import com.darkmbyte.batterysentinel.ui.screen.AnalyticsData
import com.darkmbyte.batterysentinel.ui.screen.AnalyticsScreen
import com.darkmbyte.batterysentinel.ui.screen.AppUsageScreen
import com.darkmbyte.batterysentinel.ui.screen.DashboardScreen
import com.darkmbyte.batterysentinel.ui.theme.BatterySentinelTheme
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue

import com.darkmbyte.batterysentinel.util.OptimizationHelper

import androidx.work.*
import com.darkmbyte.batterysentinel.worker.CleanupWorker
import java.util.concurrent.TimeUnit

import com.darkmbyte.batterysentinel.ui.component.CrtOverlay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        scheduleCleanupTask()
        
        setContent {
            BatterySentinelTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    PermissionHandler {
                        val context = LocalContext.current
                        // Start the monitoring service only after permissions are granted
                        val serviceIntent = Intent(context, BatteryMonitorService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }
                        
                        // Request ignoring battery optimizations
                        val optimizationHelper = remember { OptimizationHelper(context) }
                        LaunchedEffect(Unit) {
                            if (!optimizationHelper.isIgnoringBatteryOptimizations()) {
                                optimizationHelper.requestIgnoreBatteryOptimizations()
                            }
                        }
                        
                        MainNavigation(viewModel)
                    }
                    CrtOverlay()
                }
            }
        }
    }
    private fun scheduleCleanupTask() {
        val cleanupRequest = PeriodicWorkRequestBuilder<CleanupWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresCharging(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CleanupTask",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }
}

@Composable
fun PermissionHandler(onPermissionsGranted: @Composable () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    var hasUsageStatsPermission by remember {
        mutableStateOf(checkUsageStatsPermission(context))
    }

    // Re-check permission when app resumes from settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasUsageStatsPermission = checkUsageStatsPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasNotificationPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (hasNotificationPermission && hasUsageStatsPermission) {
        onPermissionsGranted()
    } else if (!hasUsageStatsPermission) {
        UsageStatsPermissionScreen {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
            context.startActivity(intent)
        }
    } else {
        // Waiting for notification permission
        Surface(color = Color.Black) {
            Text(text = "Waiting for Notification Permission...", color = NeonBlue)
        }
    }
}

fun checkUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
    } else {
        appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

@Composable
fun UsageStatsPermissionScreen(onOpenSettings: () -> Unit) {
    Surface(color = Color.Black) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(
                text = "Usage Access Required",
                style = MaterialTheme.typography.titleLarge,
                color = NeonBlue
            )
            Text(
                text = "To monitor battery usage per app, please grant Usage Access in Settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Button(
                onClick = onOpenSettings,
                colors = ButtonDefaults.buttonColors(containerColor = NeonBlue)
            ) {
                Text("Open Settings", color = Color.Black)
            }
        }
    }
}

@Composable
fun MainNavigation(viewModel: MainViewModel) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("Dashboard", "Analytics", "Usage")
    val icons = listOf(Icons.Default.Dashboard, Icons.Default.History, Icons.Default.Analytics)

    val batteryLog by viewModel.latestBatteryLog.collectAsState()
    val usageList by viewModel.appUsageList.collectAsState()
    val analyticsData by viewModel.analyticsData.collectAsState()
    val selectedPeriod by viewModel.selectedPeriod.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = NeonBlue
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(imageVector = icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = NeonBlue,
                            indicatorColor = NeonBlue,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = Color.Black
        ) {
            when (selectedItem) {
                0 -> DashboardScreen(
                    batteryLevel = batteryLog?.level ?: 0,
                    voltage = batteryLog?.voltage ?: 0,
                    temperature = batteryLog?.temperature ?: 0,
                    status = batteryLog?.status ?: 0,
                    healthStatus = "Good"
                )
                1 -> AnalyticsScreen(
                    data = analyticsData,
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { viewModel.setPeriod(it) }
                )
                2 -> AppUsageScreen(usageList = usageList)
            }
        }
    }
}
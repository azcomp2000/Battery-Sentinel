package com.darkmbyte.batterysentinel.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue

import com.darkmbyte.batterysentinel.ui.component.IndustrialCard
import com.darkmbyte.batterysentinel.ui.component.BeveledCardShape
import com.darkmbyte.batterysentinel.ui.component.SegmentedBatteryIndicator
import com.darkmbyte.batterysentinel.ui.theme.CriticalRed
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue
import com.darkmbyte.batterysentinel.ui.theme.WarningOrange
import com.darkmbyte.batterysentinel.ui.theme.Black

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.darkmbyte.batterysentinel.util.OptimizationHelper
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember

@Composable
fun DashboardScreen(
    batteryLevel: Int,
    voltage: Int,
    temperature: Int,
    status: Int,
    healthStatus: String,
    isPowerSaveMode: Boolean = false
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val optimizationHelper = remember { OptimizationHelper(context) }

    val isCharging = status == 2
    val infiniteTransition = rememberInfiniteTransition(label = "charging")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val statusText = when (status) {
        2 -> "CHARGING"
        3 -> "DISCHARGING"
        4 -> "NOT CHARGING"
        5 -> "FULL"
        else -> "UNKNOWN"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "BATTERY SENTINEL // CORE_MONITOR",
            style = MaterialTheme.typography.labelSmall,
            color = NeonBlue.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        IndustrialCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$batteryLevel%",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCharging) NeonBlue.copy(alpha = pulseAlpha) else NeonBlue
                )
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCharging) NeonBlue.copy(alpha = pulseAlpha) else NeonBlue
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box {
                    SegmentedBatteryIndicator(level = batteryLevel)
                    if (isCharging) {
                        // Overlay a pulsing glow
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(NeonBlue.copy(alpha = pulseAlpha * 0.2f))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                HealthCard(
                    label = "VOLTAGE", 
                    value = "${voltage / 1000.0}V", 
                    color = NeonBlue
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                HealthCard(
                    label = "TEMP", 
                    value = "${temperature / 10.0}°C", 
                    color = if (temperature > 400) CriticalRed else WarningOrange
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        IndustrialCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("HEALTH_STATUS", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Text(healthStatus.uppercase(), color = NeonBlue, style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "POWER_CONTROLS",
            style = MaterialTheme.typography.labelSmall,
            color = NeonBlue.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )

        IndustrialCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        optimizationHelper.togglePowerSaveMode() 
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue),
                    shape = BeveledCardShape
                ) {
                    Text("POWER SAVE", color = Black, style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        optimizationHelper.killBackgroundApps() 
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = CriticalRed),
                    shape = BeveledCardShape
                ) {
                    Text("KILL APPS", color = Black, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
fun HealthCard(label: String, value: String, color: Color) {
    IndustrialCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = color)
        }
    }
}

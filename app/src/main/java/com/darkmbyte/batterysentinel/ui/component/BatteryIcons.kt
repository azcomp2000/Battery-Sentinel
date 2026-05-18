package com.darkmbyte.batterysentinel.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Icon
import com.darkmbyte.batterysentinel.ui.theme.CriticalRed
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue
import com.darkmbyte.batterysentinel.ui.theme.WarningOrange

@Composable
fun BatteryStateIcon(
    status: Int,
    level: Int,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when {
        status == 2 -> Icons.Default.BatteryChargingFull to NeonBlue
        level <= 15 -> Icons.Default.BatteryAlert to CriticalRed
        status == 3 -> Icons.Default.BatteryStd to WarningOrange
        else -> Icons.Default.BatteryStd to NeonBlue
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = modifier
    )
}

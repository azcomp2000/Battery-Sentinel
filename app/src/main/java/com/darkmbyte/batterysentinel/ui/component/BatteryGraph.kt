package com.darkmbyte.batterysentinel.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.darkmbyte.batterysentinel.data.entity.BatteryLog
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue
import androidx.compose.ui.graphics.Color

import com.darkmbyte.batterysentinel.ui.theme.CriticalRed
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue
import com.darkmbyte.batterysentinel.ui.theme.WarningOrange

@Composable
fun BatteryGraph(
    history: List<BatteryLog>, 
    showTemperature: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        if (history.isEmpty()) return@Canvas

        val maxTime = history.maxOf { it.timestamp }
        val minTime = history.minOf { it.timestamp }
        val timeRange = (maxTime - minTime).coerceAtLeast(1).toFloat()
        
        // Draw Battery Level Path
        val levelPath = Path()
        history.forEachIndexed { index, log ->
            val x = (log.timestamp - minTime) / timeRange * size.width
            val y = (1f - (log.level / 100f)) * size.height
            if (index == 0) levelPath.moveTo(x, y) else levelPath.lineTo(x, y)
        }

        drawPath(
            path = levelPath,
            color = NeonBlue,
            style = Stroke(width = 2.dp.toPx())
        )

        if (showTemperature) {
            val tempPath = Path()
            val maxTemp = history.maxOf { it.temperature }.toFloat()
            val minTemp = history.minOf { it.temperature }.toFloat()
            val tempRange = (maxTemp - minTemp).coerceAtLeast(1f)

            history.forEachIndexed { index, log ->
                val x = (log.timestamp - minTime) / timeRange * size.width
                val y = (1f - ((log.temperature - minTemp) / tempRange)) * size.height
                if (index == 0) tempPath.moveTo(x, y) else tempPath.lineTo(x, y)
            }

            drawPath(
                path = tempPath,
                color = WarningOrange.copy(alpha = 0.6f),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }
}

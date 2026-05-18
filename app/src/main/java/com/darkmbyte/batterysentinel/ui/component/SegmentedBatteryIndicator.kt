package com.darkmbyte.batterysentinel.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkmbyte.batterysentinel.ui.theme.CriticalRed
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue
import com.darkmbyte.batterysentinel.ui.theme.WarningOrange

@Composable
fun SegmentedBatteryIndicator(
    level: Int,
    modifier: Modifier = Modifier
) {
    val segments = 10
    val activeSegments = (level / 10).coerceIn(0, segments)
    
    val color = when {
        level <= 15 -> CriticalRed
        level <= 40 -> WarningOrange
        else -> NeonBlue
    }

    Canvas(modifier = modifier.height(40.dp).fillMaxWidth()) {
        val spacing = 8.dp.toPx()
        val segmentWidth = (size.width - (segments - 1) * spacing) / segments
        val segmentHeight = size.height

        for (i in 0 until segments) {
            val isFilled = i < activeSegments
            val alpha = if (isFilled) 1f else 0.2f
            
            drawRoundRect(
                color = color.copy(alpha = alpha),
                topLeft = Offset(i * (segmentWidth + spacing), 0f),
                size = Size(segmentWidth, segmentHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )
        }
    }
}

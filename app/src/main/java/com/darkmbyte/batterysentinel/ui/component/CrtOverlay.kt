package com.darkmbyte.batterysentinel.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CrtOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val scanlineSpacing = 4.dp.toPx()
        val scanlineCount = (size.height / scanlineSpacing).toInt()
        
        // Draw Scanlines
        for (i in 0..scanlineCount) {
            val y = i * scanlineSpacing
            drawLine(
                color = Color.Black.copy(alpha = 0.15f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // Subtle Vignette/CRT curvature simulation could be added here
    }
}

package com.darkmbyte.batterysentinel.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.darkmbyte.batterysentinel.ui.theme.GridGray
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue

val BeveledCardShape = GenericShape { size, _ ->
    val bevel = 20f
    moveTo(bevel, 0f)
    lineTo(size.width - bevel, 0f)
    lineTo(size.width, bevel)
    lineTo(size.width, size.height - bevel)
    lineTo(size.width - bevel, size.height)
    lineTo(bevel, size.height)
    lineTo(0f, size.height - bevel)
    lineTo(0f, bevel)
    close()
}

@Composable
fun IndustrialCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(Color.Black, BeveledCardShape)
            .border(1.dp, NeonBlue.copy(alpha = 0.5f), BeveledCardShape)
            .padding(1.dp)
    ) {
        // Grid Pattern Background
        Canvas(modifier = Modifier.matchParentSize()) {
            val step = 20.dp.toPx()
            for (x in 0..(size.width / step).toInt()) {
                drawLine(
                    color = GridGray,
                    start = Offset(x * step, 0f),
                    end = Offset(x * step, size.height),
                    strokeWidth = 1f
                )
            }
            for (y in 0..(size.height / step).toInt()) {
                drawLine(
                    color = GridGray,
                    start = Offset(0f, y * step),
                    end = Offset(size.width, y * step),
                    strokeWidth = 1f
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            content = content
        )
    }
}

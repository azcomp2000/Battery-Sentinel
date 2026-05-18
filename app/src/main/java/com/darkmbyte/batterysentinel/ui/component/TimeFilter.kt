package com.darkmbyte.batterysentinel.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue

enum class TimeRange(val label: String, val hours: Int) {
    ONE_HOUR("1h", 1),
    SIX_HOURS("6h", 6),
    TWENTY_FOUR_HOURS("24h", 24),
    SEVEN_DAYS("7d", 168)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFilterRow(
    selectedRange: TimeRange,
    onRangeSelected: (TimeRange) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimeRange.values().forEach { range ->
            FilterChip(
                selected = selectedRange == range,
                onClick = { onRangeSelected(range) },
                label = { Text(text = range.label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonBlue,
                    selectedLabelColor = Color.Black,
                    containerColor = Color.Transparent,
                    labelColor = NeonBlue
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = NeonBlue,
                    selectedBorderColor = NeonBlue,
                    borderWidth = 1.dp
                )
            )
        }
    }
}

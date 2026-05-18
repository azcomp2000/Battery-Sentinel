package com.darkmbyte.batterysentinel.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue

data class AnalyticsData(
    val standbyDrainMah: Double,
    val activeDrainMah: Double,
    val standbyTimeHours: Double,
    val activeTimeHours: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    data: AnalyticsData,
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    val periods = listOf("1H", "24H", "3D", "1W", "1M")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "DETAILED ANALYTICS",
            style = MaterialTheme.typography.titleLarge,
            color = NeonBlue
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            periods.forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { onPeriodSelected(period) },
                    label = { Text(period, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonBlue,
                        selectedLabelColor = Color.Black,
                        labelColor = NeonBlue,
                        containerColor = Color.Transparent
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = NeonBlue.copy(alpha = 0.5f),
                        selectedBorderColor = NeonBlue
                    )
                )
            }
        }

        AnalyticsCard(
            title = "ACTIVE MODE",
            drain = data.activeDrainMah,
            time = data.activeTimeHours,
            accentColor = NeonBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnalyticsCard(
            title = "STANDBY MODE",
            drain = data.standbyDrainMah,
            time = data.standbyTimeHours,
            accentColor = Color.Gray
        )
    }
}

@Composable
fun AnalyticsCard(title: String, drain: Double, time: Double, accentColor: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF121212)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = accentColor)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "${String.format("%.1f", drain)} mAh",
                        style = MaterialTheme.typography.titleLarge,
                        color = NeonBlue
                    )
                    Text(
                        text = "Total Consumption",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${String.format("%.1f", time)} h",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.LightGray
                    )
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

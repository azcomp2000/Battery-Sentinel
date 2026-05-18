package com.darkmbyte.batterysentinel.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.darkmbyte.batterysentinel.data.AppDatabase
import com.darkmbyte.batterysentinel.ui.theme.NeonBlue
import kotlinx.coroutines.flow.first

class BatterySentinelWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val db = AppDatabase.getDatabase(context)
        val latestLog = db.batteryDao().getLatestLog().first()

        provideContent {
            BatteryWidgetContent(latestLog?.level ?: 0)
        }
    }

    @Composable
    private fun BatteryWidgetContent(level: Int) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SENTINEL // $level%",
                style = TextStyle(color = ColorProvider(NeonBlue))
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            // Simple segmented bar for widget
            Row(modifier = GlanceModifier.fillMaxWidth().height(8.dp)) {
                for (i in 1..10) {
                    val isFilled = i <= (level / 10)
                    Box(
                        modifier = GlanceModifier
                            .defaultWeight()
                            .height(8.dp)
                            .padding(horizontal = 1.dp)
                            .background(if (isFilled) NeonBlue else Color.DarkGray)
                    ) {}
                }
            }
        }
    }
}

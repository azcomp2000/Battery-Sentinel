package com.darkmbyte.batterysentinel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    secondary = WarningOrange,
    tertiary = GridGray,
    background = Black,
    surface = Black,
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = NeonBlue,
    onBackground = NeonBlue,
    onSurface = NeonBlue,
    error = CriticalRed
)

private val LightColorScheme = lightColorScheme(
    primary = NeonBlue,
    secondary = WarningOrange,
    tertiary = LightGray,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Black,
    onBackground = Black,
    onSurface = Black,
    error = CriticalRed
)

@Composable
fun BatterySentinelTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

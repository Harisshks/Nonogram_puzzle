package com.nonogram.puzzle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryLight,
    tertiary = AccentGold,
    background = AppBackground,
    surface = SurfaceWhite,
    error = AccentRed,
    onPrimary = Color.White,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark,
    onError = Color.White
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF7C6FFF),
    secondary = Color(0xFF9E93FF),
    tertiary = AccentGold,
    background = Color(0xFF0A0A0F),
    surface = Color(0xFF12121A),
    error = AccentRed,
    onPrimary = Color.White,
    onSecondary = Color(0xFFE8E6FF),
    onBackground = Color(0xFFE8E6FF),
    onSurface = Color(0xFFE8E6FF),
    onError = Color.White
)

@Composable
fun NonogramTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = Typography,
        content = content
    )
}

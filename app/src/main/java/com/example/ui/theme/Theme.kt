package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KiwiDarkColorScheme = darkColorScheme(
    primary = KiwiGreen,
    onPrimary = Color(0xFF00391C),
    primaryContainer = Color(0xFF1E1F23),
    onPrimaryContainer = KiwiGreen,
    secondary = KiwiCyan,
    onSecondary = Color(0xFF002244),
    secondaryContainer = Color(0xFF1E1F23),
    onSecondaryContainer = KiwiCyan,
    tertiary = KiwiPink,
    onTertiary = Color(0xFF380036),
    error = Color(0xFFFF5252),
    onError = Color.White,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = BorderGray800
)

@Composable
fun KiwiCodeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KiwiDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    KiwiCodeTheme(content = content)
}


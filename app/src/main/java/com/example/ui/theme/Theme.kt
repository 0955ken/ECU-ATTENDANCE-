package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EcuGoldLight,
    onPrimary = EcuNavyDark,
    primaryContainer = EcuNavyContainer,
    onPrimaryContainer = EcuGoldOnContainer,
    secondary = EcuGold,
    onSecondary = EcuNavyDark,
    secondaryContainer = EcuGoldContainer,
    onSecondaryContainer = EcuGoldLight,
    tertiary = EcuGoldDark,
    background = EcuNavyDark,
    onBackground = White,
    surface = EcuNavy,
    onSurface = White,
    surfaceVariant = EcuNavySurface,
    onSurfaceVariant = Slate200,
    outline = EcuGoldDark.copy(alpha = 0.5f),
    error = EcuRed,
    onError = White
)

private val LightColorScheme = lightColorScheme(
    primary = EcuNavy,
    onPrimary = White,
    primaryContainer = EcuGoldContainer,
    onPrimaryContainer = EcuGoldLight,
    secondary = EcuGoldDark,
    onSecondary = White,
    secondaryContainer = EcuGoldOnContainer,
    onSecondaryContainer = EcuNavyDark,
    tertiary = EcuNavyLight,
    background = Slate50,
    onBackground = Slate900,
    surface = White,
    onSurface = Slate900,
    surfaceVariant = Slate100,
    onSurfaceVariant = Slate700,
    outline = Slate200,
    error = EcuRed,
    onError = White
)

@Composable
fun ECUTheme(
    darkTheme: Boolean = true, // Default to dignified ECU executive navy theme
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

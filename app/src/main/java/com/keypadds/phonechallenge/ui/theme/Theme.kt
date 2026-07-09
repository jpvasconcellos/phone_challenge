package com.keypadds.phonechallenge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AppAccentGreen,
    secondary = AppAccentGreen,
    tertiary = AppAccentGreen,
    background = AppBackgroundDark,
    surface = AppSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun PhoneChallengeTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalAppColors provides DefaultAppColors) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = Typography,
            content = content
        )
    }
}
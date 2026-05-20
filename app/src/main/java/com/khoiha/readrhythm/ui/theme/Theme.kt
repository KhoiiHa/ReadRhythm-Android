package com.khoiha.readrhythm.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WarmRed80,
    primaryContainer = WarmRedContainerDark,
    secondary = Ochre80,
    secondaryContainer = OchreContainerDark,
    tertiary = Clay80,
    tertiaryContainer = ClayContainerDark,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = Color(0xFF321110),
    onPrimaryContainer = Color(0xFFF8DAD7),
    onSecondary = Color(0xFF2C1D03),
    onSecondaryContainer = Color(0xFFF5E1B2),
    onTertiary = Color(0xFF2B2119),
    onTertiaryContainer = Color(0xFFEAD8C8),
    onBackground = DarkText,
    onSurface = DarkText,
    onSurfaceVariant = DarkTextMuted,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = WarmRed40,
    primaryContainer = WarmRedContainerLight,
    secondary = Ochre40,
    secondaryContainer = OchreContainerLight,
    tertiary = Clay40,
    tertiaryContainer = ClayContainerLight,
    background = WarmBackground,
    surface = WarmSurface,
    surfaceVariant = WarmSurfaceVariant,
    onPrimary = Color.White,
    onPrimaryContainer = Color(0xFF3B1816),
    onSecondary = Color.White,
    onSecondaryContainer = Color(0xFF2D2108),
    onTertiary = Color.White,
    onTertiaryContainer = Color(0xFF2C2119),
    onBackground = WarmText,
    onSurface = WarmText,
    onSurfaceVariant = WarmTextMuted,
    outline = WarmOutline
)

@Composable
fun ReadRhythmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ReadRhythmTypography,
        content = content
    )
}

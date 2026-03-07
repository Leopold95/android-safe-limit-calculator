package com.alexandr.safelimitcalculator.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.alexandr.safelimitcalculator.theme.AppTheme

// Green Accent Color #17D96E
private val GreenAccent = Color(0xFF17D96E)

// Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = GreenAccent,
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF0E5A42),
    onPrimaryContainer = Color(0xFF62F1A9),

    secondary = Color(0xFF4ECDC4),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF2A7A71),
    onSecondaryContainer = Color(0xFF8EFFF8),

    tertiary = Color(0xFFFF6B9D),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF8A4050),
    onTertiaryContainer = Color(0xFFFFB8D4),

    error = Color(0xFFFF6B6B),
    onError = Color(0xFF000000),
    errorContainer = Color(0xFF8B3A3A),
    onErrorContainer = Color(0xFFFFB4B4),

    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFA8A8A8),
    outline = Color(0xFF5A5A5A)
)

// Light Theme Colors
private val LightColorScheme = lightColorScheme(
    primary = GreenAccent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5E9),
    onPrimaryContainer = Color(0xFF004D2B),

    secondary = Color(0xFF0FBDAC),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCEF7F0),
    onSecondaryContainer = Color(0xFF00504A),

    tertiary = Color(0xFFD0467F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9E3),
    onTertiaryContainer = Color(0xFF3A1D34),

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1C1C1C),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454E),
    outline = Color(0xFF79747E)
)

@Composable
fun SafeLimitCalculatorTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = { AppTheme(content = content) }
    )
}
package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FreeverseColorScheme = lightColorScheme(
    primary = FreeversePrimary,
    onPrimary = Color.White,
    primaryContainer = FreeverseSoftLavender,
    onPrimaryContainer = FreeversePrimary,
    secondary = FreeverseSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEBF3FF),
    onSecondaryContainer = FreeverseSecondary,
    tertiary = FreeverseAccentCyan,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0F7FA),
    onTertiaryContainer = Color(0xFF006064),
    background = FreeverseBackground,
    onBackground = FreeverseTextPrimary,
    surface = FreeverseSurface,
    onSurface = FreeverseTextPrimary,
    surfaceVariant = FreeverseSoftLavender,
    onSurfaceVariant = FreeverseTextSecondary,
    outline = FreeverseBorder,
    outlineVariant = Color(0xFFCBD5E1),
    error = FreeverseError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Enforce bright, modern identity as required
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FreeverseColorScheme,
        typography = Typography,
        content = content
    )
}

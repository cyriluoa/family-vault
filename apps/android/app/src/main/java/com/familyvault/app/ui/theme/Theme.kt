package com.familyvault.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VaultPrimary,
    onPrimary = Color.White,
    primaryContainer = VaultPrimarySoft,
    onPrimaryContainer = VaultPrimaryDark,
    secondary = VaultTextSecondary,
    onSecondary = Color.White,
    background = VaultBackground,
    onBackground = VaultTextPrimary,
    surface = VaultSurface,
    onSurface = VaultTextPrimary,
    surfaceVariant = VaultSurfaceAlt,
    onSurfaceVariant = VaultTextSecondary,
    outline = VaultBorder,
    error = VaultError,
    onError = Color.White,
    errorContainer = VaultErrorSoft,
    onErrorContainer = VaultError
)

private val DarkColorScheme = darkColorScheme(
    primary = VaultPrimarySoft,
    onPrimary = VaultPrimaryDark,
    primaryContainer = VaultPrimary,
    onPrimaryContainer = Color.White,
    secondary = VaultDarkTextSecondary,
    onSecondary = VaultDarkBackground,
    background = VaultDarkBackground,
    onBackground = VaultDarkTextPrimary,
    surface = VaultDarkSurface,
    onSurface = VaultDarkTextPrimary,
    surfaceVariant = VaultDarkSurfaceAlt,
    onSurfaceVariant = VaultDarkTextSecondary,
    outline = VaultTextSecondary,
    error = VaultErrorSoft,
    onError = VaultError,
    errorContainer = VaultError,
    onErrorContainer = Color.White
)

@Composable
fun FamilyVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

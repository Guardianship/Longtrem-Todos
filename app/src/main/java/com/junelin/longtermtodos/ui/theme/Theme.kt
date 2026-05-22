package com.junelin.longtermtodos.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SageLightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val SageDarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

private val AmberLightColors = lightColorScheme(
    primary = amber_light_primary, onPrimary = amber_light_onPrimary,
    primaryContainer = amber_light_primaryContainer, onPrimaryContainer = amber_light_onPrimaryContainer,
    secondary = amber_light_secondary, onSecondary = amber_light_onSecondary,
    secondaryContainer = amber_light_secondaryContainer, onSecondaryContainer = amber_light_onSecondaryContainer,
    tertiary = amber_light_tertiary, onTertiary = amber_light_onTertiary,
    tertiaryContainer = amber_light_tertiaryContainer, onTertiaryContainer = amber_light_onTertiaryContainer,
    error = amber_light_error, errorContainer = amber_light_errorContainer,
    onError = amber_light_onError, onErrorContainer = amber_light_onErrorContainer,
    background = amber_light_background, onBackground = amber_light_onBackground,
    surface = amber_light_surface, onSurface = amber_light_onSurface,
    surfaceVariant = amber_light_surfaceVariant, onSurfaceVariant = amber_light_onSurfaceVariant,
    outline = amber_light_outline, inverseOnSurface = amber_light_inverseOnSurface,
    inverseSurface = amber_light_inverseSurface, inversePrimary = amber_light_inversePrimary,
    surfaceTint = amber_light_surfaceTint, outlineVariant = amber_light_outlineVariant,
    scrim = amber_light_scrim,
)

private val AmberDarkColors = darkColorScheme(
    primary = amber_dark_primary, onPrimary = amber_dark_onPrimary,
    primaryContainer = amber_dark_primaryContainer, onPrimaryContainer = amber_dark_onPrimaryContainer,
    secondary = amber_dark_secondary, onSecondary = amber_dark_onSecondary,
    secondaryContainer = amber_dark_secondaryContainer, onSecondaryContainer = amber_dark_onSecondaryContainer,
    tertiary = amber_dark_tertiary, onTertiary = amber_dark_onTertiary,
    tertiaryContainer = amber_dark_tertiaryContainer, onTertiaryContainer = amber_dark_onTertiaryContainer,
    error = amber_dark_error, errorContainer = amber_dark_errorContainer,
    onError = amber_dark_onError, onErrorContainer = amber_dark_onErrorContainer,
    background = amber_dark_background, onBackground = amber_dark_onBackground,
    surface = amber_dark_surface, onSurface = amber_dark_onSurface,
    surfaceVariant = amber_dark_surfaceVariant, onSurfaceVariant = amber_dark_onSurfaceVariant,
    outline = amber_dark_outline, inverseOnSurface = amber_dark_inverseOnSurface,
    inverseSurface = amber_dark_inverseSurface, inversePrimary = amber_dark_inversePrimary,
    surfaceTint = amber_dark_surfaceTint, outlineVariant = amber_dark_outlineVariant,
    scrim = amber_dark_scrim,
)

private val BlueLightColors = lightColorScheme(
    primary = blue_light_primary, onPrimary = blue_light_onPrimary,
    primaryContainer = blue_light_primaryContainer, onPrimaryContainer = blue_light_onPrimaryContainer,
    secondary = blue_light_secondary, onSecondary = blue_light_onSecondary,
    secondaryContainer = blue_light_secondaryContainer, onSecondaryContainer = blue_light_onSecondaryContainer,
    tertiary = blue_light_tertiary, onTertiary = blue_light_onTertiary,
    tertiaryContainer = blue_light_tertiaryContainer, onTertiaryContainer = blue_light_onTertiaryContainer,
    error = blue_light_error, errorContainer = blue_light_errorContainer,
    onError = blue_light_onError, onErrorContainer = blue_light_onErrorContainer,
    background = blue_light_background, onBackground = blue_light_onBackground,
    surface = blue_light_surface, onSurface = blue_light_onSurface,
    surfaceVariant = blue_light_surfaceVariant, onSurfaceVariant = blue_light_onSurfaceVariant,
    outline = blue_light_outline, inverseOnSurface = blue_light_inverseOnSurface,
    inverseSurface = blue_light_inverseSurface, inversePrimary = blue_light_inversePrimary,
    surfaceTint = blue_light_surfaceTint, outlineVariant = blue_light_outlineVariant,
    scrim = blue_light_scrim,
)

private val BlueDarkColors = darkColorScheme(
    primary = blue_dark_primary, onPrimary = blue_dark_onPrimary,
    primaryContainer = blue_dark_primaryContainer, onPrimaryContainer = blue_dark_onPrimaryContainer,
    secondary = blue_dark_secondary, onSecondary = blue_dark_onSecondary,
    secondaryContainer = blue_dark_secondaryContainer, onSecondaryContainer = blue_dark_onSecondaryContainer,
    tertiary = blue_dark_tertiary, onTertiary = amber_dark_onTertiary,
    tertiaryContainer = blue_dark_tertiaryContainer, onTertiaryContainer = blue_dark_onTertiaryContainer,
    error = blue_dark_error, errorContainer = blue_dark_errorContainer,
    onError = blue_dark_onError, onErrorContainer = blue_dark_onErrorContainer,
    background = blue_dark_background, onBackground = blue_dark_onBackground,
    surface = blue_dark_surface, onSurface = blue_dark_onSurface,
    surfaceVariant = blue_dark_surfaceVariant, onSurfaceVariant = blue_dark_onSurfaceVariant,
    outline = blue_dark_outline, inverseOnSurface = blue_dark_inverseOnSurface,
    inverseSurface = blue_dark_inverseSurface, inversePrimary = blue_dark_inversePrimary,
    surfaceTint = blue_dark_surfaceTint, outlineVariant = blue_dark_outlineVariant,
    scrim = blue_dark_scrim,
)

private val RoseLightColors = lightColorScheme(
    primary = rose_light_primary, onPrimary = rose_light_onPrimary,
    primaryContainer = rose_light_primaryContainer, onPrimaryContainer = rose_light_onPrimaryContainer,
    secondary = rose_light_secondary, onSecondary = rose_light_onSecondary,
    secondaryContainer = rose_light_secondaryContainer, onSecondaryContainer = rose_light_onSecondaryContainer,
    tertiary = rose_light_tertiary, onTertiary = rose_light_onTertiary,
    tertiaryContainer = rose_light_tertiaryContainer, onTertiaryContainer = rose_light_onTertiaryContainer,
    error = rose_light_error, errorContainer = rose_light_errorContainer,
    onError = rose_light_onError, onErrorContainer = rose_light_onErrorContainer,
    background = rose_light_background, onBackground = rose_light_onBackground,
    surface = rose_light_surface, onSurface = rose_light_onSurface,
    surfaceVariant = rose_light_surfaceVariant, onSurfaceVariant = rose_light_onSurfaceVariant,
    outline = rose_light_outline, inverseOnSurface = rose_light_inverseOnSurface,
    inverseSurface = rose_light_inverseSurface, inversePrimary = rose_light_inversePrimary,
    surfaceTint = rose_light_surfaceTint, outlineVariant = rose_light_outlineVariant,
    scrim = rose_light_scrim,
)

private val RoseDarkColors = darkColorScheme(
    primary = rose_dark_primary, onPrimary = rose_dark_onPrimary,
    primaryContainer = rose_dark_primaryContainer, onPrimaryContainer = rose_dark_onPrimaryContainer,
    secondary = rose_dark_secondary, onSecondary = rose_dark_onSecondary,
    secondaryContainer = rose_dark_secondaryContainer, onSecondaryContainer = rose_dark_onSecondaryContainer,
    tertiary = rose_dark_tertiary, onTertiary = rose_dark_onTertiary,
    tertiaryContainer = rose_dark_tertiaryContainer, onTertiaryContainer = rose_dark_onTertiaryContainer,
    error = rose_dark_error, errorContainer = rose_dark_errorContainer,
    onError = rose_dark_onError, onErrorContainer = rose_dark_onErrorContainer,
    background = rose_dark_background, onBackground = rose_dark_onBackground,
    surface = rose_dark_surface, onSurface = rose_dark_onSurface,
    surfaceVariant = rose_dark_surfaceVariant, onSurfaceVariant = rose_dark_onSurfaceVariant,
    outline = rose_dark_outline, inverseOnSurface = rose_dark_inverseOnSurface,
    inverseSurface = rose_dark_inverseSurface, inversePrimary = rose_dark_inversePrimary,
    surfaceTint = rose_dark_surfaceTint, outlineVariant = rose_dark_outlineVariant,
    scrim = rose_dark_scrim,
)

@Composable
fun LongTermTodosTheme(
    darkMode: String = "system",
    themeColor: String = "sage",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (darkMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            val lightScheme = when (themeColor) {
                "amber" -> AmberLightColors
                "blue" -> BlueLightColors
                "rose" -> RoseLightColors
                else -> SageLightColors
            }
            val darkScheme = when (themeColor) {
                "amber" -> AmberDarkColors
                "blue" -> BlueDarkColors
                "rose" -> RoseDarkColors
                else -> SageDarkColors
            }
            if (darkTheme) darkScheme else lightScheme
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

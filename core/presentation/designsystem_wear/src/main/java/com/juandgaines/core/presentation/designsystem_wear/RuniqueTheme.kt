package com.juandgaines.core.presentation.designsystem_wear

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Typography
import com.juandgaines.core.presentation.designsystem.DarkColorScheme
import com.juandgaines.core.presentation.designsystem.Poppins

private fun createColorScheme():ColorScheme{
    val phoneTheme = DarkColorScheme
    return ColorScheme(
        primary = phoneTheme.primary,
        primaryContainer = phoneTheme.primaryContainer,
        onPrimary = phoneTheme.onPrimary,
        onPrimaryContainer = phoneTheme.onPrimaryContainer,
        secondary = phoneTheme.secondary,
        onSecondary = phoneTheme.onSecondary,
        secondaryContainer = phoneTheme.secondaryContainer,
        onSecondaryContainer = phoneTheme.onSecondaryContainer,
        tertiary = phoneTheme.tertiary,
        onTertiary = phoneTheme.onTertiary,
        tertiaryContainer = phoneTheme.tertiaryContainer,
        surface = phoneTheme.surface,
        surfaceDim = phoneTheme.surfaceVariant,
        onSurfaceVariant = phoneTheme.onSurfaceVariant,
        background = phoneTheme.background,
        onError = phoneTheme.onError,
        error = phoneTheme.error,
        onBackground = phoneTheme.onBackground,
    )
}

private fun createTypography():Typography{
    return Typography(
        defaultFontFamily = Poppins,

    )
}
@Composable
fun RuniqueTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = createColorScheme(),
        typography = createTypography(),
    ){
        content()
    }
}
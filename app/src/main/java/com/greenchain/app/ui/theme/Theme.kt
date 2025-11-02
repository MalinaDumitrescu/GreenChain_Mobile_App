package com.greenchain.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    background = Background,
    tertiary = GreenSecondary,
    onPrimary = BrownLight,
    onSecondary = BrownDark
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimary,
    secondary = Background,
    tertiary = GreenSecondary,
    onPrimary = BrownLight,
    onSecondary = BrownDark
)

@Composable
fun GreenChainTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}

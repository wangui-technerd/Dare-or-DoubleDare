package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppThemeColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val gold: Color,
    val goldLight: Color,
    val crimson: Color,
    val cardBackground: Color
)

val LocalAppThemeColors = staticCompositionLocalOf {
    AppThemeColors(
        isDark = true,
        background = ObsidianBlack,
        surface = ObsidianCard,
        surfaceElevated = ObsidianCardElevated,
        border = ObsidianBorder,
        textPrimary = CreamWhite,
        textSecondary = SoftCharcoal,
        textMuted = MutedGrey,
        gold = ChampagneGold,
        goldLight = LightGold,
        crimson = CrimsonRed,
        cardBackground = ObsidianCard
    )
}

object AppTheme {
    val colors: AppThemeColors
        @Composable
        get() = LocalAppThemeColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonRed,
    onPrimary = CreamWhite,
    primaryContainer = VelvetWine,
    onPrimaryContainer = LightGold,
    secondary = ChampagneGold,
    onSecondary = ObsidianBlack,
    secondaryContainer = AntiqueGold,
    onSecondaryContainer = ObsidianBlack,
    tertiary = LevelTease,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = CreamWhite,
    surface = ObsidianCard,
    onSurface = CreamWhite,
    surfaceVariant = ObsidianCardElevated,
    onSurfaceVariant = SoftCharcoal,
    outline = ObsidianBorder,
    error = SafetyRed,
    onError = CreamWhite
)

private val LightColorScheme = lightColorScheme(
    primary = LightAccentRose,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8E0),
    onPrimaryContainer = Color(0xFF3E0013),
    secondary = LightAccentGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFBE4B5),
    onSecondaryContainer = Color(0xFF281800),
    tertiary = LevelTease,
    onTertiary = Color.White,
    background = LightBgWarmPearl,
    onBackground = LightTextEspresso,
    surface = LightCardIvory,
    onSurface = LightTextEspresso,
    surfaceVariant = LightCardElevated,
    onSurfaceVariant = LightTextCharcoal,
    outline = LightBorderGold,
    error = SafetyRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val appThemeColors = if (isDark) {
        AppThemeColors(
            isDark = true,
            background = ObsidianBlack,
            surface = ObsidianCard,
            surfaceElevated = ObsidianCardElevated,
            border = ObsidianBorder,
            textPrimary = CreamWhite,
            textSecondary = SoftCharcoal,
            textMuted = MutedGrey,
            gold = ChampagneGold,
            goldLight = LightGold,
            crimson = CrimsonRed,
            cardBackground = ObsidianCard
        )
    } else {
        AppThemeColors(
            isDark = false,
            background = LightBgWarmPearl,
            surface = LightCardIvory,
            surfaceElevated = LightCardElevated,
            border = LightBorderGold,
            textPrimary = LightTextEspresso,
            textSecondary = LightTextCharcoal,
            textMuted = LightTextMuted,
            gold = LightAccentGold,
            goldLight = ChampagneGold,
            crimson = LightAccentRose,
            cardBackground = LightCardIvory
        )
    }

    CompositionLocalProvider(LocalAppThemeColors provides appThemeColors) {
        MaterialTheme(
            colorScheme = if (isDark) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}

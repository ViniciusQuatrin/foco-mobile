package com.foco.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.foco.app.domain.ThemeMode

/** Exact web tokens from globals.css [data-theme="dark"]. */
object FocoDark {
    val bg = Color(0xFF05050A)
    val fg = Color(0xFFE8FFF6)
    val muted = Color(0xFF7A9A8E)
    val accent = Color(0xFF00FF9F)
    val accent2 = Color(0xFFFF2A6D)
    val accent3 = Color(0xFF05D9E8)
    val accentFg = Color(0xFF05050A)
    val border = Color(0xFF00FF9F)
    val surface = Color(0xFF0B1210)
    val surface2 = Color(0xFF101A16)
    val glitchA = Color(0xFFFF2A6D)
    val glitchB = Color(0xFF05D9E8)
    val glitchC = Color(0xFFF9F002)
    val warn = Color(0xFFFF6B8A)
}

/** Exact web tokens from globals.css [data-theme="light"] / :root. */
object FocoLight {
    val bg = Color(0xFFF4F1EA)
    val fg = Color(0xFF0A0A0A)
    val muted = Color(0xFF3A3A3A)
    val accent = Color(0xFFFF003C)
    val accent2 = Color(0xFF00E5FF)
    val accent3 = Color(0xFFFFE600)
    val accentFg = Color(0xFFF4F1EA)
    val border = Color(0xFF0A0A0A)
    val surface = Color(0xFFFFFFFF)
    val surface2 = Color(0xFFECE8DF)
    val glitchA = Color(0xFFFF003C)
    val glitchB = Color(0xFF00E5FF)
    val glitchC = Color(0xFFFFE600)
    val warn = Color(0xFF8A0000)
}

@Immutable
data class FocoColors(
    val bg: Color,
    val fg: Color,
    val muted: Color,
    val accent: Color,
    val accent2: Color,
    val accent3: Color,
    val accentFg: Color,
    val border: Color,
    val surface: Color,
    val surface2: Color,
    val glitchA: Color,
    val glitchB: Color,
    val glitchC: Color,
    val warn: Color,
    val isDark: Boolean
)

val LocalFocoColors = staticCompositionLocalOf {
    FocoColors(
        bg = FocoDark.bg,
        fg = FocoDark.fg,
        muted = FocoDark.muted,
        accent = FocoDark.accent,
        accent2 = FocoDark.accent2,
        accent3 = FocoDark.accent3,
        accentFg = FocoDark.accentFg,
        border = FocoDark.border,
        surface = FocoDark.surface,
        surface2 = FocoDark.surface2,
        glitchA = FocoDark.glitchA,
        glitchB = FocoDark.glitchB,
        glitchC = FocoDark.glitchC,
        warn = FocoDark.warn,
        isDark = true
    )
}


// Back-compat aliases used by older call sites during migration
val NeonCyan get() = FocoDark.accent
val NeonMagenta get() = FocoDark.accent2
val NeonYellow get() = FocoDark.glitchC
val NearBlack get() = FocoDark.bg
val PanelDark get() = FocoDark.surface
val BorderGlow get() = FocoDark.border
val TextMuted get() = FocoDark.muted

private val CyberDarkColors = darkColorScheme(
    primary = FocoDark.accent,
    onPrimary = FocoDark.accentFg,
    secondary = FocoDark.accent2,
    onSecondary = FocoDark.fg,
    tertiary = FocoDark.accent3,
    background = FocoDark.bg,
    onBackground = FocoDark.fg,
    surface = FocoDark.surface,
    onSurface = FocoDark.fg,
    surfaceVariant = FocoDark.surface2,
    onSurfaceVariant = FocoDark.muted,
    outline = FocoDark.border,
    error = FocoDark.warn
)

private val LightColors = lightColorScheme(
    primary = FocoLight.accent,
    onPrimary = FocoLight.accentFg,
    secondary = FocoLight.accent2,
    onSecondary = FocoLight.fg,
    tertiary = FocoLight.accent3,
    background = FocoLight.bg,
    onBackground = FocoLight.fg,
    surface = FocoLight.surface,
    onSurface = FocoLight.fg,
    surfaceVariant = FocoLight.surface2,
    onSurfaceVariant = FocoLight.muted,
    outline = FocoLight.border,
    error = FocoLight.warn
)

@Composable
fun FocoTheme(
    themeMode: ThemeMode = ThemeMode.DARK_CYBER,
    content: @Composable () -> Unit
) {
    val dark = when (themeMode) {
        ThemeMode.DARK_CYBER -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val foco = if (dark) {
        FocoColors(
            bg = FocoDark.bg,
            fg = FocoDark.fg,
            muted = FocoDark.muted,
            accent = FocoDark.accent,
            accent2 = FocoDark.accent2,
            accent3 = FocoDark.accent3,
            accentFg = FocoDark.accentFg,
            border = FocoDark.border,
            surface = FocoDark.surface,
            surface2 = FocoDark.surface2,
            glitchA = FocoDark.glitchA,
            glitchB = FocoDark.glitchB,
            glitchC = FocoDark.glitchC,
            warn = FocoDark.warn,
            isDark = true
        )
    } else {
        FocoColors(
            bg = FocoLight.bg,
            fg = FocoLight.fg,
            muted = FocoLight.muted,
            accent = FocoLight.accent,
            accent2 = FocoLight.accent2,
            accent3 = FocoLight.accent3,
            accentFg = FocoLight.accentFg,
            border = FocoLight.border,
            surface = FocoLight.surface,
            surface2 = FocoLight.surface2,
            glitchA = FocoLight.glitchA,
            glitchB = FocoLight.glitchB,
            glitchC = FocoLight.glitchC,
            warn = FocoLight.warn,
            isDark = false
        )
    }
    CompositionLocalProvider(LocalFocoColors provides foco) {
        MaterialTheme(
            colorScheme = if (dark) CyberDarkColors else LightColors,
            content = content
        )
    }
}

@Composable
fun focoColors(): FocoColors = LocalFocoColors.current

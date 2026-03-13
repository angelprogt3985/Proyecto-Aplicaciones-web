package com.mindguardians.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Colores del diseño original ──────────────────────────────────────────────
val SpaceDark       = Color(0xFF0A0A0F)
val SpaceDeep       = Color(0xFF1A1A2E)
val SpaceMid        = Color(0xFF16213E)
val SpaceBlue       = Color(0xFF0F3460)

val CyanNeon        = Color(0xFF00D9FF)
val PurpleNeon      = Color(0xFF7C3AED)
val PurpleLight     = Color(0xFFA78BFA)
val GoldNeon        = Color(0xFFFBBF24)
val GoldDark        = Color(0xFFF59E0B)
val GreenNeon       = Color(0xFF4ADE80)
val RedAlert        = Color(0xFFEF4444)

val TextWhite       = Color(0xFFFFFFFF)
val TextMuted       = Color(0x66FFFFFF)   // white 40%
val TextDim         = Color(0x4DFFFFFF)   // white 30%

val CardBg          = Color(0x66000000)   // black 40%
val BorderPurple    = Color(0x4D7C3AED)   // purple 30%

private val DarkColorScheme = darkColorScheme(
    primary        = CyanNeon,
    secondary      = PurpleNeon,
    tertiary       = GoldNeon,
    background     = SpaceDark,
    surface        = SpaceDeep,
    onPrimary      = SpaceDark,
    onBackground   = TextWhite,
    onSurface      = TextWhite,
)

@Composable
fun MindGuardiansTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content     = content,
    )
}

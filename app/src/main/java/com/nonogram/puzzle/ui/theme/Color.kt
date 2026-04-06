package com.nonogram.puzzle.ui.theme

import androidx.compose.ui.graphics.Color

// Backgrounds
val AppBackground = Color(0xFFEEF3FB)
val SurfaceWhite = Color(0xFFFFFFFF)
val CardBlue = Color(0xFFDDE8F8)
val CardBlueDark = Color(0xFF4A90D9)

// Primary
val PrimaryBlue = Color(0xFF2F7EE8)
val PrimaryDark = Color(0xFF1A5FBB)
val PrimaryLight = Color(0xFF6BAAFF)

// Accents
val AccentGold = Color(0xFFFFB800)
val AccentGreen = Color(0xFF4CAF50)
val AccentRed = Color(0xFFE53935)
val AccentOrange = Color(0xFFFF6B35)

// Text
val TextDark = Color(0xFF1A2340)
val TextMedium = Color(0xFF4A5568)
val TextLight = Color(0xFF8A9BB0)
val TextWhite = Color(0xFFFFFFFF)

// Borders
val BorderLight = Color(0xFFD0DCF0)
val BorderMedium = Color(0xFFB0C4DE)

// Game grid (light)
val CellEmptyBg = Color(0xFFE8F0FC)
val CellEmptyBorder = Color(0xFFCCD8EF)
val CellFilledBg = Color(0xFF3A5FA0)
val CellFilledBorder = Color(0xFF2A4F90)
val CellActiveHighlight = Color(0xFFD0E2FF)

// Compatibility — map old names to light theme
val Background = AppBackground
val Surface = SurfaceWhite
val Card = CardBlue
val Border = BorderLight
val Accent = PrimaryBlue
val AccentSoft = PrimaryLight
val Gold = AccentGold
val Green = AccentGreen
val Red = AccentRed
val TextPrimary = TextDark
val TextMuted = TextLight

val TextDim = TextMedium
val White = Color(0xFFFFFFFF)
val GradientPurpleEnd = PrimaryDark

/** @deprecated appearance swatches — light pastels for settings picker */
val ThemePurple = Color(0xFFE8E0F5)
val ThemeOcean = Color(0xFFE0F0F8)
val ThemeForest = Color(0xFFE8F5E9)

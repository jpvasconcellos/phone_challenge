package com.keypadds.phonechallenge.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(
    /** Page/screen background — near-black (#0D0D0D) */
    val backgroundDark: Color,
    /** Player screen background — pure black (#000000) */
    val backgroundBlack: Color,
    /** Card / input surface (#1A1A1A) */
    val surface: Color,
    /** Artwork placeholder / thumbnail background (#2A2A2A) */
    val surfaceLight: Color,
    /** Snackbar background (#323232) */
    val surfaceLighter: Color,
    /** Accent / progress color — Spotify green (#1DB954) */
    val accent: Color,
    /** Secondary text, artist names (#9E9E9E) */
    val textSecondary: Color,
    /** Player subtitle, time labels (#AAAAAA) */
    val textTertiary: Color,
    /** Placeholder / icon grey (#757575) */
    val iconGrey: Color,
    /** List divider (#222222) */
    val divider: Color,
    /** Progress bar track (#3A3A3A) */
    val progressTrack: Color,
    /** Dark text/icon color (#A8A8A8) */
    val searchDarkGrey: Color,
)

val DefaultAppColors = AppColors(
    backgroundDark   = AppBackgroundDark,
    backgroundBlack  = AppBackgroundBlack,
    surface          = AppSurface,
    surfaceLight     = AppSurfaceLight,
    surfaceLighter   = AppSurfaceLighter,
    accent           = AppAccentGreen,
    textSecondary    = AppTextGrey,
    textTertiary     = AppTextLightGrey,
    iconGrey         = AppIconGrey,
    divider          = AppDivider,
    progressTrack    = AppProgressTrack,
    searchDarkGrey   = AppSearchDarkGrey,
)

val LocalAppColors = staticCompositionLocalOf { DefaultAppColors }

/** Convenience accessor: `MaterialTheme.appColors.accent` */
val MaterialTheme.appColors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

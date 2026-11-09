package com.messmanager.app.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Semantic colors that Material3 ColorScheme does not provide. */
@Immutable
data class ExtraColors(
    val success: Color = Color.Unspecified,
    val warning: Color = Color.Unspecified,
)

val LocalExtraColors = staticCompositionLocalOf { ExtraColors() }

val MaterialTheme.extraColors: ExtraColors
    @Composable @ReadOnlyComposable get() = LocalExtraColors.current

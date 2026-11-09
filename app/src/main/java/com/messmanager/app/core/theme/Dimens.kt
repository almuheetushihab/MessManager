package com.messmanager.app.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    val spaceXs: Dp = 0.dp,
    val spaceS: Dp = 0.dp,
    val spaceM: Dp = 0.dp,
    val spaceL: Dp = 0.dp,
    val spaceXl: Dp = 0.dp,
    val radiusS: Dp = 0.dp,
    val radiusM: Dp = 0.dp,
    val radiusL: Dp = 0.dp,
    val iconM: Dp = 0.dp,
    val iconXl: Dp = 0.dp,
    val cardElevation: Dp = 0.dp,
)

val LocalDimens = staticCompositionLocalOf { Dimens() }

val MaterialTheme.dimens: Dimens
    @Composable @ReadOnlyComposable get() = LocalDimens.current

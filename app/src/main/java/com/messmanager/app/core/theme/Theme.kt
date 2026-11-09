package com.messmanager.app.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import com.messmanager.app.R

/** All colors come from res/values(-night)/colors.xml, nothing is hard coded here. */
@Composable
private fun appColorScheme(darkTheme: Boolean): ColorScheme {
    val base = if (darkTheme) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = colorResource(R.color.primary),
        onPrimary = colorResource(R.color.on_primary),
        primaryContainer = colorResource(R.color.primary_container),
        onPrimaryContainer = colorResource(R.color.on_primary_container),
        secondary = colorResource(R.color.secondary),
        onSecondary = colorResource(R.color.on_secondary),
        secondaryContainer = colorResource(R.color.secondary_container),
        onSecondaryContainer = colorResource(R.color.on_secondary_container),
        background = colorResource(R.color.background),
        onBackground = colorResource(R.color.on_background),
        surface = colorResource(R.color.surface),
        onSurface = colorResource(R.color.on_surface),
        surfaceVariant = colorResource(R.color.surface_variant),
        onSurfaceVariant = colorResource(R.color.on_surface_variant),
        outline = colorResource(R.color.outline),
        error = colorResource(R.color.error),
        onError = colorResource(R.color.on_error),
    )
}

@Composable
private fun appExtraColors() = ExtraColors(
    success = colorResource(R.color.success),
    warning = colorResource(R.color.warning),
)

@Composable
private fun appDimens() = Dimens(
    spaceXs = dimensionResource(R.dimen.space_xs),
    spaceS = dimensionResource(R.dimen.space_s),
    spaceM = dimensionResource(R.dimen.space_m),
    spaceL = dimensionResource(R.dimen.space_l),
    spaceXl = dimensionResource(R.dimen.space_xl),
    radiusS = dimensionResource(R.dimen.radius_s),
    radiusM = dimensionResource(R.dimen.radius_m),
    radiusL = dimensionResource(R.dimen.radius_l),
    iconM = dimensionResource(R.dimen.icon_m),
    iconXl = dimensionResource(R.dimen.icon_xl),
    cardElevation = dimensionResource(R.dimen.card_elevation),
)

@Composable
fun MessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val dimens = appDimens()
    val shapes = Shapes(
        small = RoundedCornerShape(dimens.radiusS),
        medium = RoundedCornerShape(dimens.radiusM),
        large = RoundedCornerShape(dimens.radiusL),
    )
    CompositionLocalProvider(
        LocalDimens provides dimens,
        LocalExtraColors provides appExtraColors(),
    ) {
        MaterialTheme(
            colorScheme = appColorScheme(darkTheme),
            typography = AppTypography,
            shapes = shapes,
            content = content,
        )
    }
}

package com.messmanager.app.feature.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.feature.home.HomeUiState
import com.messmanager.app.ui.components.StatCard
import com.messmanager.app.ui.components.formatMoney

@Composable
fun StatGrid(state: HomeUiState, modifier: Modifier = Modifier) {
    val gap = MaterialTheme.dimens.spaceM
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            StatCard(
                title = stringResource(R.string.home_meal_rate),
                value = formatMoney(state.summary.mealRate),
                icon = Icons.Rounded.AttachMoney,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            StatCard(
                title = stringResource(R.string.home_total_bazar),
                value = formatMoney(state.summary.totalBazar),
                icon = Icons.Rounded.LocalGroceryStore,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            StatCard(
                title = stringResource(R.string.home_total_meals),
                value = state.summary.totalMeals.toString(),
                icon = Icons.Rounded.Restaurant,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            StatCard(
                title = stringResource(R.string.home_today_meals),
                value = state.todayMeals.toString(),
                icon = Icons.Rounded.Today,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun StatGridPreview() {
    PreviewSurface {
        StatGrid(
            state = PreviewData.homeNormal,
            modifier = Modifier.padding(MaterialTheme.dimens.spaceM),
        )
    }
}

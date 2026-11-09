package com.messmanager.app.feature.meal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.MealType
import com.messmanager.app.feature.meal.components.DateSelector
import com.messmanager.app.feature.meal.components.MemberMealCard
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState

@Composable
fun MealScreen(viewModel: MealViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MealContent(
        state = state,
        onPreviousDay = viewModel::onPreviousDay,
        onNextDay = viewModel::onNextDay,
        onToggleMeal = viewModel::onToggleMeal,
        onGuestsChange = viewModel::onGuestsChange,
    )
}

@Composable
fun MealContent(
    state: MealUiState,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToggleMeal: (MealEntry, MealType) -> Unit,
    onGuestsChange: (MealEntry, Int) -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = stringResource(R.string.meal_title)) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            DateSelector(
                date = state.date,
                onPrevious = onPreviousDay,
                onNext = onNextDay,
                modifier = Modifier.padding(horizontal = MaterialTheme.dimens.spaceS),
            )
            when {
                state.isLoading -> LoadingState()
                state.items.isEmpty() -> EmptyState(
                    message = stringResource(R.string.meal_empty),
                    icon = Icons.Rounded.Restaurant,
                )
                else -> {
                    Text(
                        text = stringResource(R.string.meal_total_for_day, state.dayTotal),
                        modifier = Modifier.padding(horizontal = MaterialTheme.dimens.spaceM),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                    ) {
                        items(state.items, key = { it.member.id }) { item ->
                            MemberMealCard(
                                item = item,
                                onToggle = onToggleMeal,
                                onGuestsChange = onGuestsChange,
                            )
                        }
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun MealScreenNormalPreview() {
    PreviewSurface {
        MealContent(
            state = PreviewData.mealNormal,
            onPreviousDay = {},
            onNextDay = {},
            onToggleMeal = { _, _ -> },
            onGuestsChange = { _, _ -> },
        )
    }
}

@ThemePreviews
@Composable
private fun MealScreenEmptyPreview() {
    PreviewSurface {
        MealContent(
            state = PreviewData.mealEmpty,
            onPreviousDay = {},
            onNextDay = {},
            onToggleMeal = { _, _ -> },
            onGuestsChange = { _, _ -> },
        )
    }
}

@ThemePreviews
@Composable
private fun MealScreenLoadingPreview() {
    PreviewSurface {
        MealContent(
            state = PreviewData.mealLoading,
            onPreviousDay = {},
            onNextDay = {},
            onToggleMeal = { _, _ -> },
            onGuestsChange = { _, _ -> },
        )
    }
}

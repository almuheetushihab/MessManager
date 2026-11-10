package com.messmanager.app.feature.meal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
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
import com.messmanager.app.feature.meal.components.MealQuickActions
import com.messmanager.app.feature.meal.components.MemberMealCard
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.ClosedMonthBanner
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun MealScreen(
    onOpenMembers: () -> Unit = {},
    viewModel: MealViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MealContent(
        state = state,
        onOpenMembers = onOpenMembers,
        onPreviousDay = viewModel::onPreviousDay,
        onNextDay = viewModel::onNextDay,
        onToday = viewModel::onToday,
        onDateSelected = viewModel::onDateSelected,
        onAllOn = viewModel::onAllOn,
        onCopyYesterday = viewModel::onCopyYesterday,
        onToggleMeal = viewModel::onToggleMeal,
        onGuestsChange = viewModel::onGuestsChange,
        onToggleAdminOverride = viewModel::onToggleAdminOverride,
    )
}

@Composable
fun MealContent(
    state: MealUiState,
    onOpenMembers: () -> Unit,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onAllOn: (MealType) -> Unit,
    onCopyYesterday: () -> Unit,
    onToggleMeal: (MealEntry, MealType) -> Unit,
    onGuestsChange: (MealEntry, Int) -> Unit,
    onToggleAdminOverride: (Boolean) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val updatedMessage = stringResource(R.string.msg_meals_updated)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = stringResource(R.string.meal_title)) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            DateSelector(
                date = state.date,
                onPrevious = onPreviousDay,
                onNext = onNextDay,
                onToday = onToday,
                onDateSelected = onDateSelected,
                modifier = Modifier.padding(horizontal = MaterialTheme.dimens.spaceS),
            )

            AnimatedVisibility(visible = state.isMonthClosed) {
                ClosedMonthBanner()
            }

            AnimatedVisibility(visible = !state.isMonthClosed && state.isCutoffPassed) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .padding(horizontal = MaterialTheme.dimens.spaceM, vertical = MaterialTheme.dimens.spaceS),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.meal_cutoff_passed, state.cutoffTime.orEmpty()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f),
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.meal_admin_override),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(end = MaterialTheme.dimens.spaceXs),
                        )
                        Switch(
                            checked = state.adminOverride,
                            onCheckedChange = onToggleAdminOverride,
                        )
                    }
                }
            }

            when {
                state.isLoading -> LoadingState()
                state.items.isEmpty() -> EmptyState(
                    message = stringResource(R.string.meal_empty),
                    icon = Icons.Rounded.Restaurant,
                    actionLabel = stringResource(R.string.action_add_first_member),
                    onActionClick = onOpenMembers,
                )
                else -> {
                    Column(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.spaceM)) {
                        MealQuickActions(
                            onAllOn = { mealType ->
                                onAllOn(mealType)
                                scope.launch { snackbarHostState.showSnackbar(updatedMessage) }
                            },
                            onCopyYesterday = {
                                onCopyYesterday()
                                scope.launch { snackbarHostState.showSnackbar(updatedMessage) }
                            },
                            enabled = !state.isEditingDisabled,
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.dimens.spaceXs),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.meal_total_for_day, state.dayTotal),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            if (state.isMonthClosed) {
                                AssistChip(
                                    onClick = {},
                                    label = { Text(stringResource(R.string.month_closed_badge)) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = colorResource(R.color.closed_month_container),
                                        labelColor = colorResource(R.color.on_closed_month_container),
                                    ),
                                )
                            }
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                    ) {
                        items(state.items, key = { it.member.id }) { item ->
                            Box(modifier = Modifier.animateItem()) {
                                MemberMealCard(
                                    item = item,
                                    onToggle = onToggleMeal,
                                    onGuestsChange = onGuestsChange,
                                    enabled = !state.isEditingDisabled,
                                )
                            }
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
            onOpenMembers = {},
            onPreviousDay = {},
            onNextDay = {},
            onToday = {},
            onDateSelected = {},
            onAllOn = {},
            onCopyYesterday = {},
            onToggleMeal = { _, _ -> },
            onGuestsChange = { _, _ -> },
            onToggleAdminOverride = {},
        )
    }
}

@ThemePreviews
@Composable
private fun MealScreenEmptyPreview() {
    PreviewSurface {
        MealContent(
            state = PreviewData.mealEmpty,
            onOpenMembers = {},
            onPreviousDay = {},
            onNextDay = {},
            onToday = {},
            onDateSelected = {},
            onAllOn = {},
            onCopyYesterday = {},
            onToggleMeal = { _, _ -> },
            onGuestsChange = { _, _ -> },
            onToggleAdminOverride = {},
        )
    }
}

@ThemePreviews
@Composable
private fun MealScreenLoadingPreview() {
    PreviewSurface {
        MealContent(
            state = PreviewData.mealLoading,
            onOpenMembers = {},
            onPreviousDay = {},
            onNextDay = {},
            onToday = {},
            onDateSelected = {},
            onAllOn = {},
            onCopyYesterday = {},
            onToggleMeal = { _, _ -> },
            onGuestsChange = { _, _ -> },
            onToggleAdminOverride = {},
        )
    }
}

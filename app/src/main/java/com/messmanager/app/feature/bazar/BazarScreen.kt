package com.messmanager.app.feature.bazar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messmanager.app.R
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.formatEpochDay
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.BazarUnit
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.feature.bazar.components.BazarFormSheet
import com.messmanager.app.feature.bazar.components.BazarItem
import com.messmanager.app.ui.components.AddFab
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.ClosedMonthBanner
import com.messmanager.app.ui.components.DismissibleItem
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState
import com.messmanager.app.ui.components.MonthPicker
import com.messmanager.app.ui.components.formatMoney

@Composable
fun BazarScreen(viewModel: BazarViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BazarContent(
        state = state,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
        onAddClick = viewModel::onAddClick,
        onEditClick = viewModel::onEditClick,
        onDelete = viewModel::onDelete,
        onMarkAsPaid = viewModel::onMarkAsPaid,
        onUndoDelete = viewModel::onUndoDelete,
        onClearUndo = viewModel::onClearUndo,
        onClearSavedMessage = viewModel::onClearSavedMessage,
        onAmountChange = viewModel::onAmountChange,
        onQuickAmountClick = viewModel::onQuickAmountClick,
        onCategoryChange = viewModel::onCategoryChange,
        onQuantityChange = viewModel::onQuantityChange,
        onUnitChange = viewModel::onUnitChange,
        onPaymentTypeChange = viewModel::onPaymentTypeChange,
        onCreditShopNameChange = viewModel::onCreditShopNameChange,
        onPaidByMemberChange = viewModel::onPaidByMemberChange,
        onDateChange = viewModel::onDateChange,
        onNoteChange = viewModel::onNoteChange,
        onSave = viewModel::onSave,
        onDismissForm = viewModel::onDismissForm,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BazarContent(
    state: BazarUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Bazar) -> Unit,
    onDelete: (Bazar) -> Unit,
    onMarkAsPaid: (Bazar) -> Unit,
    onUndoDelete: () -> Unit,
    onClearUndo: () -> Unit,
    onClearSavedMessage: () -> Unit,
    onAmountChange: (String) -> Unit,
    onQuickAmountClick: (Int) -> Unit,
    onCategoryChange: (BazarCategory) -> Unit,
    onQuantityChange: (String) -> Unit,
    onUnitChange: (BazarUnit) -> Unit,
    onPaymentTypeChange: (PaymentType) -> Unit,
    onCreditShopNameChange: (String) -> Unit,
    onPaidByMemberChange: (Long?) -> Unit,
    onDateChange: (Long) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismissForm: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val deletedMessage = stringResource(R.string.bazar_deleted_msg)
    val savedMessage = stringResource(R.string.msg_bazar_saved)
    val undoActionLabel = stringResource(R.string.action_undo)

    LaunchedEffect(state.recentlyDeletedBazar) {
        if (state.recentlyDeletedBazar != null) {
            val result = snackbarHostState.showSnackbar(
                message = deletedMessage,
                actionLabel = undoActionLabel,
            )
            if (result == SnackbarResult.ActionPerformed) {
                onUndoDelete()
            }
            onClearUndo()
        }
    }

    LaunchedEffect(state.showSavedMessage) {
        if (state.showSavedMessage) {
            snackbarHostState.showSnackbar(message = savedMessage)
            onClearSavedMessage()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = stringResource(R.string.bazar_title)) },
        floatingActionButton = {
            if (!state.isMonthClosed) {
                AddFab(onClick = onAddClick)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val currentMonth = state.items.firstOrNull()?.epochDay?.let {
                java.time.YearMonth.from(java.time.LocalDate.ofEpochDay(it))
            } ?: java.time.YearMonth.now()

            MonthPicker(
                selectedMonth = currentMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                isClosed = state.isMonthClosed,
            )

            AnimatedVisibility(visible = state.isMonthClosed) {
                ClosedMonthBanner()
            }

            when {
                state.isLoading -> LoadingState()
                state.items.isEmpty() -> EmptyState(
                    message = stringResource(R.string.bazar_empty),
                    icon = Icons.Rounded.LocalGroceryStore,
                    actionLabel = if (!state.isMonthClosed) stringResource(R.string.bazar_add_title) else null,
                    onActionClick = onAddClick,
                )
                else -> {
                    val groupedItems = remember(state.items) {
                        state.items.groupBy { it.epochDay }.toSortedMap(Comparator.reverseOrder())
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = MaterialTheme.dimens.spaceL),
                    ) {
                        groupedItems.forEach { (epochDay, itemsForDay) ->
                            stickyHeader(key = "header_$epochDay") {
                                DayHeader(
                                    epochDay = epochDay,
                                    dayTotal = itemsForDay.sumOf { it.amount },
                                )
                            }
                            items(itemsForDay, key = { it.id }) { bazar ->
                                Box(modifier = Modifier.animateItem()) {
                                    DismissibleItem(
                                        isDismissible = !state.isMonthClosed,
                                        onDismiss = { onDelete(bazar) },
                                    ) {
                                        BazarItem(
                                            bazar = bazar,
                                            onEdit = { if (!state.isMonthClosed) onEditClick(bazar) },
                                            onDelete = { if (!state.isMonthClosed) onDelete(bazar) },
                                            onMarkAsPaid = { if (!state.isMonthClosed) onMarkAsPaid(bazar) },
                                            modifier = Modifier.padding(
                                                horizontal = MaterialTheme.dimens.spaceM,
                                                vertical = MaterialTheme.dimens.spaceXs,
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    state.form?.let { form ->
        BazarFormSheet(
            form = form,
            members = state.members,
            onAmountChange = onAmountChange,
            onQuickAmountClick = onQuickAmountClick,
            onCategoryChange = onCategoryChange,
            onQuantityChange = onQuantityChange,
            onUnitChange = onUnitChange,
            onPaymentTypeChange = onPaymentTypeChange,
            onCreditShopNameChange = onCreditShopNameChange,
            onPaidByMemberChange = onPaidByMemberChange,
            onDateChange = onDateChange,
            onNoteChange = onNoteChange,
            onSave = onSave,
            onDismiss = onDismissForm,
        )
    }
}

@Composable
private fun DayHeader(
    epochDay: Long,
    dayTotal: Double,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = MaterialTheme.dimens.spaceM, vertical = MaterialTheme.dimens.spaceS),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = epochDay.formatEpochDay(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = formatMoney(dayTotal),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun BazarScreenNormalPreview() {
    PreviewSurface {
        BazarContent(
            state = PreviewData.bazarNormal,
            onPreviousMonth = {},
            onNextMonth = {},
            onAddClick = {},
            onEditClick = {},
            onDelete = {},
            onMarkAsPaid = {},
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
            onAmountChange = {},
            onQuickAmountClick = {},
            onCategoryChange = {},
            onQuantityChange = {},
            onUnitChange = {},
            onPaymentTypeChange = {},
            onCreditShopNameChange = {},
            onPaidByMemberChange = {},
            onDateChange = {},
            onNoteChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

@ThemePreviews
@Composable
private fun BazarScreenEmptyPreview() {
    PreviewSurface {
        BazarContent(
            state = PreviewData.bazarEmpty,
            onPreviousMonth = {},
            onNextMonth = {},
            onAddClick = {},
            onEditClick = {},
            onDelete = {},
            onMarkAsPaid = {},
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
            onAmountChange = {},
            onQuickAmountClick = {},
            onCategoryChange = {},
            onQuantityChange = {},
            onUnitChange = {},
            onPaymentTypeChange = {},
            onCreditShopNameChange = {},
            onPaidByMemberChange = {},
            onDateChange = {},
            onNoteChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

@ThemePreviews
@Composable
private fun BazarScreenLoadingPreview() {
    PreviewSurface {
        BazarContent(
            state = PreviewData.bazarLoading,
            onPreviousMonth = {},
            onNextMonth = {},
            onAddClick = {},
            onEditClick = {},
            onDelete = {},
            onMarkAsPaid = {},
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
            onAmountChange = {},
            onQuickAmountClick = {},
            onCategoryChange = {},
            onQuantityChange = {},
            onUnitChange = {},
            onPaymentTypeChange = {},
            onCreditShopNameChange = {},
            onPaidByMemberChange = {},
            onDateChange = {},
            onNoteChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

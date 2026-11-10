package com.messmanager.app.feature.shared_expense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.messmanager.app.R
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.core.util.formatMonthYear
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.feature.shared_expense.components.SharedExpenseFormSheet
import com.messmanager.app.feature.shared_expense.components.SharedExpenseItem
import com.messmanager.app.ui.components.AddFab
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState
import com.messmanager.app.ui.components.MonthPicker
import com.messmanager.app.ui.components.formatMoney
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun SharedExpenseScreen(
    onBack: () -> Unit,
    viewModel: SharedExpenseViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SharedExpenseContent(
        state = state,
        onBack = onBack,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
        onAddClick = viewModel::onAddClick,
        onEditClick = viewModel::onEditClick,
        onDelete = viewModel::onDelete,
        onUndoDelete = viewModel::onUndoDelete,
        onClearUndo = viewModel::onClearUndo,
        onClearSavedMessage = viewModel::onClearSavedMessage,
        onTitleChange = viewModel::onTitleChange,
        onTypeChange = viewModel::onTypeChange,
        onAmountChange = viewModel::onAmountChange,
        onSplitTypeChange = viewModel::onSplitTypeChange,
        onPaidByMemberChange = viewModel::onPaidByMemberChange,
        onDateChange = viewModel::onDateChange,
        onCustomShareChange = viewModel::onCustomShareChange,
        onSave = viewModel::onSave,
        onDismissForm = viewModel::onDismissForm,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedExpenseContent(
    state: SharedExpenseUiState,
    onBack: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (SharedExpense) -> Unit,
    onDelete: (SharedExpense) -> Unit,
    onUndoDelete: () -> Unit,
    onClearUndo: () -> Unit,
    onClearSavedMessage: () -> Unit,
    onTitleChange: (String) -> Unit,
    onTypeChange: (com.messmanager.app.domain.model.SharedExpenseType) -> Unit,
    onAmountChange: (String) -> Unit,
    onSplitTypeChange: (com.messmanager.app.domain.model.SplitType) -> Unit,
    onPaidByMemberChange: (Long?) -> Unit,
    onDateChange: (Long) -> Unit,
    onCustomShareChange: (Long, String) -> Unit,
    onSave: () -> Unit,
    onDismissForm: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val deletedMessage = stringResource(R.string.shared_expense_deleted_msg)
    val savedMessage = stringResource(R.string.msg_shared_expense_saved)
    val undoActionLabel = stringResource(R.string.action_undo)

    LaunchedEffect(state.recentlyDeletedExpense) {
        if (state.recentlyDeletedExpense != null) {
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
        topBar = {
            AppTopBar(
                title = stringResource(R.string.shared_expense_title),
                onBack = onBack,
            )
        },
        floatingActionButton = {
            if (!state.isMonthClosed) {
                AddFab(onClick = onAddClick)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val currentMonth = state.items.firstOrNull()?.epochDay?.let {
                YearMonth.from(LocalDate.ofEpochDay(it))
            } ?: YearMonth.now()

            MonthPicker(
                selectedMonth = currentMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                isClosed = state.isMonthClosed,
            )

            AnimatedVisibility(visible = state.isMonthClosed) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(R.color.closed_month_container))
                        .padding(MaterialTheme.dimens.spaceS),
                ) {
                    Text(
                        text = stringResource(R.string.month_read_only),
                        style = MaterialTheme.typography.labelMedium,
                        color = colorResource(R.color.on_closed_month_container),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }

            when {
                state.isLoading -> LoadingState()
                state.items.isEmpty() -> EmptyState(
                    message = stringResource(R.string.shared_expense_empty),
                    icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                    actionLabel = if (!state.isMonthClosed) stringResource(R.string.shared_expense_add_title) else null,
                    onActionClick = onAddClick,
                )
                else -> {
                    val groupedItems = remember(state.items) {
                        state.items.groupBy { YearMonth.from(LocalDate.ofEpochDay(it.epochDay)) }
                            .toSortedMap(Comparator.reverseOrder())
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = MaterialTheme.dimens.spaceL),
                    ) {
                        groupedItems.forEach { (month, expensesForMonth) ->
                            stickyHeader(key = "header_$month") {
                                MonthHeader(
                                    month = month,
                                    monthTotal = expensesForMonth.sumOf { it.amount },
                                )
                            }
                            items(expensesForMonth, key = { it.id }) { expense ->
                                Box(modifier = Modifier.animateItem()) {
                                    if (!state.isMonthClosed) {
                                        val dismissState = rememberSwipeToDismissBoxState(
                                            confirmValueChange = { dismissValue ->
                                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                                    onDelete(expense)
                                                    true
                                                } else {
                                                    false
                                                }
                                            },
                                        )
                                        SwipeToDismissBox(
                                            state = dismissState,
                                            enableDismissFromStartToEnd = false,
                                            enableDismissFromEndToStart = true,
                                            backgroundContent = {
                                                val color by animateColorAsState(
                                                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                                        MaterialTheme.colorScheme.errorContainer
                                                    } else {
                                                        Color.Transparent
                                                    },
                                                    label = "backgroundColor",
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(color, shape = MaterialTheme.shapes.medium)
                                                        .padding(horizontal = 16.dp),
                                                    contentAlignment = Alignment.CenterEnd,
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Delete,
                                                        contentDescription = stringResource(R.string.content_desc_delete),
                                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                                    )
                                                }
                                            },
                                        ) {
                                            SharedExpenseItem(
                                                expense = expense,
                                                onEdit = { onEditClick(expense) },
                                                onDelete = { onDelete(expense) },
                                                modifier = Modifier.padding(
                                                    horizontal = MaterialTheme.dimens.spaceM,
                                                    vertical = MaterialTheme.dimens.spaceXs,
                                                ),
                                            )
                                        }
                                    } else {
                                        SharedExpenseItem(
                                            expense = expense,
                                            onEdit = {},
                                            onDelete = {},
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
        SharedExpenseFormSheet(
            form = form,
            members = state.members,
            onTitleChange = onTitleChange,
            onTypeChange = onTypeChange,
            onAmountChange = onAmountChange,
            onSplitTypeChange = onSplitTypeChange,
            onPaidByMemberChange = onPaidByMemberChange,
            onDateChange = onDateChange,
            onCustomShareChange = onCustomShareChange,
            onSave = onSave,
            onDismiss = onDismissForm,
        )
    }
}

@Composable
private fun MonthHeader(
    month: YearMonth,
    monthTotal: Double,
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
                text = month.formatMonthYear(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.shared_expense_total_month, formatMoney(monthTotal)),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

package com.messmanager.app.feature.deposit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
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
import com.messmanager.app.core.preview.PreviewData
import com.messmanager.app.core.preview.PreviewSurface
import com.messmanager.app.core.preview.ThemePreviews
import com.messmanager.app.core.theme.dimens
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.feature.deposit.components.AddDepositDialog
import com.messmanager.app.feature.deposit.components.DepositItem
import com.messmanager.app.ui.components.AddFab
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState
import com.messmanager.app.ui.components.MonthPicker

@Composable
fun DepositScreen(viewModel: DepositViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DepositContent(
        state = state,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
        onAddClick = viewModel::onAddClick,
        onDelete = viewModel::onDelete,
        onUndoDelete = viewModel::onUndoDelete,
        onClearUndo = viewModel::onClearUndo,
        onClearSavedMessage = viewModel::onClearSavedMessage,
        onMemberChange = viewModel::onMemberChange,
        onAmountChange = viewModel::onAmountChange,
        onMethodChange = viewModel::onMethodChange,
        onSave = viewModel::onSave,
        onDismissForm = viewModel::onDismissForm,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositContent(
    state: DepositUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddClick: () -> Unit,
    onDelete: (Deposit) -> Unit,
    onUndoDelete: () -> Unit,
    onClearUndo: () -> Unit,
    onClearSavedMessage: () -> Unit,
    onMemberChange: (Member) -> Unit,
    onAmountChange: (String) -> Unit,
    onMethodChange: (PaymentMethod) -> Unit,
    onSave: () -> Unit,
    onDismissForm: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val deletedMessage = stringResource(R.string.msg_deposit_deleted)
    val savedMessage = stringResource(R.string.msg_deposit_saved)
    val undoActionLabel = stringResource(R.string.action_undo)

    LaunchedEffect(state.recentlyDeletedDeposit) {
        if (state.recentlyDeletedDeposit != null) {
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
        topBar = { AppTopBar(title = stringResource(R.string.deposit_title)) },
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
                    message = stringResource(R.string.deposit_empty),
                    icon = Icons.Rounded.AccountBalanceWallet,
                    actionLabel = if (!state.isMonthClosed) stringResource(R.string.deposit_add_title) else null,
                    onActionClick = onAddClick,
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                ) {
                    items(state.items, key = { it.id }) { deposit ->
                        Box(modifier = Modifier.animateItem()) {
                            if (!state.isMonthClosed) {
                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { dismissValue ->
                                        if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                            onDelete(deposit)
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
                                    DepositItem(
                                        deposit = deposit,
                                        onDelete = { onDelete(deposit) },
                                    )
                                }
                            } else {
                                DepositItem(
                                    deposit = deposit,
                                    onDelete = {},
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    state.form?.let { form ->
        AddDepositDialog(
            form = form,
            members = state.members,
            onMemberChange = onMemberChange,
            onAmountChange = onAmountChange,
            onMethodChange = onMethodChange,
            onSave = onSave,
            onDismiss = onDismissForm,
        )
    }
}

@ThemePreviews
@Composable
private fun DepositScreenNormalPreview() {
    PreviewSurface {
        DepositContent(
            state = PreviewData.depositNormal,
            onPreviousMonth = {},
            onNextMonth = {},
            onAddClick = {},
            onDelete = {},
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
            onMemberChange = {},
            onAmountChange = {},
            onMethodChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

@ThemePreviews
@Composable
private fun DepositScreenEmptyPreview() {
    PreviewSurface {
        DepositContent(
            state = PreviewData.depositEmpty,
            onPreviousMonth = {},
            onNextMonth = {},
            onAddClick = {},
            onDelete = {},
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
            onMemberChange = {},
            onAmountChange = {},
            onMethodChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

@ThemePreviews
@Composable
private fun DepositScreenLoadingPreview() {
    PreviewSurface {
        DepositContent(
            state = PreviewData.depositLoading,
            onPreviousMonth = {},
            onNextMonth = {},
            onAddClick = {},
            onDelete = {},
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
            onMemberChange = {},
            onAmountChange = {},
            onMethodChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

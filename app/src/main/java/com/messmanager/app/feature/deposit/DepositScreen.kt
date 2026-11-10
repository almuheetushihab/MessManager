package com.messmanager.app.feature.deposit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
        onMemberChange = viewModel::onMemberChange,
        onAmountChange = viewModel::onAmountChange,
        onMethodChange = viewModel::onMethodChange,
        onSave = viewModel::onSave,
        onDismissForm = viewModel::onDismissForm,
    )
}

@Composable
fun DepositContent(
    state: DepositUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddClick: () -> Unit,
    onDelete: (Deposit) -> Unit,
    onMemberChange: (Member) -> Unit,
    onAmountChange: (String) -> Unit,
    onMethodChange: (PaymentMethod) -> Unit,
    onSave: () -> Unit,
    onDismissForm: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = stringResource(R.string.deposit_title)) },
        floatingActionButton = {
            if (!state.isMonthClosed) {
                AddFab(onClick = onAddClick)
            }
        },
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

            if (state.isMonthClosed) {
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
                )
                else -> LazyColumn(
                    contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                ) {
                    items(state.items, key = { it.id }) { deposit ->
                        DepositItem(
                            deposit = deposit,
                            onDelete = { if (!state.isMonthClosed) onDelete(deposit) },
                        )
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
            onMemberChange = {},
            onAmountChange = {},
            onMethodChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

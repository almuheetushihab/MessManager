package com.messmanager.app.feature.deposit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.feature.deposit.components.AddDepositDialog
import com.messmanager.app.feature.deposit.components.DepositItem
import com.messmanager.app.ui.components.AddFab
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState

@Composable
fun DepositScreen(viewModel: DepositViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DepositContent(
        state = state,
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
        floatingActionButton = { AddFab(onClick = onAddClick) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.items.isEmpty() -> EmptyState(
                message = stringResource(R.string.deposit_empty),
                icon = Icons.Rounded.AccountBalanceWallet,
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            ) {
                items(state.items, key = { it.id }) { deposit ->
                    DepositItem(deposit = deposit, onDelete = { onDelete(deposit) })
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
private fun DepositScreenFormPreview() {
    PreviewSurface {
        DepositContent(
            state = PreviewData.depositForm,
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
private fun DepositScreenErrorPreview() {
    PreviewSurface {
        DepositContent(
            state = PreviewData.depositError,
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

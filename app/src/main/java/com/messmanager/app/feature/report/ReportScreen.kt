package com.messmanager.app.feature.report

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
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.messmanager.app.core.util.formatMonthYear
import com.messmanager.app.feature.report.components.MemberBalanceCard
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.ConfirmDialog
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState
import com.messmanager.app.ui.components.MonthPicker
import com.messmanager.app.ui.components.formatMoney

@Composable
fun ReportScreen(viewModel: ReportViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ReportContent(
        state = state,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
        onRequestCloseMonth = viewModel::onRequestCloseMonth,
        onRequestReopenMonth = viewModel::onRequestReopenMonth,
        onConfirmCloseMonth = viewModel::onConfirmCloseMonth,
        onConfirmReopenMonth = viewModel::onConfirmReopenMonth,
        onDismissDialog = viewModel::onDismissDialog,
    )
}

@Composable
fun ReportContent(
    state: ReportUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onRequestCloseMonth: () -> Unit,
    onRequestReopenMonth: () -> Unit,
    onConfirmCloseMonth: () -> Unit,
    onConfirmReopenMonth: () -> Unit,
    onDismissDialog: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = stringResource(R.string.report_title)) },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            MonthPicker(
                selectedMonth = state.summary.month,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                isClosed = state.summary.isClosed,
            )

            if (state.summary.isClosed) {
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
                state.summary.members.isEmpty() -> EmptyState(
                    message = stringResource(R.string.report_empty),
                    icon = Icons.Rounded.Assessment,
                )
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                    ) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS)) {
                                if (state.summary.totalOutstandingBaki > 0.0) {
                                    AppCard {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = stringResource(R.string.bazar_outstanding_baki),
                                                style = MaterialTheme.typography.titleMedium,
                                            )
                                            Text(
                                                text = formatMoney(state.summary.totalOutstandingBaki),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.error,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        items(state.summary.members, key = { it.member.id }) { summary ->
                            MemberBalanceCard(summary = summary)
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = MaterialTheme.dimens.spaceM),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (state.summary.isClosed) {
                                    OutlinedButton(onClick = onRequestReopenMonth) {
                                        Text(stringResource(R.string.action_reopen_month))
                                    }
                                } else {
                                    Button(
                                        onClick = onRequestCloseMonth,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError,
                                        ),
                                    ) {
                                        Text(stringResource(R.string.action_close_month))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showCloseDialog) {
        ConfirmDialog(
            title = stringResource(R.string.month_close_title),
            message = stringResource(R.string.month_close_message, state.summary.month.formatMonthYear()),
            confirmText = stringResource(R.string.action_close_month),
            onConfirm = onConfirmCloseMonth,
            onDismiss = onDismissDialog,
        )
    }

    if (state.showReopenDialog) {
        ConfirmDialog(
            title = stringResource(R.string.month_reopen_title),
            message = stringResource(R.string.month_reopen_message, state.summary.month.formatMonthYear()),
            confirmText = stringResource(R.string.action_reopen_month),
            onConfirm = onConfirmReopenMonth,
            onDismiss = onDismissDialog,
        )
    }
}

@ThemePreviews
@Composable
private fun ReportScreenNormalPreview() {
    PreviewSurface {
        ReportContent(
            state = PreviewData.reportNormal,
            onPreviousMonth = {},
            onNextMonth = {},
            onRequestCloseMonth = {},
            onRequestReopenMonth = {},
            onConfirmCloseMonth = {},
            onConfirmReopenMonth = {},
            onDismissDialog = {},
        )
    }
}

@ThemePreviews
@Composable
private fun ReportScreenEmptyPreview() {
    PreviewSurface {
        ReportContent(
            state = PreviewData.reportEmpty,
            onPreviousMonth = {},
            onNextMonth = {},
            onRequestCloseMonth = {},
            onRequestReopenMonth = {},
            onConfirmCloseMonth = {},
            onConfirmReopenMonth = {},
            onDismissDialog = {},
        )
    }
}

@ThemePreviews
@Composable
private fun ReportScreenLoadingPreview() {
    PreviewSurface {
        ReportContent(
            state = PreviewData.reportLoading,
            onPreviousMonth = {},
            onNextMonth = {},
            onRequestCloseMonth = {},
            onRequestReopenMonth = {},
            onConfirmCloseMonth = {},
            onConfirmReopenMonth = {},
            onDismissDialog = {},
        )
    }
}

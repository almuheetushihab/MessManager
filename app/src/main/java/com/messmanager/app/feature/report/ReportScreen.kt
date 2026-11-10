package com.messmanager.app.feature.report

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.messmanager.app.domain.model.MessSummary
import com.messmanager.app.feature.report.components.MemberBalanceCard
import com.messmanager.app.ui.components.AppCard
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.ConfirmDialog
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState
import com.messmanager.app.ui.components.MonthPicker
import com.messmanager.app.ui.components.StatCard
import com.messmanager.app.ui.components.formatMoney

@Composable
fun ReportScreen(
    onOpenMembers: () -> Unit = {},
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ReportContent(
        state = state,
        onOpenMembers = onOpenMembers,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
        onSortOrderChange = viewModel::onSortOrderChange,
        onRequestCloseMonth = viewModel::onRequestCloseMonth,
        onRequestReopenMonth = viewModel::onRequestReopenMonth,
        onConfirmCloseMonth = viewModel::onConfirmCloseMonth,
        onConfirmReopenMonth = viewModel::onConfirmReopenMonth,
        onDismissDialog = viewModel::onDismissDialog,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportContent(
    state: ReportUiState,
    onOpenMembers: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSortOrderChange: (ReportSortOrder) -> Unit,
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

            AnimatedVisibility(visible = state.summary.isClosed) {
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
                    actionLabel = stringResource(R.string.action_add_first_member),
                    onActionClick = onOpenMembers,
                )
                else -> {
                    val sortedMembers = remember(state.summary.members, state.sortOrder) {
                        when (state.sortOrder) {
                            ReportSortOrder.NAME -> state.summary.members.sortedBy { it.member.name.lowercase() }
                            ReportSortOrder.HIGHEST_DUE -> state.summary.members.sortedBy { it.balance }
                            ReportSortOrder.HIGHEST_REFUND -> state.summary.members.sortedByDescending { it.balance }
                        }
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                    ) {
                        item {
                            ReportSummaryHeader(summary = state.summary)
                        }

                        item {
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = MaterialTheme.dimens.spaceXs),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                            ) {
                                FilterChip(
                                    selected = state.sortOrder == ReportSortOrder.NAME,
                                    onClick = { onSortOrderChange(ReportSortOrder.NAME) },
                                    label = { Text(stringResource(R.string.report_sort_name)) },
                                )
                                FilterChip(
                                    selected = state.sortOrder == ReportSortOrder.HIGHEST_DUE,
                                    onClick = { onSortOrderChange(ReportSortOrder.HIGHEST_DUE) },
                                    label = { Text(stringResource(R.string.report_sort_highest_due)) },
                                )
                                FilterChip(
                                    selected = state.sortOrder == ReportSortOrder.HIGHEST_REFUND,
                                    onClick = { onSortOrderChange(ReportSortOrder.HIGHEST_REFUND) },
                                    label = { Text(stringResource(R.string.report_sort_highest_refund)) },
                                )
                            }
                        }

                        items(sortedMembers, key = { it.member.id }) { summary ->
                            Box(modifier = Modifier.animateItem()) {
                                MemberBalanceCard(summary = summary)
                            }
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

@Composable
private fun ReportSummaryHeader(
    summary: MessSummary,
    modifier: Modifier = Modifier,
) {
    val totalDeposit = remember(summary.members) { summary.members.sumOf { it.deposit } }
    val gap = MaterialTheme.dimens.spaceM

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            StatCard(
                title = stringResource(R.string.home_total_bazar),
                value = formatMoney(summary.totalBazar),
                icon = Icons.Rounded.LocalGroceryStore,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            StatCard(
                title = stringResource(R.string.report_total_deposit),
                value = formatMoney(totalDeposit),
                icon = Icons.Rounded.AccountBalanceWallet,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            StatCard(
                title = stringResource(R.string.home_meal_rate),
                value = formatMoney(summary.mealRate),
                icon = Icons.Rounded.AttachMoney,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            StatCard(
                title = stringResource(R.string.home_total_meals),
                value = summary.totalMeals.toString(),
                icon = Icons.Rounded.Restaurant,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
        if (summary.totalOutstandingBaki > 0.0) {
            AppCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        StatCard(
                            title = stringResource(R.string.bazar_outstanding_baki),
                            value = formatMoney(summary.totalOutstandingBaki),
                            icon = Icons.Rounded.CreditCard,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ReportScreenNormalPreview() {
    PreviewSurface {
        ReportContent(
            state = PreviewData.reportNormal,
            onOpenMembers = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onSortOrderChange = {},
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
            onOpenMembers = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onSortOrderChange = {},
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
            onOpenMembers = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onSortOrderChange = {},
            onRequestCloseMonth = {},
            onRequestReopenMonth = {},
            onConfirmCloseMonth = {},
            onConfirmReopenMonth = {},
            onDismissDialog = {},
        )
    }
}

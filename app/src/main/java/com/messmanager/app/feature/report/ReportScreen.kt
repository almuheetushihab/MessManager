package com.messmanager.app.feature.report

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.messmanager.app.feature.report.export.ReportExporter
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
    onNavigateToSharedExpense: () -> Unit = {},
    viewModel: ReportViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
    ) { uri ->
        if (uri != null) {
            val success = ReportExporter.exportCsvToUri(context, uri, state.reportData)
            val msg = if (success) R.string.report_export_success else R.string.report_export_failed
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    val exportPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
    ) { uri ->
        if (uri != null) {
            val success = ReportExporter.exportPdfToUri(context, uri, state.summary)
            val msg = if (success) R.string.report_export_success else R.string.report_export_failed
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    ReportContent(
        state = state,
        onOpenMembers = onOpenMembers,
        onNavigateToSharedExpense = onNavigateToSharedExpense,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
        onSortOrderChange = viewModel::onSortOrderChange,
        onRequestCloseMonth = viewModel::onRequestCloseMonth,
        onRequestReopenMonth = viewModel::onRequestReopenMonth,
        onConfirmCloseMonth = viewModel::onConfirmCloseMonth,
        onConfirmReopenMonth = viewModel::onConfirmReopenMonth,
        onDismissDialog = viewModel::onDismissDialog,
        onExportCsv = { exportCsvLauncher.launch("Mess_Report_${state.summary.month}.csv") },
        onShareCsv = { ReportExporter.shareCsvFile(context, state.reportData) },
        onExportPdf = { exportPdfLauncher.launch("Mess_Report_${state.summary.month}.pdf") },
        onSharePdf = { ReportExporter.sharePdfFile(context, state.summary) },
        onShareTextSummary = { ReportExporter.shareTextSummary(context, state.summary) },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportContent(
    state: ReportUiState,
    onOpenMembers: () -> Unit,
    onNavigateToSharedExpense: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSortOrderChange: (ReportSortOrder) -> Unit,
    onRequestCloseMonth: () -> Unit,
    onRequestReopenMonth: () -> Unit,
    onConfirmCloseMonth: () -> Unit,
    onConfirmReopenMonth: () -> Unit,
    onDismissDialog: () -> Unit,
    onExportCsv: () -> Unit = {},
    onShareCsv: () -> Unit = {},
    onExportPdf: () -> Unit = {},
    onSharePdf: () -> Unit = {},
    onShareTextSummary: () -> Unit = {},
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.report_title),
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = stringResource(R.string.report_export_options),
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.report_share_summary)) },
                                onClick = {
                                    menuExpanded = false
                                    onShareTextSummary()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Share, contentDescription = null)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.report_export_csv)) },
                                onClick = {
                                    menuExpanded = false
                                    onExportCsv()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.TableChart, contentDescription = null)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.report_share_csv)) },
                                onClick = {
                                    menuExpanded = false
                                    onShareCsv()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Share, contentDescription = null)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.report_export_pdf)) },
                                onClick = {
                                    menuExpanded = false
                                    onExportPdf()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.PictureAsPdf, contentDescription = null)
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.report_share_pdf)) },
                                onClick = {
                                    menuExpanded = false
                                    onSharePdf()
                                },
                                leadingIcon = {
                                    Icon(Icons.Rounded.Share, contentDescription = null)
                                },
                            )
                        }
                    }
                },
            )
        },
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
                            ReportSummaryHeader(
                                summary = state.summary,
                                onNavigateToSharedExpense = onNavigateToSharedExpense,
                                onShareSummary = onShareTextSummary,
                            )
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
    onNavigateToSharedExpense: () -> Unit,
    onShareSummary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalDeposit = remember(summary.members) { summary.members.sumOf { it.deposit } }
    val gap = MaterialTheme.dimens.spaceM

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
        OutlinedButton(
            onClick = onShareSummary,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = null,
                modifier = Modifier.padding(end = MaterialTheme.dimens.spaceS),
            )
            Text(stringResource(R.string.report_share_summary))
        }

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
        Row(
            modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(gap),
        ) {
            StatCard(
                title = stringResource(R.string.report_total_shared_bills),
                value = formatMoney(summary.totalSharedExpenses),
                icon = Icons.AutoMirrored.Rounded.ReceiptLong,
                modifier = Modifier.weight(1f).fillMaxHeight().clickable(onClick = onNavigateToSharedExpense),
            )
            if (summary.totalOutstandingBaki > 0.0) {
                StatCard(
                    title = stringResource(R.string.bazar_outstanding_baki),
                    value = formatMoney(summary.totalOutstandingBaki),
                    icon = Icons.Rounded.CreditCard,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                )
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
            onNavigateToSharedExpense = {},
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
            onNavigateToSharedExpense = {},
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
            onNavigateToSharedExpense = {},
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

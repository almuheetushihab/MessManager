package com.messmanager.app.feature.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
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
import com.messmanager.app.core.util.formatMonthYear
import com.messmanager.app.feature.report.components.MemberBalanceCard
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState

@Composable
fun ReportScreen(viewModel: ReportViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ReportContent(state = state)
}

@Composable
fun ReportContent(state: ReportUiState) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = stringResource(R.string.report_title)) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.summary.members.isEmpty() -> EmptyState(
                message = stringResource(R.string.report_empty),
                icon = Icons.Rounded.Assessment,
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            ) {
                item {
                    Text(
                        text = state.summary.month.formatMonthYear(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                items(state.summary.members, key = { it.member.id }) { summary ->
                    MemberBalanceCard(summary = summary)
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ReportScreenNormalPreview() {
    PreviewSurface {
        ReportContent(state = PreviewData.reportNormal)
    }
}

@ThemePreviews
@Composable
private fun ReportScreenEmptyPreview() {
    PreviewSurface {
        ReportContent(state = PreviewData.reportEmpty)
    }
}

@ThemePreviews
@Composable
private fun ReportScreenLoadingPreview() {
    PreviewSurface {
        ReportContent(state = PreviewData.reportLoading)
    }
}

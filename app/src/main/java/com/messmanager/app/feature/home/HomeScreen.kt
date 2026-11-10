package com.messmanager.app.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.messmanager.app.feature.home.components.StatGrid
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.LoadingState
import com.messmanager.app.ui.components.MonthPicker

@Composable
fun HomeScreen(
    onOpenMembers: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        state = state,
        onOpenMembers = onOpenMembers,
        onPreviousMonth = viewModel::onPreviousMonth,
        onNextMonth = viewModel::onNextMonth,
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onOpenMembers: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = stringResource(R.string.home_title),
                actions = {
                    IconButton(onClick = onOpenMembers) {
                        Icon(
                            imageVector = Icons.Rounded.Group,
                            contentDescription = stringResource(R.string.members_title),
                        )
                    }
                },
            )
        },
    ) { padding ->
        if (state.isLoading) {
            LoadingState(Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(MaterialTheme.dimens.spaceM),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceM),
            ) {
                MonthPicker(
                    selectedMonth = state.summary.month,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    isClosed = state.summary.isClosed,
                )
                StatGrid(state = state)
                Text(
                    text = stringResource(R.string.home_members_count, state.summary.members.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun HomeScreenNormalPreview() {
    PreviewSurface {
        HomeContent(
            state = PreviewData.homeNormal,
            onOpenMembers = {},
            onPreviousMonth = {},
            onNextMonth = {},
        )
    }
}

@ThemePreviews
@Composable
private fun HomeScreenLoadingPreview() {
    PreviewSurface {
        HomeContent(
            state = PreviewData.homeLoading,
            onOpenMembers = {},
            onPreviousMonth = {},
            onNextMonth = {},
        )
    }
}

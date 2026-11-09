package com.messmanager.app.feature.bazar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocalGroceryStore
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
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.feature.bazar.components.AddBazarDialog
import com.messmanager.app.feature.bazar.components.BazarItem
import com.messmanager.app.ui.components.AddFab
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState

@Composable
fun BazarScreen(viewModel: BazarViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BazarContent(
        state = state,
        onAddClick = viewModel::onAddClick,
        onDelete = viewModel::onDelete,
        onAmountChange = viewModel::onAmountChange,
        onNoteChange = viewModel::onNoteChange,
        onCategoryChange = viewModel::onCategoryChange,
        onSave = viewModel::onSave,
        onDismissForm = viewModel::onDismissForm,
    )
}

@Composable
fun BazarContent(
    state: BazarUiState,
    onAddClick: () -> Unit,
    onDelete: (Bazar) -> Unit,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onCategoryChange: (BazarCategory) -> Unit,
    onSave: () -> Unit,
    onDismissForm: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = stringResource(R.string.bazar_title)) },
        floatingActionButton = { AddFab(onClick = onAddClick) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.items.isEmpty() -> EmptyState(
                message = stringResource(R.string.bazar_empty),
                icon = Icons.Rounded.LocalGroceryStore,
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            ) {
                items(state.items, key = { it.id }) { bazar ->
                    BazarItem(bazar = bazar, onDelete = { onDelete(bazar) })
                }
            }
        }
    }

    state.form?.let { form ->
        AddBazarDialog(
            form = form,
            onAmountChange = onAmountChange,
            onNoteChange = onNoteChange,
            onCategoryChange = onCategoryChange,
            onSave = onSave,
            onDismiss = onDismissForm,
        )
    }
}

@ThemePreviews
@Composable
private fun BazarScreenNormalPreview() {
    PreviewSurface {
        BazarContent(
            state = PreviewData.bazarNormal,
            onAddClick = {},
            onDelete = {},
            onAmountChange = {},
            onNoteChange = {},
            onCategoryChange = {},
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
            onAddClick = {},
            onDelete = {},
            onAmountChange = {},
            onNoteChange = {},
            onCategoryChange = {},
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
            onAddClick = {},
            onDelete = {},
            onAmountChange = {},
            onNoteChange = {},
            onCategoryChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

@ThemePreviews
@Composable
private fun BazarScreenFormPreview() {
    PreviewSurface {
        BazarContent(
            state = PreviewData.bazarForm,
            onAddClick = {},
            onDelete = {},
            onAmountChange = {},
            onNoteChange = {},
            onCategoryChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

@ThemePreviews
@Composable
private fun BazarScreenErrorPreview() {
    PreviewSurface {
        BazarContent(
            state = PreviewData.bazarError,
            onAddClick = {},
            onDelete = {},
            onAmountChange = {},
            onNoteChange = {},
            onCategoryChange = {},
            onSave = {},
            onDismissForm = {},
        )
    }
}

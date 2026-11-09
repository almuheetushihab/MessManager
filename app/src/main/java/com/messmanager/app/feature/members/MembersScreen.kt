package com.messmanager.app.feature.members

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
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
import com.messmanager.app.domain.model.Member
import com.messmanager.app.feature.members.components.AddMemberDialog
import com.messmanager.app.feature.members.components.MemberItem
import com.messmanager.app.ui.components.AddFab
import com.messmanager.app.ui.components.AppTopBar
import com.messmanager.app.ui.components.ConfirmDialog
import com.messmanager.app.ui.components.EmptyState
import com.messmanager.app.ui.components.LoadingState

@Composable
fun MembersScreen(
    onBack: () -> Unit,
    viewModel: MembersViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MembersContent(
        state = state,
        onBack = onBack,
        onAddClick = viewModel::onAddClick,
        onDeleteClick = viewModel::onDeleteClick,
        onNameChange = viewModel::onNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onSave = viewModel::onSave,
        onDismissForm = viewModel::onDismissForm,
        onConfirmDelete = viewModel::onConfirmDelete,
        onDismissDelete = viewModel::onDismissDelete,
    )
}

@Composable
fun MembersContent(
    state: MembersUiState,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: (Member) -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismissForm: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { AppTopBar(title = stringResource(R.string.members_title), onBack = onBack) },
        floatingActionButton = { AddFab(onClick = onAddClick) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.members.isEmpty() -> EmptyState(
                message = stringResource(R.string.members_empty),
                icon = Icons.Rounded.Group,
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            ) {
                items(state.members, key = { it.id }) { member ->
                    MemberItem(member = member, onDelete = { onDeleteClick(member) })
                }
            }
        }
    }

    state.form?.let { form ->
        AddMemberDialog(
            form = form,
            onNameChange = onNameChange,
            onPhoneChange = onPhoneChange,
            onSave = onSave,
            onDismiss = onDismissForm,
        )
    }

    state.memberToDelete?.let { member ->
        ConfirmDialog(
            title = stringResource(R.string.members_delete_title),
            message = stringResource(R.string.members_delete_message, member.name),
            onConfirm = onConfirmDelete,
            onDismiss = onDismissDelete,
        )
    }
}

@ThemePreviews
@Composable
private fun MembersScreenNormalPreview() {
    PreviewSurface {
        MembersContent(
            state = PreviewData.membersNormal,
            onBack = {},
            onAddClick = {},
            onDeleteClick = {},
            onNameChange = {},
            onPhoneChange = {},
            onSave = {},
            onDismissForm = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

@ThemePreviews
@Composable
private fun MembersScreenEmptyPreview() {
    PreviewSurface {
        MembersContent(
            state = PreviewData.membersEmpty,
            onBack = {},
            onAddClick = {},
            onDeleteClick = {},
            onNameChange = {},
            onPhoneChange = {},
            onSave = {},
            onDismissForm = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

@ThemePreviews
@Composable
private fun MembersScreenLoadingPreview() {
    PreviewSurface {
        MembersContent(
            state = PreviewData.membersLoading,
            onBack = {},
            onAddClick = {},
            onDeleteClick = {},
            onNameChange = {},
            onPhoneChange = {},
            onSave = {},
            onDismissForm = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

@ThemePreviews
@Composable
private fun MembersScreenFormPreview() {
    PreviewSurface {
        MembersContent(
            state = PreviewData.membersForm,
            onBack = {},
            onAddClick = {},
            onDeleteClick = {},
            onNameChange = {},
            onPhoneChange = {},
            onSave = {},
            onDismissForm = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

@ThemePreviews
@Composable
private fun MembersScreenDeletePreview() {
    PreviewSurface {
        MembersContent(
            state = PreviewData.membersDelete,
            onBack = {},
            onAddClick = {},
            onDeleteClick = {},
            onNameChange = {},
            onPhoneChange = {},
            onSave = {},
            onDismissForm = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

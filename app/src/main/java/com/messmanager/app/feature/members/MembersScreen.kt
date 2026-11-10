package com.messmanager.app.feature.members

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
        onUndoDelete = viewModel::onUndoDelete,
        onClearUndo = viewModel::onClearUndo,
        onClearSavedMessage = viewModel::onClearSavedMessage,
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
    onUndoDelete: () -> Unit,
    onClearUndo: () -> Unit,
    onClearSavedMessage: () -> Unit,
    onNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismissForm: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val deletedMessage = stringResource(R.string.msg_member_deleted)
    val savedMessage = stringResource(R.string.msg_member_saved)
    val undoActionLabel = stringResource(R.string.action_undo)

    LaunchedEffect(state.recentlyDeletedMember) {
        if (state.recentlyDeletedMember != null) {
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
        topBar = { AppTopBar(title = stringResource(R.string.members_title), onBack = onBack) },
        floatingActionButton = { AddFab(onClick = onAddClick) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { padding ->
        when {
            state.isLoading -> LoadingState(Modifier.padding(padding))
            state.members.isEmpty() -> EmptyState(
                message = stringResource(R.string.members_empty),
                icon = Icons.Rounded.Group,
                actionLabel = stringResource(R.string.action_add_first_member),
                onActionClick = onAddClick,
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(MaterialTheme.dimens.spaceM),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.spaceS),
            ) {
                items(state.members, key = { it.id }) { member ->
                    Box(modifier = Modifier.animateItem()) {
                        MemberItem(member = member, onDelete = { onDeleteClick(member) })
                    }
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
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
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
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
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
            onUndoDelete = {},
            onClearUndo = {},
            onClearSavedMessage = {},
            onNameChange = {},
            onPhoneChange = {},
            onSave = {},
            onDismissForm = {},
            onConfirmDelete = {},
            onDismissDelete = {},
        )
    }
}

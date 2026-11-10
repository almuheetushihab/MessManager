package com.messmanager.app.feature.members

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.R
import com.messmanager.app.core.util.UiText
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.repository.MessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MembersViewModel @Inject constructor(
    private val repository: MessRepository,
) : ViewModel() {

    private val form = MutableStateFlow<MemberFormState?>(null)
    private val memberToDelete = MutableStateFlow<Member?>(null)
    private val recentlyDeleted = MutableStateFlow<Member?>(null)
    private val showSavedMsg = MutableStateFlow(false)

    val uiState: StateFlow<MembersUiState> = combine(
        repository.observeMembers(),
        form,
        memberToDelete,
        recentlyDeleted,
        showSavedMsg,
    ) { members, form, toDelete, deleted, savedMsg ->
        MembersUiState(
            isLoading = false,
            members = members,
            form = form,
            memberToDelete = toDelete,
            recentlyDeletedMember = deleted,
            showSavedMessage = savedMsg,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), MembersUiState())

    fun onAddClick() = form.update { MemberFormState() }
    fun onDismissForm() = form.update { null }
    fun onNameChange(value: String) = form.update { it?.copy(name = value, nameError = null) }
    fun onPhoneChange(value: String) = form.update { it?.copy(phone = value) }

    fun onSave() {
        val current = form.value ?: return
        if (current.name.isBlank()) {
            form.update { it?.copy(nameError = UiText.Resource(R.string.error_name_required)) }
            return
        }
        viewModelScope.launch {
            repository.addMember(Member(name = current.name.trim(), phone = current.phone.trim()))
            form.update { null }
            showSavedMsg.value = true
        }
    }

    fun onDeleteClick(member: Member) = memberToDelete.update { member }
    fun onDismissDelete() = memberToDelete.update { null }

    fun onConfirmDelete() {
        val member = memberToDelete.value ?: return
        recentlyDeleted.value = member
        viewModelScope.launch {
            repository.deleteMember(member.id)
            memberToDelete.update { null }
        }
    }

    fun onUndoDelete() {
        val memberToRestore = recentlyDeleted.value ?: return
        viewModelScope.launch {
            repository.addMember(memberToRestore)
            recentlyDeleted.value = null
        }
    }

    fun onClearUndo() {
        recentlyDeleted.value = null
    }

    fun onClearSavedMessage() {
        showSavedMsg.value = false
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

package com.messmanager.app.feature.members

import com.messmanager.app.core.util.UiText
import com.messmanager.app.domain.model.Member

data class MemberFormState(
    val name: String = "",
    val phone: String = "",
    val nameError: UiText? = null,
)

data class MembersUiState(
    val isLoading: Boolean = true,
    val members: List<Member> = emptyList(),
    val form: MemberFormState? = null,
    val memberToDelete: Member? = null,
    val recentlyDeletedMember: Member? = null,
    val showSavedMessage: Boolean = false,
)

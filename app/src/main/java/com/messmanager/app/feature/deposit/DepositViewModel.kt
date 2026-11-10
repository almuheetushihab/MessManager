package com.messmanager.app.feature.deposit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.R
import com.messmanager.app.core.util.UiText
import com.messmanager.app.core.util.epochRange
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.domain.repository.MessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DepositViewModel @Inject constructor(
    private val repository: MessRepository,
) : ViewModel() {

    private val form = MutableStateFlow<DepositFormState?>(null)

    val uiState: StateFlow<DepositUiState> = repository.selectedMonth.flatMapLatest { month ->
        val range = month.epochRange()
        combine(
            repository.observeDeposits().map { list -> list.filter { it.epochDay in range } },
            repository.observeMembers(),
            repository.observeIsMonthClosed(month),
            form,
        ) { deposits, members, isClosed, form ->
            DepositUiState(
                isLoading = false,
                items = deposits,
                members = members,
                isMonthClosed = isClosed,
                form = form,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), DepositUiState())

    fun onPreviousMonth() {
        repository.setSelectedMonth(repository.selectedMonth.value.minusMonths(1))
    }

    fun onNextMonth() {
        repository.setSelectedMonth(repository.selectedMonth.value.plusMonths(1))
    }

    fun onAddClick() {
        if (uiState.value.isMonthClosed) return
        form.update { DepositFormState() }
    }

    fun onDismissForm() = form.update { null }

    fun onMemberChange(member: Member) = form.update { it?.copy(member = member, memberError = null) }

    fun onAmountChange(value: String) = form.update { it?.copy(amount = value, amountError = null) }

    fun onMethodChange(method: PaymentMethod) = form.update { it?.copy(method = method) }

    fun onSave() {
        if (uiState.value.isMonthClosed) return
        val current = form.value ?: return
        val amount = current.amount.toDoubleOrNull()
        val member = current.member
        val memberError = if (member == null) UiText.Resource(R.string.error_member_required) else null
        val amountError = if (amount == null || amount <= 0.0) UiText.Resource(R.string.error_invalid_amount) else null
        if (member == null || amount == null || memberError != null || amountError != null) {
            form.update { it?.copy(memberError = memberError, amountError = amountError) }
            return
        }
        viewModelScope.launch {
            repository.addDeposit(
                Deposit(
                    memberId = member.id,
                    epochDay = repository.selectedMonth.value.atDay(1).toEpochDay(),
                    amount = amount,
                    method = current.method,
                ),
            )
            form.update { null }
        }
    }

    fun onDelete(deposit: Deposit) {
        if (uiState.value.isMonthClosed) return
        viewModelScope.launch { repository.deleteDeposit(deposit.id) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

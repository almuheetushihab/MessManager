package com.messmanager.app.feature.shared_expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.core.util.epochRange
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.domain.model.SharedExpenseShare
import com.messmanager.app.domain.model.SharedExpenseType
import com.messmanager.app.domain.model.SplitType
import com.messmanager.app.domain.repository.MessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SharedExpenseViewModel @Inject constructor(
    private val repository: MessRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SharedExpenseUiState(isLoading = true))
    val uiState: StateFlow<SharedExpenseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.selectedMonth.flatMapLatest { month ->
                    val range = month.epochRange()
                    repository.observeSharedExpensesBetween(range.first, range.last)
                },
                repository.observeMembers(),
                repository.selectedMonth.flatMapLatest { month ->
                    repository.observeIsMonthClosed(month)
                },
            ) { expenses, members, isClosed ->
                _uiState.update { state ->
                    state.copy(
                        items = expenses,
                        members = members,
                        isMonthClosed = isClosed,
                        isLoading = false,
                    )
                }
            }.collect {}
        }
    }

    fun onPreviousMonth() {
        val current = repository.selectedMonth.value
        repository.setSelectedMonth(current.minusMonths(1))
    }

    fun onNextMonth() {
        val current = repository.selectedMonth.value
        repository.setSelectedMonth(current.plusMonths(1))
    }

    fun onAddClick() {
        val members = uiState.value.members
        val initialShares = members.map { member ->
            CustomShareInput(memberId = member.id, memberName = member.name, amountText = "")
        }
        _uiState.update {
            it.copy(
                form = SharedExpenseFormState(
                    epochDay = LocalDate.now().toEpochDay(),
                    customShares = initialShares,
                ),
            )
        }
    }

    fun onEditClick(expense: SharedExpense) {
        val members = uiState.value.members
        val sharesMap = expense.shares.associate { it.memberId to it.shareAmount }
        val customShares = members.map { member ->
            val share = sharesMap[member.id]
            CustomShareInput(
                memberId = member.id,
                memberName = member.name,
                amountText = if (share != null && share > 0) {
                    if (share % 1.0 == 0.0) share.toLong().toString() else share.toString()
                } else "",
            )
        }
        _uiState.update {
            it.copy(
                form = SharedExpenseFormState(
                    id = expense.id,
                    epochDay = expense.epochDay,
                    title = expense.title,
                    type = expense.type,
                    amountText = if (expense.amount % 1.0 == 0.0) expense.amount.toLong().toString() else expense.amount.toString(),
                    splitType = expense.splitType,
                    paidByMemberId = expense.paidByMemberId,
                    customShares = customShares,
                ),
            )
        }
    }

    fun onDelete(expense: SharedExpense) {
        viewModelScope.launch {
            repository.deleteSharedExpense(expense.id)
            _uiState.update { it.copy(recentlyDeletedExpense = expense) }
        }
    }

    fun onUndoDelete() {
        val expenseToRestore = uiState.value.recentlyDeletedExpense ?: return
        viewModelScope.launch {
            repository.addSharedExpense(expenseToRestore)
            _uiState.update { it.copy(recentlyDeletedExpense = null) }
        }
    }

    fun onClearUndo() {
        _uiState.update { it.copy(recentlyDeletedExpense = null) }
    }

    fun onClearSavedMessage() {
        _uiState.update { it.copy(showSavedMessage = false) }
    }

    fun onTitleChange(title: String) {
        _uiState.update { state ->
            state.copy(form = state.form?.copy(title = title, titleError = null))
        }
    }

    fun onTypeChange(type: SharedExpenseType) {
        _uiState.update { state ->
            state.copy(form = state.form?.copy(type = type))
        }
    }

    fun onAmountChange(amountText: String) {
        _uiState.update { state ->
            val form = state.form ?: return@update state
            val newForm = form.copy(amountText = amountText, amountError = null, sharesError = null)
            val updatedForm = if (newForm.splitType == SplitType.EQUAL) {
                recalculateEqualShares(newForm, state.members)
            } else {
                newForm
            }
            state.copy(form = updatedForm)
        }
    }

    fun onSplitTypeChange(splitType: SplitType) {
        _uiState.update { state ->
            val form = state.form ?: return@update state
            val newForm = form.copy(splitType = splitType, sharesError = null)
            val updatedForm = if (splitType == SplitType.EQUAL) {
                recalculateEqualShares(newForm, state.members)
            } else {
                newForm
            }
            state.copy(form = updatedForm)
        }
    }

    fun onPaidByMemberChange(memberId: Long?) {
        _uiState.update { state ->
            state.copy(form = state.form?.copy(paidByMemberId = memberId))
        }
    }

    fun onDateChange(epochDay: Long) {
        _uiState.update { state ->
            state.copy(form = state.form?.copy(epochDay = epochDay))
        }
    }

    fun onCustomShareChange(memberId: Long, shareText: String) {
        _uiState.update { state ->
            val form = state.form ?: return@update state
            val newShares = form.customShares.map {
                if (it.memberId == memberId) it.copy(amountText = shareText) else it
            }
            state.copy(form = form.copy(customShares = newShares, sharesError = null))
        }
    }

    private fun recalculateEqualShares(form: SharedExpenseFormState, members: List<Member>): SharedExpenseFormState {
        val totalAmount = form.amountText.toDoubleOrNull() ?: 0.0
        val count = members.size
        val sharePerMember = if (count > 0 && totalAmount > 0) totalAmount / count else 0.0
        val shareText = if (sharePerMember > 0) {
            if (sharePerMember % 1.0 == 0.0) sharePerMember.toLong().toString() else String.format("%.2f", sharePerMember)
        } else ""
        val newShares = members.map { member ->
            CustomShareInput(memberId = member.id, memberName = member.name, amountText = shareText)
        }
        return form.copy(customShares = newShares)
    }

    fun onSave() {
        val state = uiState.value
        val form = state.form ?: return
        val members = state.members

        val titleTrimmed = form.title.trim()
        val amount = form.amountText.toDoubleOrNull()

        var hasError = false
        var titleErr: String? = null
        var amountErr: String? = null
        var sharesErr: String? = null

        if (titleTrimmed.isBlank()) {
            titleErr = "error_title_required"
            hasError = true
        }
        if (amount == null || amount <= 0.0) {
            amountErr = "error_invalid_amount"
            hasError = true
        }

        val shares = if (form.splitType == SplitType.EQUAL) {
            val count = members.size
            if (count > 0 && amount != null && amount > 0.0) {
                val equalShare = amount / count
                members.map { SharedExpenseShare(expenseId = form.id, memberId = it.id, shareAmount = equalShare) }
            } else {
                emptyList()
            }
        } else {
            // CUSTOM split
            val customSharesList = form.customShares.map {
                SharedExpenseShare(
                    expenseId = form.id,
                    memberId = it.memberId,
                    shareAmount = it.amountText.toDoubleOrNull() ?: 0.0,
                )
            }
            val sharesSum = customSharesList.sumOf { it.shareAmount }
            if (amount != null && amount > 0.0 && kotlin.math.abs(sharesSum - amount) > 0.01) {
                sharesErr = "error_shares_sum_mismatch"
                hasError = true
            }
            customSharesList
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    form = form.copy(
                        titleError = titleErr,
                        amountError = amountErr,
                        sharesError = sharesErr,
                    ),
                )
            }
            return
        }

        val expense = SharedExpense(
            id = form.id,
            epochDay = form.epochDay,
            title = titleTrimmed,
            type = form.type,
            amount = amount!!,
            splitType = form.splitType,
            paidByMemberId = form.paidByMemberId,
            shares = shares,
        )

        viewModelScope.launch {
            if (form.isEdit) {
                repository.updateSharedExpense(expense)
            } else {
                repository.addSharedExpense(expense)
            }
            _uiState.update { it.copy(form = null, showSavedMessage = true) }
        }
    }

    fun onDismissForm() {
        _uiState.update { it.copy(form = null) }
    }
}

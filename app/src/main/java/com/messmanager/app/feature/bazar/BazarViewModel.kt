package com.messmanager.app.feature.bazar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.R
import com.messmanager.app.core.util.UiText
import com.messmanager.app.core.util.epochRange
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.BazarUnit
import com.messmanager.app.domain.model.PaymentType
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
class BazarViewModel @Inject constructor(
    private val repository: MessRepository,
) : ViewModel() {

    private val form = MutableStateFlow<BazarFormState?>(null)
    private val recentlyDeleted = MutableStateFlow<Bazar?>(null)

    val uiState: StateFlow<BazarUiState> = repository.selectedMonth.flatMapLatest { month ->
        val range = month.epochRange()
        combine(
            repository.observeBazar().map { list -> list.filter { it.epochDay in range } },
            repository.observeMembers(),
            repository.observeIsMonthClosed(month),
            form,
            recentlyDeleted,
        ) { items, members, isClosed, form, deleted ->
            BazarUiState(
                isLoading = false,
                items = items,
                members = members,
                isMonthClosed = isClosed,
                form = form,
                recentlyDeletedBazar = deleted,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), BazarUiState())

    fun onPreviousMonth() {
        repository.setSelectedMonth(repository.selectedMonth.value.minusMonths(1))
    }

    fun onNextMonth() {
        repository.setSelectedMonth(repository.selectedMonth.value.plusMonths(1))
    }

    fun onAddClick() {
        if (uiState.value.isMonthClosed) return
        val cat = BazarCategory.GROCERY
        form.update {
            BazarFormState(
                category = cat,
                unit = cat.defaultUnit,
                allowedUnits = cat.allowedUnits,
                dateEpochDay = repository.selectedMonth.value.atDay(1).toEpochDay(),
            )
        }
    }

    fun onEditClick(bazar: Bazar) {
        if (uiState.value.isMonthClosed) return
        val cat = bazar.category
        val allowed = cat.allowedUnits
        val unit = bazar.unit ?: cat.defaultUnit
        form.update {
            BazarFormState(
                id = bazar.id,
                amount = if (bazar.amount % 1.0 == 0.0) bazar.amount.toLong().toString() else bazar.amount.toString(),
                quantity = bazar.quantity?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "",
                category = cat,
                unit = if (allowed.contains(unit)) unit else cat.defaultUnit,
                allowedUnits = allowed,
                paymentType = bazar.paymentType,
                creditShopName = bazar.creditShopName,
                paidByMemberId = bazar.paidByMemberId,
                dateEpochDay = bazar.epochDay,
                note = bazar.note,
            )
        }
    }

    fun onDismissForm() = form.update { null }

    fun onAmountChange(value: String) = form.update { it?.copy(amount = value, amountError = null) }

    fun onQuickAmountClick(chipAmount: Int) = form.update { it?.copy(amount = chipAmount.toString(), amountError = null) }

    fun onQuantityChange(value: String) = form.update { it?.copy(quantity = value) }

    fun onCategoryChange(value: BazarCategory) = form.update { state ->
        state?.let {
            val allowed = value.allowedUnits
            val selectedUnit = if (allowed.contains(it.unit)) it.unit else value.defaultUnit
            it.copy(
                category = value,
                allowedUnits = allowed,
                unit = selectedUnit,
            )
        }
    }

    fun onUnitChange(value: BazarUnit) = form.update { it?.copy(unit = value) }

    fun onPaymentTypeChange(value: PaymentType) = form.update { it?.copy(paymentType = value, shopNameError = null) }

    fun onCreditShopNameChange(value: String) = form.update { it?.copy(creditShopName = value, shopNameError = null) }

    fun onPaidByMemberChange(memberId: Long?) = form.update { it?.copy(paidByMemberId = memberId) }

    fun onDateChange(epochDay: Long) = form.update { it?.copy(dateEpochDay = epochDay) }

    fun onNoteChange(value: String) = form.update { it?.copy(note = value) }

    fun onSave() {
        if (uiState.value.isMonthClosed) return
        val current = form.value ?: return
        val amount = current.amount.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            form.update { it?.copy(amountError = UiText.Resource(R.string.error_invalid_amount)) }
            return
        }

        if (current.paymentType == PaymentType.CREDIT && current.creditShopName.trim().isEmpty()) {
            form.update { it?.copy(shopNameError = UiText.Resource(R.string.error_shop_name_required)) }
            return
        }

        val quantity = current.quantity.toDoubleOrNull()

        viewModelScope.launch {
            val item = Bazar(
                id = current.id,
                epochDay = current.dateEpochDay,
                amount = amount,
                category = current.category,
                quantity = quantity,
                unit = current.unit,
                paymentType = current.paymentType,
                creditShopName = current.creditShopName.trim(),
                isCreditSettled = false,
                paidByMemberId = current.paidByMemberId,
                note = current.note.trim(),
            )

            if (current.isEditMode) {
                val existing = uiState.value.items.find { it.id == current.id }
                val updatedItem = item.copy(isCreditSettled = existing?.isCreditSettled ?: false)
                repository.updateBazar(updatedItem)
            } else {
                repository.addBazar(item)
            }
            form.update { null }
        }
    }

    fun onMarkAsPaid(bazar: Bazar) {
        if (uiState.value.isMonthClosed) return
        viewModelScope.launch {
            repository.updateBazarCreditSettled(bazar.id, true)
        }
    }

    fun onDelete(bazar: Bazar) {
        if (uiState.value.isMonthClosed) return
        recentlyDeleted.value = bazar
        viewModelScope.launch { repository.deleteBazar(bazar.id) }
    }

    fun onUndoDelete() {
        if (uiState.value.isMonthClosed) return
        val bazarToRestore = recentlyDeleted.value ?: return
        viewModelScope.launch {
            repository.addBazar(bazarToRestore)
            recentlyDeleted.value = null
        }
    }

    fun onClearUndo() {
        recentlyDeleted.value = null
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

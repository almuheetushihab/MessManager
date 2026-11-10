package com.messmanager.app.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.core.util.epochRange
import com.messmanager.app.domain.repository.MessRepository
import com.messmanager.app.domain.usecase.CalculateMessSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val calculateMessSummary: CalculateMessSummaryUseCase,
    private val repository: MessRepository,
) : ViewModel() {

    private val dialogState = MutableStateFlow(DialogState())
    private val sortOrderState = MutableStateFlow(ReportSortOrder.NAME)

    val uiState: StateFlow<ReportUiState> = repository.selectedMonth.flatMapLatest { month ->
        val range = month.epochRange()
        combine(
            combine(
                calculateMessSummary(month),
                repository.observeMealsBetween(range.first, range.last),
                repository.observeBazar(),
                repository.observeDeposits(),
            ) { summary, meals, bazar, deposits ->
                ReportDataGroup1(summary, meals, bazar, deposits)
            },
            combine(
                repository.observeSharedExpensesBetween(range.first, range.last),
                dialogState,
                sortOrderState,
            ) { sharedExpenses, dialogs, sortOrder ->
                ReportDataGroup2(sharedExpenses, dialogs, sortOrder)
            },
        ) { group1, group2 ->
            val monthBazar = group1.bazar.filter { it.epochDay in range }
            val monthDeposits = group1.deposits.filter { it.epochDay in range }
            ReportUiState(
                isLoading = false,
                summary = group1.summary,
                meals = group1.meals,
                bazarList = monthBazar,
                depositList = monthDeposits,
                sharedExpenses = group2.sharedExpenses,
                sortOrder = group2.sortOrder,
                showCloseDialog = group2.dialogs.showClose,
                showReopenDialog = group2.dialogs.showReopen,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ReportUiState())

    fun onSortOrderChange(order: ReportSortOrder) {
        sortOrderState.value = order
    }

    fun onPreviousMonth() {
        repository.setSelectedMonth(repository.selectedMonth.value.minusMonths(1))
    }

    fun onNextMonth() {
        repository.setSelectedMonth(repository.selectedMonth.value.plusMonths(1))
    }

    fun onRequestCloseMonth() {
        dialogState.update { it.copy(showClose = true) }
    }

    fun onRequestReopenMonth() {
        dialogState.update { it.copy(showReopen = true) }
    }

    fun onDismissDialog() {
        dialogState.update { DialogState() }
    }

    fun onConfirmCloseMonth() {
        val month = repository.selectedMonth.value
        viewModelScope.launch {
            repository.closeMonth(month)
            dialogState.update { DialogState() }
        }
    }

    fun onConfirmReopenMonth() {
        val month = repository.selectedMonth.value
        viewModelScope.launch {
            repository.reopenMonth(month)
            dialogState.update { DialogState() }
        }
    }

    private data class ReportDataGroup1(
        val summary: com.messmanager.app.domain.model.MessSummary,
        val meals: List<com.messmanager.app.domain.model.MealEntry>,
        val bazar: List<com.messmanager.app.domain.model.Bazar>,
        val deposits: List<com.messmanager.app.domain.model.Deposit>,
    )

    private data class ReportDataGroup2(
        val sharedExpenses: List<com.messmanager.app.domain.model.SharedExpense>,
        val dialogs: DialogState,
        val sortOrder: ReportSortOrder,
    )

    private data class DialogState(
        val showClose: Boolean = false,
        val showReopen: Boolean = false,
    )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

package com.messmanager.app.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        combine(
            calculateMessSummary(month),
            dialogState,
            sortOrderState,
        ) { summary, dialogs, sortOrder ->
            ReportUiState(
                isLoading = false,
                summary = summary,
                sortOrder = sortOrder,
                showCloseDialog = dialogs.showClose,
                showReopenDialog = dialogs.showReopen,
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

    private data class DialogState(
        val showClose: Boolean = false,
        val showReopen: Boolean = false,
    )

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

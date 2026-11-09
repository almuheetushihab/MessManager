package com.messmanager.app.feature.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.domain.usecase.CalculateMessSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    calculateMessSummary: CalculateMessSummaryUseCase,
) : ViewModel() {

    val uiState: StateFlow<ReportUiState> = calculateMessSummary()
        .map { ReportUiState(isLoading = false, summary = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ReportUiState())

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

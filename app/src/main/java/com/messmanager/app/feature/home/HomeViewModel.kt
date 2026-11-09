package com.messmanager.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.domain.repository.MessRepository
import com.messmanager.app.domain.usecase.CalculateMessSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    calculateMessSummary: CalculateMessSummaryUseCase,
    repository: MessRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        calculateMessSummary(),
        repository.observeMealsByDay(LocalDate.now().toEpochDay()),
    ) { summary, todayMeals ->
        HomeUiState(
            isLoading = false,
            summary = summary,
            todayMeals = todayMeals.sumOf { it.totalCount },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HomeUiState())

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

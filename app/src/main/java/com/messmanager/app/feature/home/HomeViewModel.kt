package com.messmanager.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.domain.repository.MessRepository
import com.messmanager.app.domain.usecase.CalculateMessSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val calculateMessSummary: CalculateMessSummaryUseCase,
    private val repository: MessRepository,
) : ViewModel() {

    val selectedMonth: StateFlow<YearMonth> = repository.selectedMonth

    val uiState: StateFlow<HomeUiState> = repository.selectedMonth.flatMapLatest { month ->
        combine(
            calculateMessSummary(month),
            repository.observeMealsByDay(LocalDate.now().toEpochDay()),
        ) { summary, todayMeals ->
            HomeUiState(
                isLoading = false,
                summary = summary,
                todayMeals = todayMeals.sumOf { it.totalCount },
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HomeUiState())

    fun onPreviousMonth() {
        repository.setSelectedMonth(selectedMonth.value.minusMonths(1))
    }

    fun onNextMonth() {
        repository.setSelectedMonth(selectedMonth.value.plusMonths(1))
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

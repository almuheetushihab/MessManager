package com.messmanager.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.SettingsRepository
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
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val selectedMonth: StateFlow<YearMonth> = repository.selectedMonth

    val uiState: StateFlow<HomeUiState> = repository.selectedMonth.flatMapLatest { month ->
        combine(
            calculateMessSummary(month),
            repository.observeMealsByDay(LocalDate.now().toEpochDay()),
            repository.observeBazar(),
            repository.observeDeposits(),
            settingsRepository.settings,
        ) { summary, todayMeals, bazarList, depositList, settings ->
            val needsAttention = summary.members
                .filter { it.balance < -0.01 }
                .sortedBy { it.balance }

            val bazarActivities = bazarList.map { bazar ->
                RecentActivity(
                    id = "bazar_${bazar.id}",
                    title = bazar.category.name,
                    subtitle = bazar.paidByMemberName ?: "",
                    amount = bazar.amount,
                    epochDay = bazar.epochDay,
                    type = RecentActivity.ActivityType.BAZAR,
                )
            }

            val depositActivities = depositList.map { deposit ->
                RecentActivity(
                    id = "deposit_${deposit.id}",
                    title = deposit.memberName,
                    subtitle = deposit.method.name,
                    amount = deposit.amount,
                    epochDay = deposit.epochDay,
                    type = RecentActivity.ActivityType.DEPOSIT,
                )
            }

            val combinedActivities = (bazarActivities + depositActivities)
                .sortedByDescending { it.epochDay }
                .take(5)

            HomeUiState(
                isLoading = false,
                messName = settings.messName,
                summary = summary,
                todayMeals = todayMeals.sumOf { it.totalCount },
                recentActivities = combinedActivities,
                needsAttentionMembers = needsAttention,
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

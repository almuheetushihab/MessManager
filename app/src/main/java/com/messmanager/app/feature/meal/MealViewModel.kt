package com.messmanager.app.feature.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messmanager.app.data.repository.SettingsRepository
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.MealType
import com.messmanager.app.domain.repository.MessRepository
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MealViewModel @Inject constructor(
    private val repository: MessRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<MealUiState> = selectedDate
        .flatMapLatest { date ->
            val epochDay = date.toEpochDay()
            val month = YearMonth.from(date)
            combine(
                repository.observeMembers(),
                repository.observeMealsByDay(epochDay),
                repository.observeIsMonthClosed(month),
                settingsRepository.settings,
            ) { members, meals, isClosed, settings ->
                val cutoffPassed = checkCutoffPassed(date, settings.cutoffTime)
                MealUiState(
                    isLoading = false,
                    date = date,
                    isMonthClosed = isClosed,
                    cutoffTime = settings.cutoffTime,
                    adminOverride = settings.adminOverride,
                    isCutoffPassed = cutoffPassed,
                    items = members.map { member ->
                        MemberMealUi(
                            member = member,
                            entry = meals.firstOrNull { it.memberId == member.id }
                                ?: MealEntry(memberId = member.id, epochDay = epochDay),
                        )
                    },
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), MealUiState())

    fun onPreviousDay() = selectedDate.update { it.minusDays(1) }
    fun onNextDay() = selectedDate.update { it.plusDays(1) }
    fun onToday() = selectedDate.update { LocalDate.now() }
    fun onDateSelected(date: LocalDate) = selectedDate.update { date }

    fun onToggleMeal(entry: MealEntry, type: MealType) {
        if (uiState.value.isEditingDisabled) return
        viewModelScope.launch { repository.saveMeal(entry.toggle(type)) }
    }

    fun onGuestsChange(entry: MealEntry, delta: Int) {
        if (uiState.value.isEditingDisabled) return
        viewModelScope.launch { repository.saveMeal(entry.changeGuests(delta)) }
    }

    fun onAllOn(type: MealType) {
        if (uiState.value.isEditingDisabled) return
        val currentItems = uiState.value.items
        if (currentItems.isEmpty()) return
        val updatedEntries = currentItems.map { ui ->
            val e = ui.entry
            when (type) {
                MealType.BREAKFAST -> e.copy(breakfast = true)
                MealType.LUNCH -> e.copy(lunch = true)
                MealType.DINNER -> e.copy(dinner = true)
            }
        }
        viewModelScope.launch { repository.saveMeals(updatedEntries) }
    }

    fun onCopyYesterday() {
        if (uiState.value.isEditingDisabled) return
        val targetEpoch = selectedDate.value.toEpochDay()
        viewModelScope.launch { repository.copyMealsFromYesterday(targetEpoch) }
    }

    fun onToggleAdminOverride(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setAdminOverride(enabled) }
    }

    fun onSetCutoffTime(cutoffTime: String?) {
        viewModelScope.launch { settingsRepository.setCutoffTime(cutoffTime) }
    }

    private fun checkCutoffPassed(date: LocalDate, cutoffTimeStr: String?): Boolean {
        if (date != LocalDate.now() || cutoffTimeStr.isNullOrBlank()) return false
        return runCatching {
            val cutoff = LocalTime.parse(cutoffTimeStr)
            LocalTime.now().isAfter(cutoff)
        }.getOrDefault(false)
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

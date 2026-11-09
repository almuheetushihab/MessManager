package com.messmanager.app.feature.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MealViewModel @Inject constructor(
    private val repository: MessRepository,
) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<MealUiState> = selectedDate
        .flatMapLatest { date ->
            val epochDay = date.toEpochDay()
            combine(
                repository.observeMembers(),
                repository.observeMealsByDay(epochDay),
            ) { members, meals ->
                MealUiState(
                    isLoading = false,
                    date = date,
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

    fun onToggleMeal(entry: MealEntry, type: MealType) {
        viewModelScope.launch { repository.saveMeal(entry.toggle(type)) }
    }

    fun onGuestsChange(entry: MealEntry, delta: Int) {
        viewModelScope.launch { repository.saveMeal(entry.changeGuests(delta)) }
    }

    private companion object {
        const val STOP_TIMEOUT_MS = 5_000L
    }
}

package com.messmanager.app.feature.meal

import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import java.time.LocalDate

data class MemberMealUi(
    val member: Member,
    val entry: MealEntry,
)

data class MealUiState(
    val isLoading: Boolean = true,
    val date: LocalDate = LocalDate.now(),
    val items: List<MemberMealUi> = emptyList(),
    val isMonthClosed: Boolean = false,
    val cutoffTime: String? = null,
    val adminOverride: Boolean = false,
    val isCutoffPassed: Boolean = false,
) {
    val dayTotal: Int get() = items.sumOf { it.entry.totalCount }
    val isEditingDisabled: Boolean get() = isMonthClosed || (isCutoffPassed && !adminOverride)
}

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
) {
    val dayTotal: Int get() = items.sumOf { it.entry.totalCount }
}

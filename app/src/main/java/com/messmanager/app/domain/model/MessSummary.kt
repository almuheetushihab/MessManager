package com.messmanager.app.domain.model

import java.time.YearMonth

data class MemberSummary(
    val member: Member,
    val meals: Int,
    val mealCost: Double,
    val deposit: Double,
    val paidByTotal: Double = 0.0,
) {
    /** Positive: member gets money back. Negative: member needs to pay. */
    val balance: Double get() = deposit + paidByTotal - mealCost
}

data class MessSummary(
    val month: YearMonth = YearMonth.now(),
    val totalBazar: Double = 0.0,
    val totalMeals: Int = 0,
    val mealRate: Double = 0.0,
    val totalOutstandingBaki: Double = 0.0,
    val members: List<MemberSummary> = emptyList(),
)

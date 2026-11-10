package com.messmanager.app.domain.model

import java.time.YearMonth

data class MessSummary(
    val month: YearMonth = YearMonth.now(),
    val totalBazar: Double = 0.0,
    val totalSharedExpenses: Double = 0.0,
    val totalMeals: Int = 0,
    val mealRate: Double = 0.0,
    val totalOutstandingBaki: Double = 0.0,
    val isClosed: Boolean = false,
    val members: List<MemberSummary> = emptyList(),
)

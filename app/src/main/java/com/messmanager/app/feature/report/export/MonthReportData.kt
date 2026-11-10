package com.messmanager.app.feature.report.export

import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.MessSummary
import com.messmanager.app.domain.model.SharedExpense

data class MonthReportData(
    val summary: MessSummary = MessSummary(),
    val meals: List<MealEntry> = emptyList(),
    val bazarList: List<Bazar> = emptyList(),
    val depositList: List<Deposit> = emptyList(),
    val sharedExpenses: List<SharedExpense> = emptyList(),
)

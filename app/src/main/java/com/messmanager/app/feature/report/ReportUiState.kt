package com.messmanager.app.feature.report

import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.MessSummary
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.feature.report.export.MonthReportData

enum class ReportSortOrder {
    NAME,
    HIGHEST_DUE,
    HIGHEST_REFUND,
}

data class ReportUiState(
    val isLoading: Boolean = true,
    val summary: MessSummary = MessSummary(),
    val meals: List<MealEntry> = emptyList(),
    val bazarList: List<Bazar> = emptyList(),
    val depositList: List<Deposit> = emptyList(),
    val sharedExpenses: List<SharedExpense> = emptyList(),
    val sortOrder: ReportSortOrder = ReportSortOrder.NAME,
    val showCloseDialog: Boolean = false,
    val showReopenDialog: Boolean = false,
) {
    val reportData: MonthReportData
        get() = MonthReportData(
            summary = summary,
            meals = meals,
            bazarList = bazarList,
            depositList = depositList,
            sharedExpenses = sharedExpenses,
        )
}

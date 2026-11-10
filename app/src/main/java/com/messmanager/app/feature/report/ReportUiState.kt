package com.messmanager.app.feature.report

import com.messmanager.app.domain.model.MessSummary

enum class ReportSortOrder {
    NAME,
    HIGHEST_DUE,
    HIGHEST_REFUND,
}

data class ReportUiState(
    val isLoading: Boolean = true,
    val summary: MessSummary = MessSummary(),
    val sortOrder: ReportSortOrder = ReportSortOrder.NAME,
    val showCloseDialog: Boolean = false,
    val showReopenDialog: Boolean = false,
)

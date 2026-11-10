package com.messmanager.app.feature.report

import com.messmanager.app.domain.model.MessSummary

data class ReportUiState(
    val isLoading: Boolean = true,
    val summary: MessSummary = MessSummary(),
    val showCloseDialog: Boolean = false,
    val showReopenDialog: Boolean = false,
)

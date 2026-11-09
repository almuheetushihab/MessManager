package com.messmanager.app.feature.home

import com.messmanager.app.domain.model.MessSummary

data class HomeUiState(
    val isLoading: Boolean = true,
    val summary: MessSummary = MessSummary(),
    val todayMeals: Int = 0,
)

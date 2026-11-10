package com.messmanager.app.feature.home

import com.messmanager.app.domain.model.MemberSummary
import com.messmanager.app.domain.model.MessSummary

data class RecentActivity(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val epochDay: Long,
    val type: ActivityType,
) {
    enum class ActivityType { BAZAR, DEPOSIT }
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val messName: String = "",
    val summary: MessSummary = MessSummary(),
    val todayMeals: Int = 0,
    val recentActivities: List<RecentActivity> = emptyList(),
    val needsAttentionMembers: List<MemberSummary> = emptyList(),
)

package com.messmanager.app.domain.model

data class SharedExpense(
    val id: Long = 0,
    val epochDay: Long,
    val title: String,
    val type: SharedExpenseType,
    val amount: Double,
    val splitType: SplitType,
    val paidByMemberId: Long? = null,
    val paidByMemberName: String? = null,
    val shares: List<SharedExpenseShare> = emptyList(),
)

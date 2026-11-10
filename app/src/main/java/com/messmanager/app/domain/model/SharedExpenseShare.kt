package com.messmanager.app.domain.model

data class SharedExpenseShare(
    val expenseId: Long = 0,
    val memberId: Long,
    val shareAmount: Double,
    val memberName: String? = null,
)

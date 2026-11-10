package com.messmanager.app.domain.model

data class MemberSummary(
    val member: Member,
    val meals: Int,
    val mealCost: Double,
    val deposit: Double,
    val paidByTotal: Double = 0.0,
    val sharedExpenseShare: Double = 0.0,
    val previousBalance: Double = 0.0,
) {
    /** Positive: member gets money back. Negative: member needs to pay. */
    val balance: Double get() = previousBalance + deposit + paidByTotal - mealCost - sharedExpenseShare
}

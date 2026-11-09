package com.messmanager.app.domain.model

data class Deposit(
    val id: Long = 0,
    val memberId: Long,
    val memberName: String = "",
    val epochDay: Long,
    val amount: Double,
    val method: PaymentMethod,
)

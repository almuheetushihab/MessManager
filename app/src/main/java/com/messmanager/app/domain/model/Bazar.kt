package com.messmanager.app.domain.model

data class Bazar(
    val id: Long = 0,
    val epochDay: Long,
    val amount: Double,
    val category: BazarCategory,
    val note: String = "",
    val quantity: Double? = null,
    val unit: BazarUnit? = null,
    val paymentType: PaymentType = PaymentType.CASH,
    val creditShopName: String = "",
    val isCreditSettled: Boolean = false,
    val paidByMemberId: Long? = null,
    val paidByMemberName: String? = null,
    val photoUri: String? = null,
)

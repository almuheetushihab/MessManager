package com.messmanager.app.domain.model

data class Bazar(
    val id: Long = 0,
    val epochDay: Long,
    val amount: Double,
    val category: BazarCategory,
    val note: String = "",
)

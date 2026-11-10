package com.messmanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "shared_expense_shares",
    primaryKeys = ["expenseId", "memberId"],
    foreignKeys = [
        ForeignKey(
            entity = SharedExpenseEntity::class,
            parentColumns = ["id"],
            childColumns = ["expenseId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("expenseId"),
        Index("memberId"),
    ],
)
data class SharedExpenseShareEntity(
    val expenseId: Long,
    val memberId: Long,
    val shareAmount: Double,
)

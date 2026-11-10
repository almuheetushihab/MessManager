package com.messmanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "shared_expenses",
    foreignKeys = [
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["paidByMemberId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("paidByMemberId")],
)
data class SharedExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val epochDay: Long,
    val title: String,
    val type: String,
    val amount: Double,
    val splitType: String,
    val paidByMemberId: Long? = null,
)

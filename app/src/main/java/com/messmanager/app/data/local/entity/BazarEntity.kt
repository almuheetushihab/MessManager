package com.messmanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bazar",
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
data class BazarEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val epochDay: Long,
    val amount: Double,
    val category: String,
    val note: String,
    val quantity: Double? = null,
    val unit: String? = null,
    val paymentType: String = "CASH",
    val creditShopName: String = "",
    val isCreditSettled: Boolean = false,
    val paidByMemberId: Long? = null,
    val photoUri: String? = null,
)

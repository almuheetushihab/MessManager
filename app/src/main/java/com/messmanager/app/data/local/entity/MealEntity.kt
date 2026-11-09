package com.messmanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "meals",
    foreignKeys = [
        ForeignKey(
            entity = MemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["memberId", "epochDay"], unique = true)],
)
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long,
    val epochDay: Long,
    val breakfast: Boolean,
    val lunch: Boolean,
    val dinner: Boolean,
    val guests: Int,
)

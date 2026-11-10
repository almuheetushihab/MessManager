package com.messmanager.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "closed_months")
data class ClosedMonthEntity(
    @PrimaryKey val month: String,
    val closedAtEpochDay: Long,
)

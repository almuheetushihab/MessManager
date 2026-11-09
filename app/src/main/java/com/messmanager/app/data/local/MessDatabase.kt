package com.messmanager.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.messmanager.app.data.local.dao.BazarDao
import com.messmanager.app.data.local.dao.DepositDao
import com.messmanager.app.data.local.dao.MealDao
import com.messmanager.app.data.local.dao.MemberDao
import com.messmanager.app.data.local.entity.BazarEntity
import com.messmanager.app.data.local.entity.DepositEntity
import com.messmanager.app.data.local.entity.MealEntity
import com.messmanager.app.data.local.entity.MemberEntity

@Database(
    entities = [MemberEntity::class, MealEntity::class, BazarEntity::class, DepositEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class MessDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun mealDao(): MealDao
    abstract fun bazarDao(): BazarDao
    abstract fun depositDao(): DepositDao

    companion object {
        const val NAME = "mess_manager.db"
    }
}

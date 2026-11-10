package com.messmanager.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.messmanager.app.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meals WHERE epochDay = :epochDay")
    fun observeByDay(epochDay: Long): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE epochDay = :epochDay")
    suspend fun getByDay(epochDay: Long): List<MealEntity>

    @Query("SELECT * FROM meals WHERE epochDay BETWEEN :start AND :end")
    fun observeBetween(start: Long, end: Long): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals")
    suspend fun getAll(): List<MealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meal: MealEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(meals: List<MealEntity>)

    @Query("DELETE FROM meals")
    suspend fun deleteAll()
}

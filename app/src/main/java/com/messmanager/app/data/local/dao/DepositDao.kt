package com.messmanager.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.messmanager.app.data.local.entity.DepositEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits ORDER BY epochDay DESC, id DESC")
    fun observeAll(): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits")
    suspend fun getAll(): List<DepositEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(deposit: DepositEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deposits: List<DepositEntity>)

    @Query("DELETE FROM deposits WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM deposits")
    suspend fun deleteAll()
}

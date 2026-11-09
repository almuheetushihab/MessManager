package com.messmanager.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.messmanager.app.data.local.entity.DepositEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits ORDER BY epochDay DESC, id DESC")
    fun observeAll(): Flow<List<DepositEntity>>

    @Insert
    suspend fun insert(deposit: DepositEntity)

    @Query("DELETE FROM deposits WHERE id = :id")
    suspend fun deleteById(id: Long)
}

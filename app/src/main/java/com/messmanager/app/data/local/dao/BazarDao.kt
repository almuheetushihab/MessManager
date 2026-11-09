package com.messmanager.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.messmanager.app.data.local.entity.BazarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BazarDao {
    @Query("SELECT * FROM bazar ORDER BY epochDay DESC, id DESC")
    fun observeAll(): Flow<List<BazarEntity>>

    @Insert
    suspend fun insert(bazar: BazarEntity)

    @Query("DELETE FROM bazar WHERE id = :id")
    suspend fun deleteById(id: Long)
}

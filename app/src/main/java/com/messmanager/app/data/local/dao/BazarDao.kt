package com.messmanager.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.messmanager.app.data.local.entity.BazarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BazarDao {
    @Query("SELECT * FROM bazar ORDER BY epochDay DESC, id DESC")
    fun observeAll(): Flow<List<BazarEntity>>

    @Insert
    suspend fun insert(bazar: BazarEntity)

    @Update
    suspend fun update(bazar: BazarEntity)

    @Query("UPDATE bazar SET isCreditSettled = :isSettled WHERE id = :id")
    suspend fun updateCreditSettled(id: Long, isSettled: Boolean)

    @Query("DELETE FROM bazar WHERE id = :id")
    suspend fun deleteById(id: Long)
}

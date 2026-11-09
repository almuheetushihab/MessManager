package com.messmanager.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.messmanager.app.data.local.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun observeAll(): Flow<List<MemberEntity>>

    @Insert
    suspend fun insert(member: MemberEntity)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun deleteById(id: Long)
}

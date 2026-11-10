package com.messmanager.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.messmanager.app.data.local.entity.ClosedMonthEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClosedMonthDao {
    @Query("SELECT month FROM closed_months")
    fun observeClosedMonths(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM closed_months WHERE month = :month)")
    fun observeIsClosed(month: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM closed_months WHERE month = :month)")
    suspend fun isClosed(month: String): Boolean

    @Query("SELECT * FROM closed_months")
    suspend fun getAll(): List<ClosedMonthEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun closeMonth(entity: ClosedMonthEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(closedMonths: List<ClosedMonthEntity>)

    @Query("DELETE FROM closed_months WHERE month = :month")
    suspend fun reopenMonth(month: String)

    @Query("DELETE FROM closed_months")
    suspend fun deleteAll()
}

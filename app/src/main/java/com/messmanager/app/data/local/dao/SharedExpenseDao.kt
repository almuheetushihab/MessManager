package com.messmanager.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.messmanager.app.data.local.entity.SharedExpenseEntity
import com.messmanager.app.data.local.entity.SharedExpenseShareEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SharedExpenseDao {
    @Query("SELECT * FROM shared_expenses ORDER BY epochDay DESC, id DESC")
    fun observeAllExpenses(): Flow<List<SharedExpenseEntity>>

    @Query("SELECT * FROM shared_expenses WHERE epochDay BETWEEN :start AND :end ORDER BY epochDay DESC, id DESC")
    fun observeExpensesBetween(start: Long, end: Long): Flow<List<SharedExpenseEntity>>

    @Query("SELECT * FROM shared_expense_shares")
    fun observeAllShares(): Flow<List<SharedExpenseShareEntity>>

    @Query("SELECT * FROM shared_expenses")
    suspend fun getAllExpenses(): List<SharedExpenseEntity>

    @Query("SELECT * FROM shared_expense_shares")
    suspend fun getAllShares(): List<SharedExpenseShareEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: SharedExpenseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExpenses(expenses: List<SharedExpenseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShares(shares: List<SharedExpenseShareEntity>)

    @Update
    suspend fun updateExpense(expense: SharedExpenseEntity)

    @Query("DELETE FROM shared_expense_shares WHERE expenseId = :expenseId")
    suspend fun deleteSharesForExpense(expenseId: Long)

    @Query("DELETE FROM shared_expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("DELETE FROM shared_expense_shares")
    suspend fun deleteAllShares()

    @Query("DELETE FROM shared_expenses")
    suspend fun deleteAllExpenses()

    @Transaction
    suspend fun insertExpenseWithShares(expense: SharedExpenseEntity, shares: List<SharedExpenseShareEntity>): Long {
        val expenseId = insertExpense(expense)
        val sharesWithId = shares.map { it.copy(expenseId = expenseId) }
        insertShares(sharesWithId)
        return expenseId
    }

    @Transaction
    suspend fun updateExpenseWithShares(expense: SharedExpenseEntity, shares: List<SharedExpenseShareEntity>) {
        updateExpense(expense)
        deleteSharesForExpense(expense.id)
        val sharesWithId = shares.map { it.copy(expenseId = expense.id) }
        insertShares(sharesWithId)
    }
}

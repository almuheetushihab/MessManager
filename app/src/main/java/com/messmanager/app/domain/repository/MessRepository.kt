package com.messmanager.app.domain.repository

import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.SharedExpense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.YearMonth

interface MessRepository {
    val selectedMonth: StateFlow<YearMonth>
    fun setSelectedMonth(month: YearMonth)

    fun observeMembers(): Flow<List<Member>>
    suspend fun addMember(member: Member)
    suspend fun deleteMember(id: Long)

    fun observeMealsByDay(epochDay: Long): Flow<List<MealEntry>>
    fun observeMealsBetween(start: Long, end: Long): Flow<List<MealEntry>>
    suspend fun saveMeal(entry: MealEntry)
    suspend fun saveMeals(entries: List<MealEntry>)
    suspend fun copyMealsFromYesterday(targetEpochDay: Long)

    fun observeBazar(): Flow<List<Bazar>>
    suspend fun addBazar(bazar: Bazar)
    suspend fun updateBazar(bazar: Bazar)
    suspend fun updateBazarCreditSettled(id: Long, isSettled: Boolean)
    suspend fun deleteBazar(id: Long)

    fun observeDeposits(): Flow<List<Deposit>>
    suspend fun addDeposit(deposit: Deposit)
    suspend fun deleteDeposit(id: Long)

    fun observeSharedExpenses(): Flow<List<SharedExpense>>
    fun observeSharedExpensesBetween(start: Long, end: Long): Flow<List<SharedExpense>>
    suspend fun addSharedExpense(expense: SharedExpense)
    suspend fun updateSharedExpense(expense: SharedExpense)
    suspend fun deleteSharedExpense(id: Long)

    fun observeClosedMonths(): Flow<List<YearMonth>>
    fun observeIsMonthClosed(month: YearMonth): Flow<Boolean>
    suspend fun closeMonth(month: YearMonth)
    suspend fun reopenMonth(month: YearMonth)
}

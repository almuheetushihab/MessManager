package com.messmanager.app.domain.repository

import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import kotlinx.coroutines.flow.Flow

interface MessRepository {
    fun observeMembers(): Flow<List<Member>>
    suspend fun addMember(member: Member)
    suspend fun deleteMember(id: Long)

    fun observeMealsByDay(epochDay: Long): Flow<List<MealEntry>>
    fun observeMealsBetween(start: Long, end: Long): Flow<List<MealEntry>>
    suspend fun saveMeal(entry: MealEntry)

    fun observeBazar(): Flow<List<Bazar>>
    suspend fun addBazar(bazar: Bazar)
    suspend fun deleteBazar(id: Long)

    fun observeDeposits(): Flow<List<Deposit>>
    suspend fun addDeposit(deposit: Deposit)
    suspend fun deleteDeposit(id: Long)
}

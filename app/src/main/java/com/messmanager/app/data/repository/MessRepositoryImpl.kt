package com.messmanager.app.data.repository

import com.messmanager.app.data.local.dao.BazarDao
import com.messmanager.app.data.local.dao.DepositDao
import com.messmanager.app.data.local.dao.MealDao
import com.messmanager.app.data.local.dao.MemberDao
import com.messmanager.app.data.mapper.toDomain
import com.messmanager.app.data.mapper.toEntity
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.repository.MessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessRepositoryImpl @Inject constructor(
    private val memberDao: MemberDao,
    private val mealDao: MealDao,
    private val bazarDao: BazarDao,
    private val depositDao: DepositDao,
) : MessRepository {

    override fun observeMembers(): Flow<List<Member>> =
        memberDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addMember(member: Member) = memberDao.insert(member.toEntity())

    override suspend fun deleteMember(id: Long) = memberDao.deleteById(id)

    override fun observeMealsByDay(epochDay: Long): Flow<List<MealEntry>> =
        mealDao.observeByDay(epochDay).map { list -> list.map { it.toDomain() } }

    override fun observeMealsBetween(start: Long, end: Long): Flow<List<MealEntry>> =
        mealDao.observeBetween(start, end).map { list -> list.map { it.toDomain() } }

    override suspend fun saveMeal(entry: MealEntry) = mealDao.upsert(entry.toEntity())

    override fun observeBazar(): Flow<List<Bazar>> =
        bazarDao.observeAll().map { list -> list.map { it.toDomain() } }

    override suspend fun addBazar(bazar: Bazar) = bazarDao.insert(bazar.toEntity())

    override suspend fun deleteBazar(id: Long) = bazarDao.deleteById(id)

    override fun observeDeposits(): Flow<List<Deposit>> =
        combine(depositDao.observeAll(), memberDao.observeAll()) { deposits, members ->
            val names = members.associate { it.id to it.name }
            deposits.map { it.toDomain(memberName = names[it.memberId].orEmpty()) }
        }

    override suspend fun addDeposit(deposit: Deposit) = depositDao.insert(deposit.toEntity())

    override suspend fun deleteDeposit(id: Long) = depositDao.deleteById(id)
}

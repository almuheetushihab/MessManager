package com.messmanager.app.data.repository

import com.messmanager.app.data.local.dao.BazarDao
import com.messmanager.app.data.local.dao.ClosedMonthDao
import com.messmanager.app.data.local.dao.DepositDao
import com.messmanager.app.data.local.dao.MealDao
import com.messmanager.app.data.local.dao.MemberDao
import com.messmanager.app.data.local.dao.SharedExpenseDao
import com.messmanager.app.data.local.entity.ClosedMonthEntity
import com.messmanager.app.data.mapper.toDomain
import com.messmanager.app.data.mapper.toEntity
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.domain.repository.MessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessRepositoryImpl @Inject constructor(
    private val memberDao: MemberDao,
    private val mealDao: MealDao,
    private val bazarDao: BazarDao,
    private val depositDao: DepositDao,
    private val closedMonthDao: ClosedMonthDao,
    private val sharedExpenseDao: SharedExpenseDao,
) : MessRepository {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    override val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    override fun setSelectedMonth(month: YearMonth) {
        _selectedMonth.value = month
    }

    override fun observeMembers(): Flow<List<Member>> =
        memberDao.observeAll()
            .map { list -> list.map { it.toDomain() } }
            .catch { emit(emptyList()) }

    override suspend fun addMember(member: Member) {
        runCatching { memberDao.insert(member.toEntity()) }
    }

    override suspend fun deleteMember(id: Long) {
        runCatching { memberDao.deleteById(id) }
    }

    override fun observeMealsByDay(epochDay: Long): Flow<List<MealEntry>> =
        mealDao.observeByDay(epochDay)
            .map { list -> list.map { it.toDomain() } }
            .catch { emit(emptyList()) }

    override fun observeMealsBetween(start: Long, end: Long): Flow<List<MealEntry>> =
        mealDao.observeBetween(start, end)
            .map { list -> list.map { it.toDomain() } }
            .catch { emit(emptyList()) }

    override suspend fun saveMeal(entry: MealEntry) {
        runCatching { mealDao.upsert(entry.toEntity()) }
    }

    override suspend fun saveMeals(entries: List<MealEntry>) {
        runCatching { entries.forEach { mealDao.upsert(it.toEntity()) } }
    }

    override suspend fun copyMealsFromYesterday(targetEpochDay: Long) {
        runCatching {
            val yesterdayEpoch = targetEpochDay - 1
            val yesterdayMeals = mealDao.getByDay(yesterdayEpoch)
            if (yesterdayMeals.isNotEmpty()) {
                val copiedMeals = yesterdayMeals.map { entity ->
                    entity.copy(epochDay = targetEpochDay)
                }
                copiedMeals.forEach { mealDao.upsert(it) }
            }
        }
    }

    override fun observeBazar(): Flow<List<Bazar>> =
        combine(bazarDao.observeAll(), memberDao.observeAll()) { bazars, members ->
            val memberNames = members.associate { it.id to it.name }
            bazars.map { entity ->
                entity.toDomain(paidByMemberName = entity.paidByMemberId?.let { memberNames[it] })
            }
        }.catch { emit(emptyList()) }

    override suspend fun addBazar(bazar: Bazar) {
        runCatching { bazarDao.insert(bazar.toEntity()) }
    }

    override suspend fun updateBazar(bazar: Bazar) {
        runCatching { bazarDao.update(bazar.toEntity()) }
    }

    override suspend fun updateBazarCreditSettled(id: Long, isSettled: Boolean) {
        runCatching { bazarDao.updateCreditSettled(id, isSettled) }
    }

    override suspend fun deleteBazar(id: Long) {
        runCatching { bazarDao.deleteById(id) }
    }

    override fun observeDeposits(): Flow<List<Deposit>> =
        combine(depositDao.observeAll(), memberDao.observeAll()) { deposits, members ->
            val names = members.associate { it.id to it.name }
            deposits.map { it.toDomain(memberName = names[it.memberId].orEmpty()) }
        }.catch { emit(emptyList()) }

    override suspend fun addDeposit(deposit: Deposit) {
        runCatching { depositDao.insert(deposit.toEntity()) }
    }

    override suspend fun deleteDeposit(id: Long) {
        runCatching { depositDao.deleteById(id) }
    }

    override fun observeSharedExpenses(): Flow<List<SharedExpense>> =
        combine(
            sharedExpenseDao.observeAllExpenses(),
            sharedExpenseDao.observeAllShares(),
            memberDao.observeAll(),
        ) { expenses, shares, members ->
            val memberNames = members.associate { it.id to it.name }
            val sharesByExpense = shares.groupBy { it.expenseId }
            expenses.map { entity ->
                val expenseShares = (sharesByExpense[entity.id] ?: emptyList()).map { shareEntity ->
                    shareEntity.toDomain(memberName = memberNames[shareEntity.memberId])
                }
                entity.toDomain(
                    paidByMemberName = entity.paidByMemberId?.let { memberNames[it] },
                    shares = expenseShares,
                )
            }
        }.catch { emit(emptyList()) }

    override fun observeSharedExpensesBetween(start: Long, end: Long): Flow<List<SharedExpense>> =
        combine(
            sharedExpenseDao.observeExpensesBetween(start, end),
            sharedExpenseDao.observeAllShares(),
            memberDao.observeAll(),
        ) { expenses, shares, members ->
            val memberNames = members.associate { it.id to it.name }
            val sharesByExpense = shares.groupBy { it.expenseId }
            expenses.map { entity ->
                val expenseShares = (sharesByExpense[entity.id] ?: emptyList()).map { shareEntity ->
                    shareEntity.toDomain(memberName = memberNames[shareEntity.memberId])
                }
                entity.toDomain(
                    paidByMemberName = entity.paidByMemberId?.let { memberNames[it] },
                    shares = expenseShares,
                )
            }
        }.catch { emit(emptyList()) }

    override suspend fun addSharedExpense(expense: SharedExpense) {
        runCatching {
            val entity = expense.toEntity()
            val shares = expense.shares.map { it.toEntity() }
            sharedExpenseDao.insertExpenseWithShares(entity, shares)
        }
    }

    override suspend fun updateSharedExpense(expense: SharedExpense) {
        runCatching {
            val entity = expense.toEntity()
            val shares = expense.shares.map { it.toEntity(expense.id) }
            sharedExpenseDao.updateExpenseWithShares(entity, shares)
        }
    }

    override suspend fun deleteSharedExpense(id: Long) {
        runCatching { sharedExpenseDao.deleteExpenseById(id) }
    }

    override fun observeClosedMonths(): Flow<List<YearMonth>> =
        closedMonthDao.observeClosedMonths()
            .map { list -> list.mapNotNull { str -> runCatching { YearMonth.parse(str) }.getOrNull() } }
            .catch { emit(emptyList()) }

    override fun observeIsMonthClosed(month: YearMonth): Flow<Boolean> =
        closedMonthDao.observeIsClosed(month.toString())
            .catch { emit(false) }

    override suspend fun closeMonth(month: YearMonth) {
        runCatching {
            closedMonthDao.closeMonth(
                ClosedMonthEntity(
                    month = month.toString(),
                    closedAtEpochDay = LocalDate.now().toEpochDay(),
                ),
            )
        }
    }

    override suspend fun reopenMonth(month: YearMonth) {
        runCatching { closedMonthDao.reopenMonth(month.toString()) }
    }
}

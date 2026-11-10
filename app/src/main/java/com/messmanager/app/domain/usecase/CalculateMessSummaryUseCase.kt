package com.messmanager.app.domain.usecase

import com.messmanager.app.core.util.epochRange
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.MemberSummary
import com.messmanager.app.domain.model.MessSummary
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.domain.repository.MessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import javax.inject.Inject

/**
 * meal rate = total bazar / total meals
 * member balance = previousBalance + deposit + paidByTotal - (member meals * meal rate)
 */
class CalculateMessSummaryUseCase @Inject constructor(
    private val repository: MessRepository,
) {
    operator fun invoke(month: YearMonth = YearMonth.now()): Flow<MessSummary> {
        val startRange = month.minusMonths(12).atDay(1).toEpochDay()
        val endRange = month.atEndOfMonth().toEpochDay()

        return combine(
            repository.observeMembers(),
            repository.observeMealsBetween(startRange, endRange),
            repository.observeBazar(),
            repository.observeDeposits(),
            repository.observeIsMonthClosed(month),
        ) { members, meals, bazar, deposits, isClosed ->
            calculateWithCarryForward(month, members, meals, bazar, deposits, isClosed)
        }
    }

    fun calculateWithCarryForward(
        targetMonth: YearMonth,
        members: List<Member>,
        allMeals: List<MealEntry>,
        allBazar: List<Bazar>,
        allDeposits: List<Deposit>,
        isClosed: Boolean = false,
        maxMonthsBack: Long = 12,
    ): MessSummary {
        var currentPrevBalances = members.associate { it.id to 0.0 }

        val startMonth = targetMonth.minusMonths(maxMonthsBack)
        var m = startMonth
        while (m.isBefore(targetMonth)) {
            val range = m.epochRange()
            val mMeals = allMeals.filter { it.epochDay in range }
            val mBazar = allBazar.filter { it.epochDay in range }
            val mDeposits = allDeposits.filter { it.epochDay in range }

            val summary = calculateSingleMonth(
                month = m,
                members = members,
                meals = mMeals,
                bazar = mBazar,
                deposits = mDeposits,
                previousBalances = currentPrevBalances,
                isClosed = true,
            )
            currentPrevBalances = summary.members.associate { it.member.id to it.balance }
            m = m.plusMonths(1)
        }

        val targetRange = targetMonth.epochRange()
        return calculateSingleMonth(
            month = targetMonth,
            members = members,
            meals = allMeals.filter { it.epochDay in targetRange },
            bazar = allBazar.filter { it.epochDay in targetRange },
            deposits = allDeposits.filter { it.epochDay in targetRange },
            previousBalances = currentPrevBalances,
            isClosed = isClosed,
        )
    }

    fun calculateSingleMonth(
        month: YearMonth,
        members: List<Member>,
        meals: List<MealEntry>,
        bazar: List<Bazar>,
        deposits: List<Deposit>,
        previousBalances: Map<Long, Double> = emptyMap(),
        isClosed: Boolean = false,
    ): MessSummary {
        val mealsByMember = meals.groupBy { it.memberId }.mapValues { (_, list) -> list.sumOf { it.totalCount } }
        val depositByMember = deposits.groupBy { it.memberId }.mapValues { (_, list) -> list.sumOf { it.amount } }
        val paidByMember = bazar.filter { it.paidByMemberId != null }
            .groupBy { it.paidByMemberId!! }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        val totalMeals = members.sumOf { mealsByMember[it.id] ?: 0 }
        val totalBazar = bazar.sumOf { it.amount }
        val mealRate = if (totalMeals > 0) totalBazar / totalMeals else 0.0

        val totalOutstandingBaki = bazar
            .filter { it.paymentType == PaymentType.CREDIT && !it.isCreditSettled }
            .sumOf { it.amount }

        val summaries = members.map { member ->
            val memberMeals = mealsByMember[member.id] ?: 0
            MemberSummary(
                member = member,
                meals = memberMeals,
                mealCost = memberMeals * mealRate,
                deposit = depositByMember[member.id] ?: 0.0,
                paidByTotal = paidByMember[member.id] ?: 0.0,
                previousBalance = previousBalances[member.id] ?: 0.0,
            )
        }
        return MessSummary(
            month = month,
            totalBazar = totalBazar,
            totalMeals = totalMeals,
            mealRate = mealRate,
            totalOutstandingBaki = totalOutstandingBaki,
            isClosed = isClosed,
            members = summaries,
        )
    }
}

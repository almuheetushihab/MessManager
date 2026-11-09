package com.messmanager.app.domain.usecase

import com.messmanager.app.core.util.epochRange
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.MemberSummary
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.MessSummary
import com.messmanager.app.domain.repository.MessRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth
import javax.inject.Inject

/**
 * meal rate = total bazar / total meals
 * member balance = deposit - (member meals * meal rate)
 */
class CalculateMessSummaryUseCase @Inject constructor(
    private val repository: MessRepository,
) {
    operator fun invoke(month: YearMonth = YearMonth.now()): Flow<MessSummary> {
        val range = month.epochRange()
        return combine(
            repository.observeMembers(),
            repository.observeMealsBetween(range.first, range.last),
            repository.observeBazar(),
            repository.observeDeposits(),
        ) { members, meals, bazar, deposits ->
            calculate(month, members, meals, bazar.filter { it.epochDay in range }, deposits.filter { it.epochDay in range })
        }
    }

    fun calculate(
        month: YearMonth,
        members: List<Member>,
        meals: List<MealEntry>,
        bazar: List<Bazar>,
        deposits: List<Deposit>,
    ): MessSummary {
        val mealsByMember = meals.groupBy { it.memberId }.mapValues { (_, list) -> list.sumOf { it.totalCount } }
        val depositByMember = deposits.groupBy { it.memberId }.mapValues { (_, list) -> list.sumOf { it.amount } }

        val totalMeals = members.sumOf { mealsByMember[it.id] ?: 0 }
        val totalBazar = bazar.sumOf { it.amount }
        val mealRate = if (totalMeals > 0) totalBazar / totalMeals else 0.0

        val summaries = members.map { member ->
            val memberMeals = mealsByMember[member.id] ?: 0
            MemberSummary(
                member = member,
                meals = memberMeals,
                mealCost = memberMeals * mealRate,
                deposit = depositByMember[member.id] ?: 0.0,
            )
        }
        return MessSummary(
            month = month,
            totalBazar = totalBazar,
            totalMeals = totalMeals,
            mealRate = mealRate,
            members = summaries,
        )
    }
}

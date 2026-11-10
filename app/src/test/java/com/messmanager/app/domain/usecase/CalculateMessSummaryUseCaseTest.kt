package com.messmanager.app.domain.usecase

import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.domain.repository.MessRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import java.lang.reflect.Proxy
import java.time.LocalDate
import java.time.YearMonth

class CalculateMessSummaryUseCaseTest {

    private val repository = Proxy.newProxyInstance(
        MessRepository::class.java.classLoader,
        arrayOf(MessRepository::class.java),
    ) { _, _, _ -> error("Repository must not be used in calculate()") } as MessRepository

    private val useCase = CalculateMessSummaryUseCase(repository)
    private val month = YearMonth.of(2026, 11)
    private val rahim = Member(id = 1, name = "Rahim", phone = "")
    private val karim = Member(id = 2, name = "Karim", phone = "")

    @Test
    fun `meal rate is total bazar divided by total meals`() {
        val summary = useCase.calculateSingleMonth(
            month = month,
            members = listOf(rahim, karim),
            meals = listOf(
                MealEntry(memberId = 1, epochDay = 1, lunch = true, dinner = true),
                MealEntry(memberId = 2, epochDay = 1, lunch = true, guests = 1),
            ),
            bazar = listOf(Bazar(epochDay = 1, amount = 400.0, category = BazarCategory.GROCERY)),
            deposits = listOf(
                Deposit(memberId = 1, epochDay = 1, amount = 300.0, method = PaymentMethod.CASH),
            ),
        )

        assertEquals(4, summary.totalMeals)
        assertEquals(100.0, summary.mealRate, 0.001)
        val rahimSummary = summary.members.first { it.member.id == 1L }
        assertEquals(200.0, rahimSummary.mealCost, 0.001)
        assertEquals(100.0, rahimSummary.balance, 0.001)
    }

    @Test
    fun `carry forward balance from previous month is added to current month balance`() {
        val octDate = LocalDate.of(2026, 10, 15).toEpochDay()
        val novDate = LocalDate.of(2026, 11, 15).toEpochDay()

        val octoberMeals = listOf(
            MealEntry(memberId = 1, epochDay = octDate, lunch = true, dinner = true), // 2 meals
        )
        val octoberBazar = listOf(
            Bazar(epochDay = octDate, amount = 500.0, category = BazarCategory.GROCERY),
        )
        val octoberDeposits = listOf(
            Deposit(memberId = 1, epochDay = octDate, amount = 1000.0, method = PaymentMethod.CASH),
        )
        // October balance: deposit 1000 - mealCost 500 = +500

        val novemberMeals = listOf(
            MealEntry(memberId = 1, epochDay = novDate, lunch = true), // 1 meal
        )
        val novemberBazar = listOf(
            Bazar(epochDay = novDate, amount = 200.0, category = BazarCategory.GROCERY),
        )
        val novemberDeposits = listOf(
            Deposit(memberId = 1, epochDay = novDate, amount = 300.0, method = PaymentMethod.CASH),
        )
        // November mealCost = 200.0
        // November balance = previousBalance (+500) + deposit (300) - mealCost (200) = +600.0

        val targetMonth = YearMonth.of(2026, 11)
        val summary = useCase.calculateWithCarryForward(
            targetMonth = targetMonth,
            members = listOf(rahim),
            allMeals = octoberMeals + novemberMeals,
            allBazar = octoberBazar + novemberBazar,
            allDeposits = octoberDeposits + novemberDeposits,
            maxMonthsBack = 2,
        )

        val rahimSummary = summary.members.first { it.member.id == 1L }
        assertEquals(500.0, rahimSummary.previousBalance, 0.001)
        assertEquals(300.0, rahimSummary.deposit, 0.001)
        assertEquals(200.0, rahimSummary.mealCost, 0.001)
        assertEquals(600.0, rahimSummary.balance, 0.001)
    }

    @Test
    fun `baki items count in total bazar and calculate outstanding baki`() {
        val summary = useCase.calculateSingleMonth(
            month = month,
            members = listOf(rahim),
            meals = listOf(MealEntry(memberId = 1, epochDay = 1, lunch = true)),
            bazar = listOf(
                Bazar(
                    epochDay = 1,
                    amount = 500.0,
                    category = BazarCategory.GROCERY,
                    paymentType = PaymentType.CASH,
                ),
                Bazar(
                    epochDay = 1,
                    amount = 300.0,
                    category = BazarCategory.VEGETABLE,
                    paymentType = PaymentType.CREDIT,
                    creditShopName = "Bhai Bhai Store",
                    isCreditSettled = false,
                ),
            ),
            deposits = emptyList(),
        )

        assertEquals(800.0, summary.totalBazar, 0.001)
        assertEquals(800.0, summary.mealRate, 0.001)
        assertEquals(300.0, summary.totalOutstandingBaki, 0.001)
    }
}

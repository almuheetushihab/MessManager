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
        val summary = useCase.calculate(
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
    fun `paidByMemberId contribution adds to member balance`() {
        val summary = useCase.calculate(
            month = month,
            members = listOf(rahim, karim),
            meals = listOf(
                MealEntry(memberId = 1, epochDay = 1, lunch = true, dinner = true),
                MealEntry(memberId = 2, epochDay = 1, lunch = true, dinner = true),
            ),
            bazar = listOf(
                Bazar(
                    epochDay = 1,
                    amount = 600.0,
                    category = BazarCategory.FISH_MEAT,
                    paidByMemberId = 1L,
                ),
            ),
            deposits = emptyList(),
        )

        assertEquals(4, summary.totalMeals)
        assertEquals(150.0, summary.mealRate, 0.001)
        val rahimSummary = summary.members.first { it.member.id == 1L }
        assertEquals(600.0, rahimSummary.paidByTotal, 0.001)
        assertEquals(300.0, rahimSummary.mealCost, 0.001)
        // balance = deposit (0) + paidByTotal (600) - mealCost (300) = 300
        assertEquals(300.0, rahimSummary.balance, 0.001)
    }

    @Test
    fun `baki items count in total bazar and calculate outstanding baki`() {
        val summary = useCase.calculate(
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
                Bazar(
                    epochDay = 1,
                    amount = 200.0,
                    category = BazarCategory.GAS,
                    paymentType = PaymentType.CREDIT,
                    creditShopName = "Gas Store",
                    isCreditSettled = true,
                ),
            ),
            deposits = emptyList(),
        )

        assertEquals(1000.0, summary.totalBazar, 0.001)
        assertEquals(1000.0, summary.mealRate, 0.001)
        assertEquals(300.0, summary.totalOutstandingBaki, 0.001)
    }

    @Test
    fun `meal rate is zero when nobody has eaten`() {
        val summary = useCase.calculate(month, listOf(rahim), emptyList(), emptyList(), emptyList())
        assertEquals(0.0, summary.mealRate, 0.0)
        assertEquals(0.0, summary.totalOutstandingBaki, 0.0)
    }
}

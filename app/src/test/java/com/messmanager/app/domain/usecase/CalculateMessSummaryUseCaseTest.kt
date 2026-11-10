package com.messmanager.app.domain.usecase

import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.domain.model.SharedExpenseShare
import com.messmanager.app.domain.model.SharedExpenseType
import com.messmanager.app.domain.model.SplitType
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
    fun `shared expenses are NOT included in meal rate and subtract from member balance`() {
        val summary = useCase.calculateSingleMonth(
            month = month,
            members = listOf(rahim, karim),
            meals = listOf(
                MealEntry(memberId = 1, epochDay = 1, lunch = true), // 1 meal
                MealEntry(memberId = 2, epochDay = 1, lunch = true), // 1 meal
            ),
            bazar = listOf(Bazar(epochDay = 1, amount = 400.0, category = BazarCategory.GROCERY)),
            deposits = listOf(
                Deposit(memberId = 1, epochDay = 1, amount = 500.0, method = PaymentMethod.CASH),
                Deposit(memberId = 2, epochDay = 1, amount = 500.0, method = PaymentMethod.CASH),
            ),
            sharedExpenses = listOf(
                SharedExpense(
                    id = 1,
                    epochDay = 1,
                    title = "WiFi Bill",
                    type = SharedExpenseType.WIFI,
                    amount = 600.0,
                    splitType = SplitType.EQUAL,
                    paidByMemberId = null, // paid from mess fund
                    shares = listOf(
                        SharedExpenseShare(expenseId = 1, memberId = 1, shareAmount = 300.0),
                        SharedExpenseShare(expenseId = 2, memberId = 2, shareAmount = 300.0),
                    ),
                ),
            ),
        )

        assertEquals(2, summary.totalMeals)
        assertEquals(400.0, summary.totalBazar, 0.001)
        assertEquals(600.0, summary.totalSharedExpenses, 0.001)
        // Meal rate should be 400 / 2 = 200, NOT affected by 600 WiFi bill
        assertEquals(200.0, summary.mealRate, 0.001)

        val rahimSummary = summary.members.first { it.member.id == 1L }
        assertEquals(200.0, rahimSummary.mealCost, 0.001)
        assertEquals(300.0, rahimSummary.sharedExpenseShare, 0.001)
        // Balance = deposit (500) + paidByTotal (0) - mealCost (200) - sharedExpenseShare (300) = 0.0
        assertEquals(0.0, rahimSummary.balance, 0.001)
    }

    @Test
    fun `member paying for shared expense gets paidByTotal added to balance`() {
        val summary = useCase.calculateSingleMonth(
            month = month,
            members = listOf(rahim, karim),
            meals = emptyList(),
            bazar = emptyList(),
            deposits = emptyList(),
            sharedExpenses = listOf(
                SharedExpense(
                    id = 1,
                    epochDay = 1,
                    title = "Electricity",
                    type = SharedExpenseType.ELECTRICITY,
                    amount = 1000.0,
                    splitType = SplitType.CUSTOM,
                    paidByMemberId = 1L, // Rahim paid out of pocket
                    shares = listOf(
                        SharedExpenseShare(expenseId = 1, memberId = 1, shareAmount = 400.0),
                        SharedExpenseShare(expenseId = 1, memberId = 2, shareAmount = 600.0),
                    ),
                ),
            ),
        )

        val rahimSummary = summary.members.first { it.member.id == 1L }
        val karimSummary = summary.members.first { it.member.id == 2L }

        assertEquals(1000.0, rahimSummary.paidByTotal, 0.001)
        assertEquals(400.0, rahimSummary.sharedExpenseShare, 0.001)
        // Rahim balance = deposit(0) + paidByTotal(1000) - mealCost(0) - share(400) = +600
        assertEquals(600.0, rahimSummary.balance, 0.001)

        assertEquals(0.0, karimSummary.paidByTotal, 0.001)
        assertEquals(600.0, karimSummary.sharedExpenseShare, 0.001)
        // Karim balance = deposit(0) + paidByTotal(0) - mealCost(0) - share(600) = -600
        assertEquals(-600.0, karimSummary.balance, 0.001)
    }

    @Test
    fun `carry forward balance from previous month includes shared expense share`() {
        val octDate = LocalDate.of(2026, 10, 15).toEpochDay()
        val novDate = LocalDate.of(2026, 11, 15).toEpochDay()

        val octoberSharedExpenses = listOf(
            SharedExpense(
                id = 1,
                epochDay = octDate,
                title = "Gas",
                type = SharedExpenseType.GAS,
                amount = 400.0,
                splitType = SplitType.EQUAL,
                shares = listOf(SharedExpenseShare(expenseId = 1, memberId = 1, shareAmount = 400.0)),
            ),
        )
        val octoberDeposits = listOf(
            Deposit(memberId = 1, epochDay = octDate, amount = 1000.0, method = PaymentMethod.CASH),
        )
        // October balance: deposit 1000 - sharedExpenseShare 400 = +600

        val novemberDeposits = listOf(
            Deposit(memberId = 1, epochDay = novDate, amount = 200.0, method = PaymentMethod.CASH),
        )

        val targetMonth = YearMonth.of(2026, 11)
        val summary = useCase.calculateWithCarryForward(
            targetMonth = targetMonth,
            members = listOf(rahim),
            allMeals = emptyList(),
            allBazar = emptyList(),
            allDeposits = octoberDeposits + novemberDeposits,
            allSharedExpenses = octoberSharedExpenses,
            maxMonthsBack = 2,
        )

        val rahimSummary = summary.members.first { it.member.id == 1L }
        assertEquals(600.0, rahimSummary.previousBalance, 0.001)
        assertEquals(200.0, rahimSummary.deposit, 0.001)
        assertEquals(800.0, rahimSummary.balance, 0.001)
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

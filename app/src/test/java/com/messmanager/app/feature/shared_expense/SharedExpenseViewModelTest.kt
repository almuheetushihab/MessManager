package com.messmanager.app.feature.shared_expense

import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.domain.model.SharedExpenseType
import com.messmanager.app.domain.model.SplitType
import com.messmanager.app.domain.repository.MessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class SharedExpenseViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val expensesFlow = MutableStateFlow<List<SharedExpense>>(emptyList())
    private val membersFlow = MutableStateFlow(
        listOf(
            Member(id = 1, name = "Rahim", phone = ""),
            Member(id = 2, name = "Karim", phone = ""),
        ),
    )
    private val selectedMonthFlow = MutableStateFlow(YearMonth.now())

    private val repository = object : MessRepository {
        override val selectedMonth = selectedMonthFlow
        override fun setSelectedMonth(month: YearMonth) { selectedMonthFlow.value = month }

        override fun observeMembers(): Flow<List<Member>> = membersFlow
        override suspend fun addMember(member: Member) {}
        override suspend fun deleteMember(id: Long) {}
        override fun observeMealsByDay(epochDay: Long) = error("Not needed")
        override fun observeMealsBetween(start: Long, end: Long) = error("Not needed")
        override suspend fun saveMeal(entry: com.messmanager.app.domain.model.MealEntry) {}
        override suspend fun saveMeals(entries: List<com.messmanager.app.domain.model.MealEntry>) {}
        override suspend fun copyMealsFromYesterday(targetEpochDay: Long) {}
        override fun observeBazar() = error("Not needed")
        override suspend fun addBazar(bazar: com.messmanager.app.domain.model.Bazar) {}
        override suspend fun updateBazar(bazar: com.messmanager.app.domain.model.Bazar) {}
        override suspend fun updateBazarCreditSettled(id: Long, isSettled: Boolean) {}
        override suspend fun deleteBazar(id: Long) {}
        override fun observeDeposits() = error("Not needed")
        override suspend fun addDeposit(deposit: com.messmanager.app.domain.model.Deposit) {}
        override suspend fun deleteDeposit(id: Long) {}

        override fun observeSharedExpenses(): Flow<List<SharedExpense>> = expensesFlow
        override fun observeSharedExpensesBetween(start: Long, end: Long): Flow<List<SharedExpense>> = expensesFlow
        override suspend fun addSharedExpense(expense: SharedExpense) {
            expensesFlow.value = expensesFlow.value + expense
        }
        override suspend fun updateSharedExpense(expense: SharedExpense) {}
        override suspend fun deleteSharedExpense(id: Long) {
            expensesFlow.value = expensesFlow.value.filter { it.id != id }
        }

        override fun observeClosedMonths(): Flow<List<YearMonth>> = flowOf(emptyList())
        override fun observeIsMonthClosed(month: YearMonth): Flow<Boolean> = flowOf(false)
        override suspend fun closeMonth(month: YearMonth) {}
        override suspend fun reopenMonth(month: YearMonth) {}
    }

    private lateinit var viewModel: SharedExpenseViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SharedExpenseViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onAddClick initializes form and custom shares for members`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAddClick()
        val form = viewModel.uiState.value.form
        assertNotNull(form)
        assertEquals(2, form?.customShares?.size)
        assertEquals("Rahim", form?.customShares?.get(0)?.memberName)
        assertEquals("Karim", form?.customShares?.get(1)?.memberName)

        collectJob.cancel()
    }

    @Test
    fun `equal split recalculates per member share when amount changes`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAddClick()
        viewModel.onAmountChange("1000")
        val form = viewModel.uiState.value.form
        assertEquals("500", form?.customShares?.get(0)?.amountText)
        assertEquals("500", form?.customShares?.get(1)?.amountText)

        collectJob.cancel()
    }

    @Test
    fun `custom split validation fails when shares sum does not equal total amount`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAddClick()
        viewModel.onTitleChange("WiFi Bill")
        viewModel.onAmountChange("1000")
        viewModel.onSplitTypeChange(SplitType.CUSTOM)
        viewModel.onCustomShareChange(1L, "400")
        viewModel.onCustomShareChange(2L, "400") // sum = 800 != 1000

        viewModel.onSave()

        val form = viewModel.uiState.value.form
        assertNotNull(form?.sharesError)

        collectJob.cancel()
    }

    @Test
    fun `custom split save succeeds when shares sum equals total amount`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAddClick()
        viewModel.onTitleChange("WiFi Bill")
        viewModel.onAmountChange("1000")
        viewModel.onSplitTypeChange(SplitType.CUSTOM)
        viewModel.onCustomShareChange(1L, "400")
        viewModel.onCustomShareChange(2L, "600") // sum = 1000

        viewModel.onSave()

        assertNull(viewModel.uiState.value.form)
        assertEquals(1, expensesFlow.value.size)
        assertEquals("WiFi Bill", expensesFlow.value[0].title)
        assertEquals(1000.0, expensesFlow.value[0].amount, 0.001)

        collectJob.cancel()
    }

    @Test
    fun `onDelete and onUndoDelete restores deleted shared expense`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        val expense = SharedExpense(id = 1, epochDay = 1, title = "Gas", type = SharedExpenseType.GAS, amount = 500.0, splitType = SplitType.EQUAL)
        expensesFlow.value = listOf(expense)

        viewModel.onDelete(expense)
        assertEquals(0, expensesFlow.value.size)
        assertEquals(expense, viewModel.uiState.value.recentlyDeletedExpense)

        viewModel.onUndoDelete()
        assertEquals(1, expensesFlow.value.size)
        assertNull(viewModel.uiState.value.recentlyDeletedExpense)

        collectJob.cancel()
    }
}

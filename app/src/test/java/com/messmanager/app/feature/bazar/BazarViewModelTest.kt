package com.messmanager.app.feature.bazar

import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.BazarUnit
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.domain.repository.MessRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BazarViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val bazarFlow = MutableStateFlow<List<Bazar>>(emptyList())
    private val membersFlow = MutableStateFlow<List<Member>>(emptyList())

    private val repository = object : MessRepository {
        override fun observeMembers(): Flow<List<Member>> = membersFlow
        override suspend fun addMember(member: Member) {}
        override suspend fun deleteMember(id: Long) {}
        override fun observeMealsByDay(epochDay: Long) = error("Not needed")
        override fun observeMealsBetween(start: Long, end: Long) = error("Not needed")
        override suspend fun saveMeal(entry: com.messmanager.app.domain.model.MealEntry) {}
        override fun observeBazar(): Flow<List<Bazar>> = bazarFlow
        override suspend fun addBazar(bazar: Bazar) { bazarFlow.value = bazarFlow.value + bazar }
        override suspend fun updateBazar(bazar: Bazar) {}
        override suspend fun updateBazarCreditSettled(id: Long, isSettled: Boolean) {}
        override suspend fun deleteBazar(id: Long) { bazarFlow.value = bazarFlow.value.filter { it.id != id } }
        override fun observeDeposits() = error("Not needed")
        override suspend fun addDeposit(deposit: com.messmanager.app.domain.model.Deposit) {}
        override suspend fun deleteDeposit(id: Long) {}
    }

    private lateinit var viewModel: BazarViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = BazarViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onAddClick initializes form with Grocery defaults`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAddClick()
        val form = viewModel.uiState.value.form
        assertNotNull(form)
        assertEquals(BazarCategory.GROCERY, form?.category)
        assertEquals(BazarUnit.KG, form?.unit)

        collectJob.cancel()
    }

    @Test
    fun `onCategoryChange updates allowed units and default unit`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAddClick()
        viewModel.onCategoryChange(BazarCategory.GAS)
        val form = viewModel.uiState.value.form
        assertEquals(BazarCategory.GAS, form?.category)
        assertEquals(BazarUnit.PIECE, form?.unit)
        assertEquals(listOf(BazarUnit.PIECE), form?.allowedUnits)

        collectJob.cancel()
    }

    @Test
    fun `onQuickAmountClick sets amount`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAddClick()
        viewModel.onQuickAmountClick(500)
        val form = viewModel.uiState.value.form
        assertEquals("500", form?.amount)

        collectJob.cancel()
    }

    @Test
    fun `onSave fails validation when credit shop name is empty`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAddClick()
        viewModel.onAmountChange("300")
        viewModel.onPaymentTypeChange(PaymentType.CREDIT)
        viewModel.onSave()
        val form = viewModel.uiState.value.form
        assertNotNull(form?.shopNameError)

        collectJob.cancel()
    }
}

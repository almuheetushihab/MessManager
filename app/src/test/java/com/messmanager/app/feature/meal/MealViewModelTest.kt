package com.messmanager.app.feature.meal

import com.messmanager.app.data.repository.SettingsRepository
import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.MealType
import com.messmanager.app.domain.model.Member
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class MealViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val members = listOf(
        Member(id = 1, name = "Rahim", phone = ""),
        Member(id = 2, name = "Karim", phone = ""),
    )

    private val mealsFlow = MutableStateFlow<List<MealEntry>>(emptyList())
    private val selectedMonthFlow = MutableStateFlow(YearMonth.now())

    private val repository = object : MessRepository {
        override val selectedMonth = selectedMonthFlow
        override fun setSelectedMonth(month: YearMonth) { selectedMonthFlow.value = month }

        override fun observeMembers(): Flow<List<Member>> = flowOf(members)
        override suspend fun addMember(member: Member) {}
        override suspend fun deleteMember(id: Long) {}

        override fun observeMealsByDay(epochDay: Long): Flow<List<MealEntry>> = mealsFlow
        override fun observeMealsBetween(start: Long, end: Long): Flow<List<MealEntry>> = mealsFlow
        override suspend fun saveMeal(entry: MealEntry) {
            mealsFlow.value = mealsFlow.value.filter { it.memberId != entry.memberId } + entry
        }
        override suspend fun saveMeals(entries: List<MealEntry>) {
            var current = mealsFlow.value
            entries.forEach { entry ->
                current = current.filter { it.memberId != entry.memberId } + entry
            }
            mealsFlow.value = current
        }

        override suspend fun copyMealsFromYesterday(targetEpochDay: Long) {}
        override fun observeBazar(): Flow<List<Bazar>> = flowOf(emptyList())
        override suspend fun addBazar(bazar: Bazar) {}
        override suspend fun updateBazar(bazar: Bazar) {}
        override suspend fun updateBazarCreditSettled(id: Long, isSettled: Boolean) {}
        override suspend fun deleteBazar(id: Long) {}
        override fun observeDeposits(): Flow<List<Deposit>> = flowOf(emptyList())
        override suspend fun addDeposit(deposit: Deposit) {}
        override suspend fun deleteDeposit(id: Long) {}
        override fun observeClosedMonths(): Flow<List<YearMonth>> = flowOf(emptyList())
        override fun observeIsMonthClosed(month: YearMonth): Flow<Boolean> = flowOf(false)
        override suspend fun closeMonth(month: YearMonth) {}
        override suspend fun reopenMonth(month: YearMonth) {}
    }

    private val settingsDataStore = object : androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> {
        private val prefs = MutableStateFlow(androidx.datastore.preferences.core.emptyPreferences())
        override val data: Flow<androidx.datastore.preferences.core.Preferences> = prefs
        override suspend fun updateData(transform: suspend (androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences): androidx.datastore.preferences.core.Preferences {
            val updated = transform(prefs.value)
            prefs.value = updated
            return updated
        }
    }

    private val settingsRepository = SettingsRepository(settingsDataStore)
    private lateinit var viewModel: MealViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MealViewModel(repository, settingsRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onToday resets date to today`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onPreviousDay()
        assertTrue(viewModel.uiState.value.date != LocalDate.now())

        viewModel.onToday()
        assertEquals(LocalDate.now(), viewModel.uiState.value.date)

        collectJob.cancel()
    }

    @Test
    fun `onAllOn sets breakfast true for all members`() = runTest(testDispatcher) {
        val collectJob = backgroundScope.launch { viewModel.uiState.collect {} }

        viewModel.onAllOn(MealType.BREAKFAST)
        val items = viewModel.uiState.value.items
        assertTrue(items.all { it.entry.breakfast })

        collectJob.cancel()
    }
}

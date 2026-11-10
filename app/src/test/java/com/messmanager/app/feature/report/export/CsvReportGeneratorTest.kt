package com.messmanager.app.feature.report.export

import com.messmanager.app.domain.model.Bazar
import com.messmanager.app.domain.model.BazarCategory
import com.messmanager.app.domain.model.BazarUnit
import com.messmanager.app.domain.model.Deposit
import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.MemberSummary
import com.messmanager.app.domain.model.MessSummary
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.domain.model.SharedExpense
import com.messmanager.app.domain.model.SharedExpenseType
import com.messmanager.app.domain.model.SplitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class CsvReportGeneratorTest {

    @Test
    fun `generateCsv starts with UTF-8 BOM`() {
        val data = MonthReportData()
        val csv = CsvReportGenerator.generateCsv(data)
        assertTrue(csv.startsWith("\uFEFF"))
    }

    @Test
    fun `generateCsvBytes starts with UTF-8 BOM bytes`() {
        val data = MonthReportData()
        val bytes = CsvReportGenerator.generateCsvBytes(data)
        assertEquals(0xEF.toByte(), bytes[0])
        assertEquals(0xBB.toByte(), bytes[1])
        assertEquals(0xBF.toByte(), bytes[2])
    }

    @Test
    fun `generateCsv includes all 5 required sections`() {
        val member1 = Member(id = 1, name = "Rahim, Ahmed", phone = "01700000000")
        val epochDay = LocalDate.of(2023, 10, 15).toEpochDay()

        val summary = MessSummary(
            month = YearMonth.of(2023, 10),
            totalBazar = 500.0,
            totalSharedExpenses = 200.0,
            totalMeals = 2,
            mealRate = 250.0,
            members = listOf(
                MemberSummary(member = member1, meals = 2, mealCost = 500.0, deposit = 1000.0),
            ),
        )

        val meals = listOf(
            MealEntry(memberId = 1, epochDay = epochDay, breakfast = true, lunch = true),
        )

        val bazarList = listOf(
            Bazar(
                id = 1,
                epochDay = epochDay,
                amount = 500.0,
                category = BazarCategory.GROCERY,
                quantity = 2.0,
                unit = BazarUnit.KG,
                paymentType = PaymentType.CASH,
                paidByMemberName = "Rahim, Ahmed",
                note = "Rice & \"Fresh\" Oil",
            ),
        )

        val depositList = listOf(
            Deposit(
                id = 1,
                memberId = 1,
                memberName = "Rahim, Ahmed",
                epochDay = epochDay,
                amount = 1000.0,
                method = PaymentMethod.BKASH,
            ),
        )

        val sharedExpenses = listOf(
            SharedExpense(
                id = 1,
                epochDay = epochDay,
                title = "WiFi Bill",
                type = SharedExpenseType.WIFI,
                amount = 200.0,
                splitType = SplitType.EQUAL,
                paidByMemberName = "Rahim, Ahmed",
            ),
        )

        val data = MonthReportData(
            summary = summary,
            meals = meals,
            bazarList = bazarList,
            depositList = depositList,
            sharedExpenses = sharedExpenses,
        )

        val csv = CsvReportGenerator.generateCsv(data)

        // Verify section headers
        assertTrue(csv.contains("--- Members Summary ---"))
        assertTrue(csv.contains("--- Daily Meal Grid ---"))
        assertTrue(csv.contains("--- Bazar List ---"))
        assertTrue(csv.contains("--- Deposit List ---"))
        assertTrue(csv.contains("--- Shared Expenses ---"))

        // Verify escaping of commas and quotes
        assertTrue(csv.contains("\"Rahim, Ahmed\""))
        assertTrue(csv.contains("\"Rice & \"\"Fresh\"\" Oil\""))

        // Verify data items presence
        assertTrue(csv.contains("WiFi Bill"))
        assertTrue(csv.contains("2023-10-15"))
    }

    @Test
    fun `escapeCsv escapes quotes, commas and newlines correctly`() {
        assertEquals("normal", CsvReportGenerator.escapeCsv("normal"))
        assertEquals("\"hello, world\"", CsvReportGenerator.escapeCsv("hello, world"))
        assertEquals("\"hello \"\"world\"\"\"", CsvReportGenerator.escapeCsv("hello \"world\""))
        assertEquals("\"line1\nline2\"", CsvReportGenerator.escapeCsv("line1\nline2"))
    }
}

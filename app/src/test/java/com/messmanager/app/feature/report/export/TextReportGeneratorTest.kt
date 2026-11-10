package com.messmanager.app.feature.report.export

import com.messmanager.app.domain.model.Member
import com.messmanager.app.domain.model.MemberSummary
import com.messmanager.app.domain.model.MessSummary
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.YearMonth

class TextReportGeneratorTest {

    @Test
    fun `generate text summary formatted correctly with refund, due, and settled members`() {
        val member1 = Member(id = 1, name = "Rahim", phone = "01711111111")
        val member2 = Member(id = 2, name = "Karim", phone = "01722222222")
        val member3 = Member(id = 3, name = "Tanvir", phone = "01733333333")

        val summary = MessSummary(
            month = YearMonth.of(2023, 10),
            totalBazar = 5000.0,
            totalSharedExpenses = 1200.0,
            totalMeals = 100,
            mealRate = 50.0,
            members = listOf(
                MemberSummary(member = member1, meals = 30, mealCost = 1500.0, deposit = 2500.0),
                MemberSummary(member = member2, meals = 40, mealCost = 2000.0, deposit = 1500.0),
                MemberSummary(member = member3, meals = 30, mealCost = 1500.0, deposit = 1500.0),
            ),
        )

        val labels = ReportLabels(
            title = "Mess Summary",
            month = "October 2023",
            totalBazar = "Total Bazar",
            mealRate = "Meal Rate",
            totalSharedBills = "Total Shared Bills",
            membersSummary = "Members Summary",
            refund = "Refund",
            due = "Due",
            settled = "Settled",
        )

        val result = TextReportGenerator.generate(summary, labels)

        assertTrue(result.contains("*Mess Summary - October 2023*"))
        assertTrue(result.contains("Total Bazar: ৳5000.00"))
        assertTrue(result.contains("Meal Rate: ৳50.00"))
        assertTrue(result.contains("Total Shared Bills: ৳1200.00"))
        assertTrue(result.contains("• Rahim: Refund ৳1000.00"))
        assertTrue(result.contains("• Karim: Due ৳500.00"))
        assertTrue(result.contains("• Tanvir: Settled"))
    }

    @Test
    fun `generate text summary with empty members list`() {
        val summary = MessSummary(
            month = YearMonth.of(2023, 10),
            totalBazar = 0.0,
            mealRate = 0.0,
            members = emptyList(),
        )

        val result = TextReportGenerator.generate(summary)

        assertTrue(result.contains("Total Bazar: ৳0.00"))
        assertTrue(result.contains("Meal Rate: ৳0.00"))
    }
}

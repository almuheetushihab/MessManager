package com.messmanager.app.feature.report.export

import com.messmanager.app.domain.model.MealEntry
import com.messmanager.app.domain.model.PaymentMethod
import com.messmanager.app.domain.model.PaymentType
import com.messmanager.app.domain.model.SplitType
import java.time.LocalDate
import java.util.Locale

object CsvReportGenerator {
    private const val BOM = "\uFEFF"

    fun generateCsv(
        data: MonthReportData,
        labels: ReportLabels = ReportLabels(),
    ): String {
        val sb = StringBuilder()
        sb.append(BOM)

        // Title & Month Header
        sb.append("=== ").append(escapeCsv(labels.title))
        if (labels.month.isNotBlank()) {
            sb.append(" - ").append(escapeCsv(labels.month))
        }
        sb.append(" ===\n\n")

        // 1. Members Summary Sheet
        sb.append("--- ").append(escapeCsv(labels.membersSummary)).append(" ---\n")
        sb.append(
            listOf(
                labels.name,
                labels.phone,
                labels.meals,
                labels.mealCost,
                labels.deposit,
                labels.bazarPaid,
                labels.sharedShare,
                labels.prevBalance,
                labels.finalBalance,
                labels.status,
            ).joinToString(",") { escapeCsv(it) },
        ).append("\n")

        data.summary.members.forEach { summary ->
            val bal = summary.balance
            val statusText = when {
                bal > 0.001 -> "${labels.refund} ${formatNumber(bal)}"
                bal < -0.001 -> "${labels.due} ${formatNumber(-bal)}"
                else -> labels.settled
            }

            sb.append(
                listOf(
                    summary.member.name,
                    summary.member.phone,
                    summary.meals.toString(),
                    formatNumber(summary.mealCost),
                    formatNumber(summary.deposit),
                    formatNumber(summary.paidByTotal),
                    formatNumber(summary.sharedExpenseShare),
                    formatNumber(summary.previousBalance),
                    formatNumber(bal),
                    statusText,
                ).joinToString(",") { escapeCsv(it) },
            ).append("\n")
        }
        sb.append("\n")

        // 2. Meal Grid (dates x members)
        sb.append("--- ").append(escapeCsv(labels.dailyMealGrid)).append(" ---\n")
        val members = data.summary.members.map { it.member }
        val headerCols = mutableListOf(labels.date)
        headerCols.addAll(members.map { it.name })
        headerCols.add(labels.dailyTotal)
        sb.append(headerCols.joinToString(",") { escapeCsv(it) }).append("\n")

        val yearMonth = data.summary.month
        val daysInMonth = yearMonth.lengthOfMonth()
        val mealsByDayAndMember = data.meals.groupBy { it.epochDay }

        val memberTotals = members.associate { it.id to 0 }.toMutableMap()
        var grandTotalMeals = 0

        for (day in 1..daysInMonth) {
            val date = yearMonth.atDay(day)
            val epoch = date.toEpochDay()
            val dayMeals: List<MealEntry> = mealsByDayAndMember[epoch] ?: emptyList()
            val mealsByMemberMap = dayMeals.associate { it.memberId to it.totalCount }

            var dailyTotal = 0
            val rowCols = mutableListOf(date.toString())
            members.forEach { member ->
                val count = mealsByMemberMap[member.id] ?: 0
                rowCols.add(count.toString())
                dailyTotal += count
                memberTotals[member.id] = (memberTotals[member.id] ?: 0) + count
            }
            rowCols.add(dailyTotal.toString())
            grandTotalMeals += dailyTotal
            sb.append(rowCols.joinToString(",") { escapeCsv(it) }).append("\n")
        }

        // Summary Row for Meal Grid
        val totalRowCols = mutableListOf(labels.total)
        members.forEach { member ->
            totalRowCols.add((memberTotals[member.id] ?: 0).toString())
        }
        totalRowCols.add(grandTotalMeals.toString())
        sb.append(totalRowCols.joinToString(",") { escapeCsv(it) }).append("\n\n")

        // 3. Bazar List
        sb.append("--- ").append(escapeCsv(labels.bazarList)).append(" ---\n")
        sb.append(
            listOf(
                labels.date,
                labels.category,
                labels.amount,
                labels.quantity,
                labels.paymentType,
                labels.paidBy,
                labels.shop,
                labels.note,
            ).joinToString(",") { escapeCsv(it) },
        ).append("\n")

        data.bazarList.sortedBy { it.epochDay }.forEach { bazar ->
            val dateStr = LocalDate.ofEpochDay(bazar.epochDay).toString()
            val qtyUnitStr = if (bazar.quantity != null && bazar.unit != null) {
                "${bazar.quantity} ${bazar.unit.name}"
            } else if (bazar.quantity != null) {
                bazar.quantity.toString()
            } else ""

            val payTypeStr = when (bazar.paymentType) {
                PaymentType.CASH -> labels.cash
                PaymentType.CREDIT -> labels.credit
            }

            val paidByStr = bazar.paidByMemberName ?: labels.messFund

            sb.append(
                listOf(
                    dateStr,
                    bazar.category.name,
                    formatNumber(bazar.amount),
                    qtyUnitStr,
                    payTypeStr,
                    paidByStr,
                    bazar.creditShopName,
                    bazar.note,
                ).joinToString(",") { escapeCsv(it) },
            ).append("\n")
        }
        sb.append("\n")

        // 4. Deposit List
        sb.append("--- ").append(escapeCsv(labels.depositList)).append(" ---\n")
        sb.append(
            listOf(
                labels.date,
                labels.member,
                labels.amount,
                labels.method,
            ).joinToString(",") { escapeCsv(it) },
        ).append("\n")

        data.depositList.sortedBy { it.epochDay }.forEach { deposit ->
            val dateStr = LocalDate.ofEpochDay(deposit.epochDay).toString()
            val methodStr = when (deposit.method) {
                PaymentMethod.CASH -> labels.cash
                PaymentMethod.BKASH -> labels.bkash
                PaymentMethod.NAGAD -> labels.nagad
                PaymentMethod.BANK -> labels.bank
            }
            sb.append(
                listOf(
                    dateStr,
                    deposit.memberName,
                    formatNumber(deposit.amount),
                    methodStr,
                ).joinToString(",") { escapeCsv(it) },
            ).append("\n")
        }
        sb.append("\n")

        // 5. Shared Expenses
        sb.append("--- ").append(escapeCsv(labels.sharedExpenses)).append(" ---\n")
        sb.append(
            listOf(
                labels.date,
                labels.expenseTitle,
                labels.type,
                labels.amount,
                labels.paidBy,
                labels.splitType,
            ).joinToString(",") { escapeCsv(it) },
        ).append("\n")

        data.sharedExpenses.sortedBy { it.epochDay }.forEach { expense ->
            val dateStr = LocalDate.ofEpochDay(expense.epochDay).toString()
            val splitStr = when (expense.splitType) {
                SplitType.EQUAL -> labels.splitEqual
                SplitType.CUSTOM -> labels.splitCustom
            }
            val paidByStr = expense.paidByMemberName ?: labels.messFund
            sb.append(
                listOf(
                    dateStr,
                    expense.title,
                    expense.type.name,
                    formatNumber(expense.amount),
                    paidByStr,
                    splitStr,
                ).joinToString(",") { escapeCsv(it) },
            ).append("\n")
        }

        return sb.toString()
    }

    fun generateCsvBytes(
        data: MonthReportData,
        labels: ReportLabels = ReportLabels(),
    ): ByteArray {
        return generateCsv(data, labels).toByteArray(Charsets.UTF_8)
    }

    fun escapeCsv(value: Any?): String {
        val str = value?.toString() ?: ""
        return if (str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
            "\"" + str.replace("\"", "\"\"") + "\""
        } else {
            str
        }
    }

    private fun formatNumber(amount: Double): String {
        return String.format(Locale.US, "%.2f", amount)
    }
}

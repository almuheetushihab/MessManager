package com.messmanager.app.feature.report.export

import com.messmanager.app.domain.model.MessSummary
import java.util.Locale

object TextReportGenerator {
    fun generate(
        summary: MessSummary,
        labels: ReportLabels = ReportLabels(),
    ): String {
        val sb = StringBuilder()
        val titleText = if (labels.month.isNotBlank()) {
            "*${labels.title} - ${labels.month}*"
        } else {
            "*${labels.title}*"
        }
        sb.append(titleText).append("\n\n")
        sb.append("${labels.totalBazar}: ").append(formatMoney(summary.totalBazar)).append("\n")
        sb.append("${labels.mealRate}: ").append(formatMoney(summary.mealRate)).append("\n")

        if (summary.totalSharedExpenses > 0.0) {
            sb.append("${labels.totalSharedBills}: ").append(formatMoney(summary.totalSharedExpenses)).append("\n")
        }

        sb.append("\n*${labels.membersSummary}:*\n")
        if (summary.members.isEmpty()) {
            sb.append("-")
        } else {
            summary.members.forEach { memberSummary ->
                val bal = memberSummary.balance
                val statusText = when {
                    bal > 0.001 -> "${labels.refund} ${formatMoney(bal)}"
                    bal < -0.001 -> "${labels.due} ${formatMoney(-bal)}"
                    else -> labels.settled
                }
                sb.append("• ").append(memberSummary.member.name).append(": ").append(statusText).append("\n")
            }
        }

        return sb.toString().trimEnd()
    }

    private fun formatMoney(amount: Double): String {
        return String.format(Locale.US, "৳%.2f", amount)
    }
}

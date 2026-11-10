package com.messmanager.app.feature.report.export

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.messmanager.app.domain.model.MessSummary
import java.io.OutputStream
import java.util.Locale

object PdfReportGenerator {
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 36f

    fun generate(
        summary: MessSummary,
        labels: ReportLabels,
        outputStream: OutputStream,
    ) {
        val pdfDocument = PdfDocument()
        var pageNum = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.parseColor("#1B5E20")
            textSize = 20f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val sectionPaint = Paint().apply {
            color = Color.BLACK
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val duePaint = Paint().apply {
            color = Color.parseColor("#C62828")
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val refundPaint = Paint().apply {
            color = Color.parseColor("#2E7D32")
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val cardBgPaint = Paint().apply {
            color = Color.parseColor("#F5F5F5")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val headerBgPaint = Paint().apply {
            color = Color.parseColor("#E0E0E0")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        var y = MARGIN + 20f

        // Title Header
        canvas.drawText(labels.pdfReportTitle, MARGIN, y, titlePaint)
        y += 18f
        if (labels.month.isNotBlank()) {
            canvas.drawText(labels.month, MARGIN, y, subtitlePaint)
            y += 20f
        }

        // Top Accent Bar
        val barPaint = Paint().apply {
            color = Color.parseColor("#2E7D32")
            style = Paint.Style.FILL
        }
        canvas.drawRect(MARGIN, y, PAGE_WIDTH - MARGIN, y + 3f, barPaint)
        y += 15f

        // 1. Summary Overview Section
        canvas.drawText(labels.pdfSummaryOverview, MARGIN, y, sectionPaint)
        y += 12f

        // Grid for summary stats (2 rows of 3 cards)
        val cardWidth = (PAGE_WIDTH - 2 * MARGIN - 20f) / 3f
        val cardHeight = 36f

        val totalDeposit = summary.members.sumOf { it.deposit }

        val stats = listOf(
            labels.totalBazar to formatMoney(summary.totalBazar),
            labels.totalDeposit to formatMoney(totalDeposit),
            labels.totalMeals to summary.totalMeals.toString(),
            labels.mealRate to formatMoney(summary.mealRate),
            labels.totalSharedBills to formatMoney(summary.totalSharedExpenses),
            labels.outstandingBaki to formatMoney(summary.totalOutstandingBaki),
        )

        for (i in stats.indices) {
            val row = i / 3
            val col = i % 3
            val left = MARGIN + col * (cardWidth + 10f)
            val top = y + row * (cardHeight + 8f)
            val right = left + cardWidth
            val bottom = top + cardHeight

            val rect = RectF(left, top, right, bottom)
            canvas.drawRoundRect(rect, 4f, 4f, cardBgPaint)
            canvas.drawRoundRect(rect, 4f, 4f, linePaint)

            val (title, value) = stats[i]
            canvas.drawText(title, left + 8f, top + 14f, subtitlePaint)
            canvas.drawText(value, left + 8f, top + 28f, boldTextPaint)
        }

        y += 2 * cardHeight + 25f

        // 2. Member Balances Section
        canvas.drawText(labels.pdfMemberBalances, MARGIN, y, sectionPaint)
        y += 15f

        // Table Header
        val colWidths = floatArrayOf(80f, 40f, 55f, 55f, 65f, 65f, 65f, 60f)
        val headers = arrayOf(
            labels.name,
            labels.meals,
            labels.mealCost,
            labels.deposit,
            labels.bazarPaid,
            labels.sharedShare,
            labels.finalBalance,
            labels.status,
        )

        val tableWidth = PAGE_WIDTH - 2 * MARGIN

        fun drawTableHeader(currentY: Float): Float {
            val headerRect = RectF(MARGIN, currentY, MARGIN + tableWidth, currentY + 20f)
            canvas.drawRect(headerRect, headerBgPaint)
            canvas.drawRect(headerRect, linePaint)

            var x = MARGIN + 4f
            for (i in headers.indices) {
                canvas.drawText(headers[i], x, currentY + 14f, boldTextPaint)
                x += colWidths[i]
            }
            return currentY + 20f
        }

        y = drawTableHeader(y)

        // Draw Member Rows
        summary.members.forEachIndexed { index, memberSummary ->
            if (y + 20f > PAGE_HEIGHT - MARGIN) {
                pdfDocument.finishPage(page)
                pageNum++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNum).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = MARGIN + 20f
                y = drawTableHeader(y)
            }

            val rowRect = RectF(MARGIN, y, MARGIN + tableWidth, y + 20f)
            if (index % 2 == 1) {
                canvas.drawRect(rowRect, cardBgPaint)
            }
            canvas.drawRect(rowRect, linePaint)

            val bal = memberSummary.balance
            val (statusText, statusPaint) = when {
                bal > 0.001 -> labels.refund to refundPaint
                bal < -0.001 -> labels.due to duePaint
                else -> labels.settled to textPaint
            }

            val rowValues = arrayOf(
                memberSummary.member.name,
                memberSummary.meals.toString(),
                formatMoney(memberSummary.mealCost),
                formatMoney(memberSummary.deposit),
                formatMoney(memberSummary.paidByTotal),
                formatMoney(memberSummary.sharedExpenseShare),
                formatMoney(bal),
                statusText,
            )

            var x = MARGIN + 4f
            for (i in rowValues.indices) {
                val p = if (i == 7) statusPaint else textPaint
                val cellText = rowValues[i]
                val truncatedText = if (i == 0 && textPaint.measureText(cellText) > colWidths[0] - 8f) {
                    cellText.take(10) + "…"
                } else cellText

                canvas.drawText(truncatedText, x, y + 14f, p)
                x += colWidths[i]
            }
            y += 20f
        }

        pdfDocument.finishPage(page)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
    }

    private fun formatMoney(amount: Double): String {
        return String.format(Locale.US, "৳%.2f", amount)
    }
}

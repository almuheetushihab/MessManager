package com.messmanager.app.feature.report.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.messmanager.app.R
import com.messmanager.app.core.util.formatMonthYear
import com.messmanager.app.domain.model.MessSummary
import java.io.File
import java.io.FileOutputStream

object ReportExporter {

    fun buildReportLabels(context: Context, summary: MessSummary): ReportLabels {
        val monthStr = summary.month.formatMonthYear()
        return ReportLabels(
            title = context.getString(R.string.report_title),
            month = monthStr,
            totalBazar = context.getString(R.string.home_total_bazar),
            totalDeposit = context.getString(R.string.report_total_deposit),
            totalMeals = context.getString(R.string.home_total_meals),
            mealRate = context.getString(R.string.home_meal_rate),
            totalSharedBills = context.getString(R.string.report_total_shared_bills),
            outstandingBaki = context.getString(R.string.bazar_outstanding_baki),
            membersSummary = context.getString(R.string.report_summary),
            dailyMealGrid = context.getString(R.string.csv_meal_grid),
            bazarList = context.getString(R.string.nav_bazar),
            depositList = context.getString(R.string.nav_deposit),
            sharedExpenses = context.getString(R.string.shared_expense_title),
            name = context.getString(R.string.members_name),
            phone = context.getString(R.string.members_phone),
            meals = context.getString(R.string.home_total_meals),
            mealCost = context.getString(R.string.report_meal_cost),
            deposit = context.getString(R.string.report_deposit),
            bazarPaid = context.getString(R.string.report_paid_by_total),
            sharedShare = context.getString(R.string.report_shared_bills),
            prevBalance = context.getString(R.string.report_previous_balance),
            finalBalance = context.getString(R.string.csv_col_balance),
            status = context.getString(R.string.csv_col_status),
            due = context.getString(R.string.report_status_due),
            refund = context.getString(R.string.report_status_refund),
            settled = context.getString(R.string.report_status_settled),
            date = context.getString(R.string.bazar_date),
            dailyTotal = context.getString(R.string.csv_col_daily_total),
            total = context.getString(R.string.csv_row_total),
            category = context.getString(R.string.bazar_category),
            amount = context.getString(R.string.bazar_amount),
            quantity = context.getString(R.string.bazar_quantity),
            paymentType = context.getString(R.string.bazar_payment_type),
            paidBy = context.getString(R.string.bazar_paid_by),
            shop = context.getString(R.string.bazar_credit_shop_name),
            note = context.getString(R.string.bazar_note),
            member = context.getString(R.string.deposit_member),
            method = context.getString(R.string.deposit_method),
            expenseTitle = context.getString(R.string.shared_expense_expense_title),
            type = context.getString(R.string.shared_expense_type),
            splitType = context.getString(R.string.shared_expense_split_type),
            messFund = context.getString(R.string.bazar_paid_by_mess_fund),
            pdfReportTitle = context.getString(R.string.pdf_report_title),
            pdfSummaryOverview = context.getString(R.string.pdf_summary_overview),
            pdfMemberBalances = context.getString(R.string.pdf_member_balances),
            cash = context.getString(R.string.payment_type_cash),
            credit = context.getString(R.string.payment_type_baki),
            bkash = context.getString(R.string.method_bkash),
            nagad = context.getString(R.string.method_nagad),
            bank = context.getString(R.string.method_bank),
            splitEqual = context.getString(R.string.shared_expense_split_equal),
            splitCustom = context.getString(R.string.shared_expense_split_custom),
        )
    }

    fun exportCsvToUri(context: Context, uri: Uri, data: MonthReportData): Boolean {
        return runCatching {
            val labels = buildReportLabels(context, data.summary)
            val bytes = CsvReportGenerator.generateCsvBytes(data, labels)
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                stream.write(bytes)
                stream.flush()
            }
            true
        }.getOrDefault(false)
    }

    fun exportPdfToUri(context: Context, uri: Uri, summary: MessSummary): Boolean {
        return runCatching {
            val labels = buildReportLabels(context, summary)
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                PdfReportGenerator.generate(summary, labels, stream)
                stream.flush()
            }
            true
        }.getOrDefault(false)
    }

    fun shareTextSummary(context: Context, summary: MessSummary) {
        val labels = buildReportLabels(context, summary)
        val text = TextReportGenerator.generate(summary, labels)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        val chooser = Intent.createChooser(intent, context.getString(R.string.report_share_summary))
        context.startActivity(chooser)
    }

    fun shareCsvFile(context: Context, data: MonthReportData) {
        runCatching {
            val labels = buildReportLabels(context, data.summary)
            val bytes = CsvReportGenerator.generateCsvBytes(data, labels)

            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mess_Report_${data.summary.month}.csv"
            val file = File(exportDir, fileName)

            FileOutputStream(file).use { out ->
                out.write(bytes)
                out.flush()
            }

            val authority = "${context.packageName}.fileprovider"
            val contentUri = FileProvider.getUriForFile(context, authority, file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, context.getString(R.string.report_share_csv))
            context.startActivity(chooser)
        }
    }

    fun sharePdfFile(context: Context, summary: MessSummary) {
        runCatching {
            val labels = buildReportLabels(context, summary)

            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val fileName = "Mess_Report_${summary.month}.pdf"
            val file = File(exportDir, fileName)

            FileOutputStream(file).use { out ->
                PdfReportGenerator.generate(summary, labels, out)
                out.flush()
            }

            val authority = "${context.packageName}.fileprovider"
            val contentUri = FileProvider.getUriForFile(context, authority, file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, context.getString(R.string.report_share_pdf))
            context.startActivity(chooser)
        }
    }
}

package com.firebaseapp.controlefinanceiro.helpers

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.ui.screens.reports.ReportDateFilter
import com.firebaseapp.controlefinanceiro.ui.screens.reports.ReportUiState

class ShareHelper(private val context: Context) {

    fun shareReportScreenToText(
        subject: String,
        reportUiState: ReportUiState,
    ): ShareHelper? {


        val period = if (reportUiState.currentFilter == ReportDateFilter.PERIOD) "${toDateWithWeekDay(reportUiState.fromDatePeriod)}\n${toDateWithWeekDay(reportUiState.toDatePeriod)}"  else reportUiState.periodLabel

        val groupedIncome = reportUiState.list
            .filter { it.first.type == WordType.Income }
            .groupBy {
                it.first.categoryName
            }

        val groupedExpense = reportUiState.list
            .filter { it.first.type == WordType.Expense }
            .groupBy {
                it.first.categoryName
            }


        var body = ""
        body += context.getString(R.string.report)
        body += "\n\n"
        body += period
        body += "\n\n"

        body += context.getString(R.string.incomes_by_category)
        body += "\n\n"
        if (groupedIncome.values.isEmpty()) {
            body += context.getString(R.string.no_incomes_found)
            body += "\n\n"
        } else {
            groupedIncome.forEach { (category, pairs) ->
                val total = pairs.sumOf { it.first.value }
                body += "$category: ${currencyFormat(total.toString())}"
                body += "\n"
            }
            val total = groupedIncome.values.sumOf { it -> it.sumOf { it.first.value } }
            body += "${context.getString(R.string.total_incomes)}: ${currencyFormat(total.toString())}"
            body += "\n\n"
        }

        body += context.getString(R.string.expenses_by_category)
        body += "\n\n"
        if (groupedExpense.values.isEmpty()) {
            body += context.getString(R.string.no_expenses_found)
            body += "\n\n"
        } else {
            groupedExpense.forEach { (category, pairs) ->
                val total = pairs.sumOf { it.first.value }
                body += "$category: ${currencyFormat(total.toString())}"
                body += "\n"
            }
            val total = groupedExpense.values.sumOf { it -> it.sumOf { it.first.value } }
            body += "${context.getString(R.string.total_expenses)}: ${currencyFormat(total.toString())}"
            body += "\n\n"
        }

        val intent = getIntentParams(subject, body)
        context.startActivity(
            Intent.createChooser(
                intent,
                context.resources.getString(R.string.share)
            )
        )
        return null
    }


    private fun getIntentParams(subject: String, body: String): Intent {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, body)
        return sharingIntent
    }
}
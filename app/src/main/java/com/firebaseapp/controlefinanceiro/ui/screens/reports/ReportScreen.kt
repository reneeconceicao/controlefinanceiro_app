package com.firebaseapp.controlefinanceiro.ui.screens.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLargest
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.helpers.ShareHelper
import com.firebaseapp.controlefinanceiro.helpers.currencyFormat
import com.firebaseapp.controlefinanceiro.helpers.dateToDateWeekString
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.DatePickerModal
import com.firebaseapp.controlefinanceiro.ui.components.hapticClick
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    viewModel: ReportViewModel = viewModel(factory = ViewModelProviders.Factory),
) {

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
    ) { innerPadding ->

        val context = LocalContext.current

        val reportUiState by viewModel.reportUiState.collectAsState()


        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.reporting),
                Modifier
                    .padding(paddingDefault()),
                fontSize = 24.sp,
            )

            ReportHeader(
                reportUiState = reportUiState,
                onNextMonthClick = {
                    viewModel.changePeriod(1)

                },
                onPreviousMonthClick = {
                    viewModel.changePeriod(-1)
                },
                onChangeToAllFilter = {
                    viewModel.changeFilter(ReportDateFilter.PERIOD)
                },
                onChangeToMonthFilter = {
                    viewModel.changeFilter(ReportDateFilter.MONTH)
                },
                onChangeToPeriodFilter = {
                    viewModel.changeFilter(ReportDateFilter.YEAR)
                },
                onChangeFromPeriodDate = { date ->
                    viewModel.changePeriodFrom(date)
                },
                onChangeToPeriodDate = { date ->
                    viewModel.changePeriodTo(date)
                }
            )

            ReportBody(
                reportUiState = reportUiState,
                onShareReport = { reportUiState ->
                    ShareHelper(context).shareReportScreenToText("", reportUiState)
                }
            )


        }


    }
}


@Composable
fun ReportBody(
    reportUiState: ReportUiState,
    onShareReport: (ReportUiState) -> Unit
) {

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


    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            stringResource(R.string.incomes_by_category),
            Modifier
                .fillMaxWidth()
                .padding(paddingSmall()),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

        if (groupedIncome.values.isEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.no_incomes_found_in_this_period),
                    Modifier
                        .padding(paddingDefault())
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            groupedIncome.forEach { (category, pairs) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = paddingDefault(), end = paddingSmall())
                ) {
                    Text(category)
                    Spacer(Modifier.weight(1f))
                    val total = pairs.sumOf { it.first.value }
                    Text(currencyFormat(total.toString()))
                }
            }


        }

        Text(
            stringResource(R.string.expenses_by_category),
            Modifier
                .fillMaxWidth()
                .padding(paddingSmall())
                .padding(top = paddingDefault()),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )

        if (groupedExpense.values.isEmpty()) {
            Column(
                Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.no_expenses_found_in_this_period),
                    Modifier
                        .padding(paddingDefault())
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp
                )
            }
        } else {
            groupedExpense.forEach { (category, pairs) ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = paddingDefault(), end = paddingSmall())
                ) {
                    Text(category)
                    Spacer(Modifier.weight(1f))
                    val total = pairs.sumOf { it.first.value }
                    Text(currencyFormat(total.toString()))
                }
            }
        }



        Text(
            stringResource(R.string.totals),
            Modifier
                .fillMaxWidth()
                .padding(paddingSmall())
                .padding(top = paddingDefault()),
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.SemiBold
        )

        Column(Modifier.padding(start = paddingSmall())) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                val totalIncome = groupedIncome.values.sumOf { it -> it.sumOf { it.first.value } }

                Text(
                    stringResource(R.string.total_incomes),
                )

                Text(currencyFormat(totalIncome.toString()))
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                val totalExpense = groupedExpense.values.sumOf { it -> it.sumOf { it.first.value } }

                Text(
                    stringResource(R.string.total_expenses),
                )

                Text(currencyFormat(totalExpense.toString()))
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val balanceLabel = when (reportUiState.currentFilter) {
                    ReportDateFilter.PERIOD -> stringResource(R.string.period_balance)
                    ReportDateFilter.MONTH -> stringResource(R.string.monthly_balance)
                    ReportDateFilter.YEAR -> stringResource(R.string.yearly_balance)
                }
                val totalIncome = groupedIncome.values.sumOf { it -> it.sumOf { it.first.value } }
                val totalExpense = groupedExpense.values.sumOf { it -> it.sumOf { it.first.value } }
                val totalBalance = totalIncome - totalExpense
                Text(
                    balanceLabel,
                )

                Text(
                    currencyFormat(totalBalance.toString()),
                    color = if (totalBalance < BigDecimal.ZERO)
                        colorResource(R.color.dark_red) else MaterialTheme.colorScheme.onBackground
                )
            }
        }




        Spacer(Modifier.height(paddingExtraLarge()))

        Button(
            onClick = hapticClick {
                onShareReport(reportUiState)
            },
            Modifier.align(Alignment.CenterHorizontally)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.share))
            }
        }

        Spacer(Modifier.height(paddingExtraLargest()))
    }

}


@Composable
fun ReportHeader(
    reportUiState: ReportUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onChangeToAllFilter: () -> Unit,
    onChangeToMonthFilter: () -> Unit,
    onChangeToPeriodFilter: () -> Unit,
    onChangeFromPeriodDate: (LocalDateTime) -> Unit,
    onChangeToPeriodDate: (LocalDateTime) -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(paddingDefault()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReportFilterChips(
            onChangeToAllFilter = onChangeToAllFilter,
            onChangeToMonthFilter = onChangeToMonthFilter,
            onChangeToPeriodFilter = onChangeToPeriodFilter
        )

        Spacer(Modifier.padding(paddingSmall()))

        ReportPeriodSelector(
            reportUiState = reportUiState,
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick,
            onChangeFromPeriodDate = onChangeFromPeriodDate,
            onChangeToPeriodDate = onChangeToPeriodDate,
        )
    }


}

@Composable
fun ReportFilterChips(
    onChangeToAllFilter: () -> Unit,
    onChangeToMonthFilter: () -> Unit,
    onChangeToPeriodFilter: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(paddingDefault()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var selected by rememberSaveable { mutableIntStateOf(0) }

        FilterChip(
            onClick = {
                selected = 0
                onChangeToMonthFilter()
            },
            label = {
                Text(
                    stringResource(R.string.month),
                    Modifier.padding(vertical = paddingSmall()),
                    fontSize = 16.sp
                )
            },
            selected = selected == 0,
            leadingIcon = if (selected == 0) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            },
            shape = RoundedCornerShape(paddingExtraLarge())
        )

        FilterChip(
            onClick = {
                selected = 1
                onChangeToPeriodFilter()
            },
            label = {
                Text(
                    stringResource(R.string.year),
                    Modifier.padding(vertical = paddingSmall()),
                    fontSize = 16.sp
                )
            },
            selected = selected == 1,
            leadingIcon = if (selected == 1) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            },
            shape = RoundedCornerShape(paddingExtraLarge())
        )

        FilterChip(
            onClick = {
                selected = 2
                onChangeToAllFilter()
            },
            label = {
                Text(
                    stringResource(R.string.period),
                    Modifier.padding(vertical = paddingSmall()),
                    fontSize = 16.sp
                )
            },
            selected = selected == 2,
            leadingIcon = if (selected == 2) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "",
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else {
                null
            },
            shape = RoundedCornerShape(paddingExtraLarge())
        )
    }
}


@Composable
fun ReportPeriodSelector(
    reportUiState: ReportUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onChangeFromPeriodDate: (LocalDateTime) -> Unit,
    onChangeToPeriodDate: (LocalDateTime) -> Unit,
) {

    var showFromDatePicker by rememberSaveable { mutableStateOf(false) }

    var showToDatePicker by rememberSaveable { mutableStateOf(false) }

    if (showFromDatePicker) {
        DatePickerModal(
            date = reportUiState.fromDatePeriod,
            onDateSelected = { selectedDate ->
                if (selectedDate != null) {
                    onChangeFromPeriodDate(selectedDate.atStartOfDay())
                }
            },
            onDismiss = {
                showFromDatePicker = false
            }
        )
    }

    if (showToDatePicker) {
        DatePickerModal(
            date = reportUiState.toDatePeriod,
            onDateSelected = { selectedDate ->
                if (selectedDate != null) {
                    onChangeToPeriodDate(selectedDate.atEndOfDay())
                }
            },
            onDismiss = {
                showToDatePicker = false
            }
        )
    }

    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (reportUiState.currentFilter != ReportDateFilter.PERIOD) {
            Button(
                onClick = onPreviousMonthClick,
                modifier = Modifier.padding(horizontal = paddingDefault()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "",
                    Modifier.size(paddingExtraLarge())

                )
            }


            Text(
                reportUiState.periodLabel,
                Modifier.padding(paddingDefault()),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )


            Button(
                onClick = onNextMonthClick,
                modifier = Modifier.padding(horizontal = paddingDefault()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "",
                    Modifier.size(32.dp)

                )
            }
        } else {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            showFromDatePicker = true
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center

                ) {
                    Text(
                        dateToDateWeekString(reportUiState.fromDatePeriod),
                        Modifier
                            .padding(paddingDefault()),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(Icons.Default.EditCalendar, null)
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            showToDatePicker = true
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center

                ) {
                    Text(
                        dateToDateWeekString(reportUiState.toDatePeriod),
                        Modifier
                            .padding(paddingDefault()),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(Icons.Default.EditCalendar, null)
                }
//                Text(
//                    dateToDateWeekString(reportUiState.fromDatePeriod),
//                    Modifier
//                        .padding(paddingDefault())
//                        .clickable {
//                            showFromDatePicker = true
//                        },
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Text(
//                    dateToDateWeekString(reportUiState.toDatePeriod),
//                    Modifier
//                        .padding(paddingDefault())
//                        .clickable {
//                            showToDatePicker = true
//                        },
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
            }
        }

    }
}

package com.firebaseapp.controlefinanceiro.ui.screens.graphics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLargest
import com.firebaseapp.controlefinanceiro.defaults.paddingLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.helpers.ColorsHelper
import com.firebaseapp.controlefinanceiro.helpers.ShareHelper
import com.firebaseapp.controlefinanceiro.helpers.dateToDateWeekString
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.DatePickerModal
import com.firebaseapp.controlefinanceiro.ui.components.hapticClick
import java.time.LocalDateTime

import com.touzalab.composecharts.components.LineChart
import com.touzalab.composecharts.components.PieChart
import com.touzalab.composecharts.data.DataPoint
import com.touzalab.composecharts.data.DataSeries
import com.touzalab.composecharts.data.PieChartSegment
import com.touzalab.composecharts.style.ChartStyle
import com.touzalab.composecharts.theme.ChartThemes
import com.touzalab.composecharts.theme.ColorPalettes
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.Currency

@Composable
fun GraphicsScreen(
    modifier: Modifier = Modifier,
    viewModel: GraphicsViewModel = viewModel(factory = ViewModelProviders.Factory),
) {

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
    ) { innerPadding ->

        val context = LocalContext.current

        val graphicsUiState by viewModel.graphicsUiState.collectAsState()


        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.charts),
                Modifier
                    .padding(paddingDefault()),
                fontSize = 24.sp,
            )

            ReportHeader(
                reportUiState = graphicsUiState,
                onNextMonthClick = {
                    viewModel.changePeriod(1)

                },
                onPreviousMonthClick = {
                    viewModel.changePeriod(-1)
                },
                onChangeToAllFilter = {
                    viewModel.changeFilter(GraphicsDateFilter.PERIOD)
                },
                onChangeToMonthFilter = {
                    viewModel.changeFilter(GraphicsDateFilter.MONTH)
                },
                onChangeToPeriodFilter = {
                    viewModel.changeFilter(GraphicsDateFilter.YEAR)
                },
                onChangeFromPeriodDate = { date ->
                    viewModel.changePeriodFrom(date)
                },
                onChangeToPeriodDate = { date ->
                    viewModel.changePeriodTo(date)
                }
            )

            GraphicsBody(
                graphicsUiState = graphicsUiState,
                onShareReport = { graphicsUiState ->
                    ShareHelper(context).shareGraphicsScreenToText("", graphicsUiState)
                }
            )


        }


    }
}


@Composable
fun GraphicsBody(
    graphicsUiState: GraphicsUiState,
    onShareReport: (GraphicsUiState) -> Unit
) {

    val context = LocalContext.current

    val tabs = listOf(stringResource(R.string.expenses), stringResource(R.string.incomes))

    val pagerState = rememberPagerState(pageCount = { tabs.size })

    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }

    val groupedIncome = graphicsUiState.list
        .filter { it.first.type == WordType.Income }
        .groupBy {
            it.first.categoryName
        }

    val groupedExpense = graphicsUiState.list
        .filter { it.first.type == WordType.Expense }
        .groupBy {
            it.first.categoryName
        }

    val groupedDateExpense = graphicsUiState.list
        .filter { it.first.type == WordType.Expense }
        .groupBy {
            it.first.date
        }

    val groupedDateExpenseMonthYear = graphicsUiState.list
        .filter { it.first.type == WordType.Expense }
        .groupBy {
            YearMonth.from(it.first.date)
        }

    val fromDate = graphicsUiState.fromDatePeriod
    val toDate = graphicsUiState.toDatePeriod

    Column() {
        PrimaryTabRow(pagerState.currentPage) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = { Text(title) })
            }
        }


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> {
                    Column(
                        Modifier
                            .background(color = MaterialTheme.colorScheme.background)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {

                        Text(
                            stringResource(R.string.expenses_by_category),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = paddingLarge())
                        )

                        var pieChartSegmentsExpenses = mutableListOf<PieChartSegment>()
                        groupedExpense.forEach { (category, pairs) ->
                            val total = pairs.sumOf { it.first.value }
                            val segment = PieChartSegment(
                                category,
                                value = total.toFloat(),
                                ColorsHelper(context).colorByNameNonComposable(pairs[0].first.categoryColor)
                            )
                            pieChartSegmentsExpenses.add(segment)
                        }
                        if (pieChartSegmentsExpenses.isNotEmpty()) {
                            PieChart(
                                segments = pieChartSegmentsExpenses,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f),
                                donut = true,
                                donutRatio = 0.6f,
                                style = if (isSystemInDarkTheme()) ChartThemes.Dark else ChartThemes.Light
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    stringResource(R.string.no_expenses_found),
                                    Modifier.padding(top = paddingLarge())
                                )
                            }
                        }

                        var points = mutableListOf<DataPoint>()

                        var tempDate = fromDate

                        var count = 1.0f

                        if (graphicsUiState.currentFilter == GraphicsDateFilter.MONTH) {

                            while (tempDate.toLocalDate() <= toDate.toLocalDate()) {

                                val pairs = groupedDateExpense[tempDate]?.toList().orEmpty()

                                val total = pairs.sumOf { it.first.value }

                                points.add(
                                    DataPoint(
                                        x = count,
                                        y = total.toFloat(),
                                        label = ""
                                    )
                                )

                                tempDate = tempDate.plusDays(1)
                                count++
                            }

                        }

                        if (graphicsUiState.currentFilter == GraphicsDateFilter.YEAR) {

                            while (tempDate.toLocalDate() <= toDate.toLocalDate()) {

                                val pairs =
                                    groupedDateExpenseMonthYear[YearMonth.from(tempDate)]?.toList()
                                        .orEmpty()

                                val total = pairs.sumOf { it.first.value }

                                points.add(
                                    DataPoint(
                                        x = count,
                                        y = total.toFloat(),
                                        label = ""
                                    )
                                )

                                tempDate = tempDate.plusMonths(1)
                                count++
                            }

                        }


                        val data = listOf(
                            DataSeries(
                                stringResource(R.string.expenses),
                                colorResource(R.color.dark_red),
                                points
                            )
                        )

                        Text(
                            stringResource(R.string.expense_trends),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = paddingLarge())
                        )

                        val locale = LocalConfiguration.current.locales[0]
                        val currencySymbol = Currency.getInstance(locale).symbol
                        val customStyle = ChartStyle(
                            backgroundColor = Color.White,
                            gridColor = Color.LightGray.copy(alpha = 0.5f),
                            axisColor = Color.DarkGray,
//                            titleStyle = TextStyle(
//                                color = Color(0xFF3F51B5),
//                                fontSize = 18.sp,
//                                fontWeight = FontWeight.Bold
//                            ),
                            legendTextStyle = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            axisLabelStyle = TextStyle(
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            ),

                            // Other customizable properties...
                        )


                        LineChart(
                            dataSeries = data,
                            xAxisTitle = if (graphicsUiState.currentFilter == GraphicsDateFilter.MONTH) stringResource(R.string.days) else stringResource(R.string.months),
                            yAxisTitle = "${stringResource(R.string.expense)} ($currencySymbol)",
                            smoothCurve = false,
                            showPoints = false,
                            fillArea = true,
                            style = if (isSystemInDarkTheme()) customStyle else ChartThemes.Light,
                            showGrid = false,
                            showTooltip = false
                        )

                        Spacer(Modifier.height(paddingExtraLargest()))
                    }
                }

                1 -> {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
//                        Text(
//                            stringResource(R.string.incomes),
//                            textAlign = TextAlign.Center,
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = 20.sp,
//                            modifier = Modifier.padding(top = paddingDefault())
//                        )

                        var pieChartSegments = mutableListOf<PieChartSegment>()
                        groupedIncome.forEach { (category, pairs) ->

                            val total = pairs.sumOf { it.first.value }
                            val segment = PieChartSegment(
                                category,
                                value = total.toFloat(),
                                ColorsHelper(context).colorByNameNonComposable(pairs[0].first.categoryColor)
                            )
                            pieChartSegments.add(segment)

                        }

                        if (pieChartSegments.isNotEmpty()) {
                            PieChart(
                                segments = pieChartSegments,
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(top = paddingDefault()),
                                donut = true,
                                donutRatio = 0.6f,
                                style = if (isSystemInDarkTheme()) ChartThemes.Dark else ChartThemes.Light
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    stringResource(R.string.no_incomes_found),
                                    Modifier.padding(top = paddingLarge())
                                )
                            }

                        }
                        Spacer(Modifier.height(paddingExtraLargest()))
                    }
                }

            }
        }


    }


    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            stringResource(R.string.expenses),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp
        )

        var pieChartSegmentsExpenses = mutableListOf<PieChartSegment>()
        groupedExpense.forEach { (category, pairs) ->
            val total = pairs.sumOf { it.first.value }
            val segment = PieChartSegment(
                category,
                value = total.toFloat(),
                ColorsHelper(context).colorByNameNonComposable(pairs[0].first.categoryColor)
            )
            pieChartSegmentsExpenses.add(segment)
        }
        if (pieChartSegmentsExpenses.isNotEmpty()) {
            PieChart(
                segments = pieChartSegmentsExpenses,
                modifier = Modifier
                    .fillMaxWidth(0.7f),
                donut = true,
                donutRatio = 0.6f,
                style = if (isSystemInDarkTheme()) ChartThemes.Dark else ChartThemes.Light
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_expenses_found))
            }
        }


        Text(
            stringResource(R.string.incomes),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = paddingDefault())
        )

        var pieChartSegments = mutableListOf<PieChartSegment>()
        groupedIncome.forEach { (category, pairs) ->

            val total = pairs.sumOf { it.first.value }
            val segment = PieChartSegment(
                category,
                value = total.toFloat(),
                ColorsHelper(context).colorByNameNonComposable(pairs[0].first.categoryColor)
            )
            pieChartSegments.add(segment)

        }

        if (pieChartSegments.isNotEmpty()) {
            PieChart(
                segments = pieChartSegments,
                modifier = Modifier
                    .fillMaxWidth(0.7f),
                donut = true,
                donutRatio = 0.6f,
                style = if (isSystemInDarkTheme()) ChartThemes.Dark else ChartThemes.Light
            )
        } else {
            Box(contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_incomes_found))
            }

        }


//        Text(
//            stringResource(R.string.totals),
//            Modifier
//                .fillMaxWidth()
//                .padding(paddingSmall())
//                .padding(top = paddingDefault()),
//            textAlign = TextAlign.Start,
//            fontWeight = FontWeight.SemiBold
//        )
//
//        Column(Modifier.padding(start = paddingSmall())) {
//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = paddingSmall()),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//
//                val totalIncome = groupedIncome.values.sumOf { it -> it.sumOf { it.first.value } }
//
//                Text(
//                    stringResource(R.string.total_incomes),
//                )
//
//                Text(currencyFormat(totalIncome.toString()))
//            }
//
//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = paddingSmall()),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//
//                val totalExpense = groupedExpense.values.sumOf { it -> it.sumOf { it.first.value } }
//
//                Text(
//                    stringResource(R.string.total_expenses),
//                )
//
//                Text(currencyFormat(totalExpense.toString()))
//            }
//
//            Row(
//                Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = paddingSmall()),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                val balanceLabel = when (reportUiState.currentFilter) {
//                    GraphicsDateFilter.PERIOD -> stringResource(R.string.period_balance)
//                    GraphicsDateFilter.MONTH -> stringResource(R.string.monthly_balance)
//                    GraphicsDateFilter.YEAR -> stringResource(R.string.yearly_balance)
//                }
//                val totalIncome = groupedIncome.values.sumOf { it -> it.sumOf { it.first.value } }
//                val totalExpense = groupedExpense.values.sumOf { it -> it.sumOf { it.first.value } }
//                val totalBalance = totalIncome - totalExpense
//                Text(
//                    balanceLabel,
//                )
//
//                Text(
//                    currencyFormat(totalBalance.toString()),
//                    color = if (totalBalance < BigDecimal.ZERO)
//                        colorResource(R.color.dark_red) else MaterialTheme.colorScheme.onBackground
//                )
//            }
//        }


        Spacer(Modifier.height(paddingExtraLarge()))

        Button(
            onClick = hapticClick {
                onShareReport(graphicsUiState)
            },
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.share))
            }
        }

        val dataSeries = listOf(
            DataSeries(
                name = "Sales 2024",
                color = ColorPalettes.Default[0],
                points = listOf(
                    DataPoint(x = 0f, y = 10f, label = "Jan"),
                    DataPoint(x = 1f, y = 15f, label = "Feb"),
                    DataPoint(x = 2f, y = 8f, label = "Mar"),
                    DataPoint(x = 3f, y = 12f, label = "Apr"),
                    DataPoint(x = 4f, y = 20f, label = "May"),
                    DataPoint(x = 5f, y = 18f, label = "Jun")
                )
            ),
            DataSeries(
                name = "Sales 2023",
                color = ColorPalettes.Default[1],
                points = listOf(
                    DataPoint(x = 0f, y = 8f, label = "Jan"),
                    DataPoint(x = 1f, y = 12f, label = "Feb"),
                    DataPoint(x = 2f, y = 10f, label = "Mar"),
                    DataPoint(x = 3f, y = 9f, label = "Apr"),
                    DataPoint(x = 4f, y = 15f, label = "May"),
                    DataPoint(x = 5f, y = 16f, label = "Jun")
                )
            )
        )

        LineChart(
            dataSeries = dataSeries,
            title = "Sales Evolution",
            xAxisTitle = "Month",
            yAxisTitle = "Sales (K$)",
            smoothCurve = false,
            showPoints = false,
            fillArea = true
        )


// Use the composable


        Spacer(Modifier.height(paddingExtraLargest()))

    }
}


@Composable
fun ReportHeader(
    reportUiState: GraphicsUiState,
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
                    Modifier.padding(paddingSmall()),
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
                    Modifier.padding(paddingSmall()),
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
                    Modifier.padding(paddingSmall()),
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
    reportUiState: GraphicsUiState,
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
        if (reportUiState.currentFilter != GraphicsDateFilter.PERIOD) {
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

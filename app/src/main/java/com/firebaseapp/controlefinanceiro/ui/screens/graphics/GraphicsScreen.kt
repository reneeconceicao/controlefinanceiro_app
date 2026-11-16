package com.firebaseapp.controlefinanceiro.ui.screens.graphics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
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
import com.firebaseapp.controlefinanceiro.defaults.paddingLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.helpers.ColorsHelper
import com.firebaseapp.controlefinanceiro.helpers.ShareHelper
import com.firebaseapp.controlefinanceiro.helpers.dateToDateWeekString
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.DatePickerModal
import java.time.LocalDateTime

import com.firebaseapp.controlefinanceiro.charts.components.LineChart
import com.firebaseapp.controlefinanceiro.charts.components.PieChart
import com.firebaseapp.controlefinanceiro.charts.data.DataPoint
import com.firebaseapp.controlefinanceiro.charts.data.DataSeries
import com.firebaseapp.controlefinanceiro.charts.data.PieChartSegment
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.Currency
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.orEmpty
import kotlin.collections.sumOf
import kotlin.collections.toList

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

            GraphicsHeader(
                onChangeToAllFilter = {
                    viewModel.changeFilter(GraphicsDateFilter.PERIOD)
                },
                onChangeToMonthFilter = {
                    viewModel.changeFilter(GraphicsDateFilter.MONTH)
                },
                onChangeToPeriodFilter = {
                    viewModel.changeFilter(GraphicsDateFilter.YEAR)
                },
            )

            GraphicsBody(
                graphicsUiState = graphicsUiState,
                onNextMonthClick = {
                    viewModel.changePeriod(1)

                },
                onPreviousMonthClick = {
                    viewModel.changePeriod(-1)
                },
                onChangeFromPeriodDate = { date ->
                    viewModel.changePeriodFrom(date)
                },
                onChangeToPeriodDate = { date ->
                    viewModel.changePeriodTo(date)
                }
            )


        }


    }
}


@Composable
fun GraphicsBody(
    graphicsUiState: GraphicsUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onChangeFromPeriodDate: (LocalDateTime) -> Unit,
    onChangeToPeriodDate: (LocalDateTime) -> Unit,

    ) {

    val context = LocalContext.current

    val tabs = listOf(
        stringResource(R.string.expenses), stringResource(R.string.incomes),
        stringResource(R.string.summary)
    )

    val pagerState = rememberPagerState(pageCount = { tabs.size })

    val scope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }

    Column() {
        PrimaryTabRow(
            pagerState.currentPage,
            Modifier.background(MaterialTheme.colorScheme.background),
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) },
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                )
            }
        }


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->

            Column() {


                when (page) {
                    0 -> {

                        ExpensePage(
                            graphicsUiState,
                            onNextMonthClick = onNextMonthClick,
                            onPreviousMonthClick = onPreviousMonthClick,
                            onChangeToPeriodDate = onChangeToPeriodDate,
                            onChangeFromPeriodDate = onChangeFromPeriodDate
                        )
                    }

                    1 -> {
                        IncomePage(
                            graphicsUiState,
                            onNextMonthClick = onNextMonthClick,
                            onPreviousMonthClick = onPreviousMonthClick,
                            onChangeToPeriodDate = onChangeToPeriodDate,
                            onChangeFromPeriodDate = onChangeFromPeriodDate
                        )
                    }

                    2 -> {
                        ComparatePage(
                            graphicsUiState,
                            onNextMonthClick = onNextMonthClick,
                            onPreviousMonthClick = onPreviousMonthClick,
                            onChangeToPeriodDate = onChangeToPeriodDate,
                            onChangeFromPeriodDate = onChangeFromPeriodDate
                        )
                    }

                }
            }
        }


    }

    Spacer(Modifier.height(paddingExtraLarge()))


// Use the composable


    Spacer(Modifier.height(paddingExtraLargest()))


}


@Composable
fun GraphicsHeader(
    onChangeToAllFilter: () -> Unit,
    onChangeToMonthFilter: () -> Unit,
    onChangeToPeriodFilter: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(paddingDefault()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GraphicsFilterChips(
            onChangeToPeriodFilter = onChangeToAllFilter,
            onChangeToMonthFilter = onChangeToMonthFilter,
            onChangeToYearFilter = onChangeToPeriodFilter
        )


    }


}

@Composable
fun GraphicsFilterChips(
    onChangeToPeriodFilter: () -> Unit,
    onChangeToMonthFilter: () -> Unit,
    onChangeToYearFilter: () -> Unit,
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
                onChangeToYearFilter()
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

//        FilterChip(
//            onClick = {
//                selected = 2
//                onChangeToPeriodFilter()
//            },
//            label = {
//                Text(
//                    stringResource(R.string.period),
//                    Modifier.padding(paddingSmall()),
//                    fontSize = 16.sp
//                )
//            },
//            selected = selected == 2,
//            leadingIcon = if (selected == 2) {
//                {
//                    Icon(
//                        imageVector = Icons.Filled.Done,
//                        contentDescription = "",
//                        modifier = Modifier.size(FilterChipDefaults.IconSize)
//                    )
//                }
//            } else {
//                null
//            },
//            shape = RoundedCornerShape(paddingExtraLarge())
//        )
    }
}


@Composable
fun GraphicsPeriodSelector(
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

    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = paddingSmall()),
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
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                }
            }

        }
    }


}


@Composable
fun ExpensePage(
    graphicsUiState: GraphicsUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onChangeFromPeriodDate: (LocalDateTime) -> Unit,
    onChangeToPeriodDate: (LocalDateTime) -> Unit,
) {

    val context = LocalContext.current

    val graphicsLayer = rememberGraphicsLayer()

    val scope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background

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

    Column(
        Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        val isSharing = remember { mutableStateOf(false) }

        Column(
            Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            GraphicsPeriodSelector(
                reportUiState = graphicsUiState,
                onPreviousMonthClick = onPreviousMonthClick,
                onNextMonthClick = onNextMonthClick,
                onChangeFromPeriodDate = onChangeFromPeriodDate,
                onChangeToPeriodDate = onChangeToPeriodDate,
            )

            Column(
                Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .drawWithContent {
                        graphicsLayer.record {
                            drawRect(color = backgroundColor, size = size)
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {

                if (isSharing.value) {
                    Text(
                        graphicsUiState.periodLabel,
                        Modifier.padding(paddingDefault()),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LaunchedEffect(isSharing.value) {
                        scope.launch {
                            val bitmap = graphicsLayer.toImageBitmap()
                            ShareHelper(context).shareGraphics(bitmap)
                            isSharing.value = false
                        }
                    }
                }

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
                        segments = pieChartSegmentsExpenses.sortedByDescending { it.value },
                        modifier = Modifier
                            .fillMaxWidth(0.7f),
                        donut = true,
                        donutRatio = 0.6f,
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

                if (pieChartSegmentsExpenses.isNotEmpty()) {
                    LineChart(
                        dataSeries = data,
                        xAxisTitle = if (graphicsUiState.currentFilter == GraphicsDateFilter.MONTH) stringResource(
                            R.string.days
                        ) else stringResource(R.string.months),
                        yAxisTitle = "${stringResource(R.string.value)} ($currencySymbol)",
                        smoothCurve = false,
                        showPoints = false,
                        fillArea = true,
                        showGrid = false,
                        showTooltip = false
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.no_expenses_found),
                            Modifier.padding(top = paddingLarge())
                        )
                    }
                }

                if (!isSharing.value) {
                    Button(
                        onClick = {
                            isSharing.value = true

                        },
                        modifier = Modifier.padding(top = paddingLarge())
                    ) {
                        Text(stringResource(R.string.share))
                    }
                    Spacer(Modifier.height(paddingExtraLargest()))
                }
            }
        }

    }
}

@Composable
fun IncomePage(
    graphicsUiState: GraphicsUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onChangeFromPeriodDate: (LocalDateTime) -> Unit,
    onChangeToPeriodDate: (LocalDateTime) -> Unit,
) {

    val context = LocalContext.current

    val graphicsLayer = rememberGraphicsLayer()

    val scope = rememberCoroutineScope()

    val backgroundColor = MaterialTheme.colorScheme.background

    val groupedIncome = graphicsUiState.list
        .filter { it.first.type == WordType.Income }
        .groupBy {
            it.first.categoryName
        }

    val groupedDateIncome = graphicsUiState.list
        .filter { it.first.type == WordType.Income }
        .groupBy {
            it.first.date
        }

    val groupedDateIncomeMonthYear = graphicsUiState.list
        .filter { it.first.type == WordType.Income }
        .groupBy {
            YearMonth.from(it.first.date)
        }

    val fromDate = graphicsUiState.fromDatePeriod
    val toDate = graphicsUiState.toDatePeriod

    Column(
        Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        val isSharing = remember { mutableStateOf(false) }

        Column(
            Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            GraphicsPeriodSelector(
                reportUiState = graphicsUiState,
                onPreviousMonthClick = onPreviousMonthClick,
                onNextMonthClick = onNextMonthClick,
                onChangeFromPeriodDate = onChangeFromPeriodDate,
                onChangeToPeriodDate = onChangeToPeriodDate,
            )

            Column(
                Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .drawWithContent {
                        graphicsLayer.record {
                            drawRect(color = backgroundColor, size = size)
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {

                if (isSharing.value) {
                    Text(
                        graphicsUiState.periodLabel,
                        Modifier.padding(paddingDefault()),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LaunchedEffect(isSharing.value) {
                        scope.launch {
                            val bitmap = graphicsLayer.toImageBitmap()
                            ShareHelper(context).shareGraphics(bitmap)
                            isSharing.value = false
                        }
                    }
                }

                Text(
                    stringResource(R.string.incomes_by_category),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = paddingLarge())
                )

                val pieChartSegmentsIncomes = mutableListOf<PieChartSegment>()
                groupedIncome.forEach { (category, pairs) ->
                    val total = pairs.sumOf { it.first.value }
                    val segment = PieChartSegment(
                        category,
                        value = total.toFloat(),
                        ColorsHelper(context).colorByNameNonComposable(pairs[0].first.categoryColor)
                    )
                    pieChartSegmentsIncomes.add(segment)
                }
                AnimatedVisibility(pieChartSegmentsIncomes.isNotEmpty()) {
                    PieChart(
                        segments = pieChartSegmentsIncomes.sortedByDescending { it.value },
                        modifier = Modifier
                            .fillMaxWidth(0.7f),
                        donut = true,
                        donutRatio = 0.6f,

                        //style = if (isSystemInDarkTheme()) ChartThemes.Dark else ChartThemes.Light
                    )
                }
                if (pieChartSegmentsIncomes.isEmpty()) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.no_incomes_found),
                            Modifier.padding(top = paddingLarge())
                        )
                    }
                }

                var points = mutableListOf<DataPoint>()

                var tempDate = fromDate

                var count = 1.0f

                if (graphicsUiState.currentFilter == GraphicsDateFilter.MONTH) {

                    while (tempDate.toLocalDate() <= toDate.toLocalDate()) {

                        val pairs = groupedDateIncome[tempDate]?.toList().orEmpty()

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
                            groupedDateIncomeMonthYear[YearMonth.from(tempDate)]?.toList()
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
                        stringResource(R.string.incomes),
                        colorResource(R.color.dark_green),
                        points
                    )
                )

                Text(
                    stringResource(R.string.income_trends),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = paddingLarge())
                )

                val locale = LocalConfiguration.current.locales[0]
                val currencySymbol = Currency.getInstance(locale).symbol

                if (pieChartSegmentsIncomes.isNotEmpty()) {
                    LineChart(
                        dataSeries = data,
                        xAxisTitle = if (graphicsUiState.currentFilter == GraphicsDateFilter.MONTH) stringResource(
                            R.string.days
                        ) else stringResource(R.string.months),
                        yAxisTitle = "${stringResource(R.string.value)} ($currencySymbol)",
                        smoothCurve = false,
                        showPoints = false,
                        fillArea = true,
                        showGrid = false,
                        showTooltip = false
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.no_incomes_found),
                            Modifier.padding(top = paddingLarge())
                        )
                    }
                }



                if (!isSharing.value) {
                    Button(
                        onClick = {
                            isSharing.value = true

                        },
                        modifier = Modifier.padding(top = paddingLarge())
                    ) {
                        Text(stringResource(R.string.share))
                    }
                    Spacer(Modifier.height(paddingExtraLargest()))
                }


            }

        }
    }
}

@Composable
fun ComparatePage(
    graphicsUiState: GraphicsUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onChangeFromPeriodDate: (LocalDateTime) -> Unit,
    onChangeToPeriodDate: (LocalDateTime) -> Unit,
) {

    val context = LocalContext.current

    val incomes = graphicsUiState.list
        .filter { it.first.type == WordType.Income }


    val expenses = graphicsUiState.list
        .filter { it.first.type == WordType.Expense }


    val groupedDateIncome = graphicsUiState.list
        .filter { it.first.type == WordType.Income }
        .groupBy {
            it.first.date
        }

    val groupedDateIncomeMonthYear = graphicsUiState.list
        .filter { it.first.type == WordType.Income }
        .groupBy {
            YearMonth.from(it.first.date)
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

    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    val backgroundColor = MaterialTheme.colorScheme.background

    Column(
        Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        val isSharing = remember { mutableStateOf(false) }

        Column(
            Modifier
                .background(color = MaterialTheme.colorScheme.background)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {


            GraphicsPeriodSelector(
                reportUiState = graphicsUiState,
                onPreviousMonthClick = onPreviousMonthClick,
                onNextMonthClick = onNextMonthClick,
                onChangeFromPeriodDate = onChangeFromPeriodDate,
                onChangeToPeriodDate = onChangeToPeriodDate,
            )

            Column(
                Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .drawWithContent {
                        graphicsLayer.record {
                            drawRect(color = backgroundColor, size = size)
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {

                if (isSharing.value) {
                    Text(
                        graphicsUiState.periodLabel,
                        Modifier.padding(paddingDefault()),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    LaunchedEffect(isSharing.value) {
                        scope.launch {
                            val bitmap = graphicsLayer.toImageBitmap()
                            ShareHelper(context).shareGraphics(bitmap)
                            isSharing.value = false
                        }
                    }
                }

                Text(
                    stringResource(R.string.income_x_expenses),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = paddingLarge())
                )


                val incomeTotal = incomes.sumOf { it.first.value }
                val incomeSegment = PieChartSegment(
                    stringResource(R.string.incomes),
                    value = incomeTotal.toFloat(),
                    colorResource(R.color.dark_green)
                )

                val expenseTotal = expenses.sumOf { it.first.value }
                val expenseSegment = PieChartSegment(
                    stringResource(R.string.expenses),
                    value = expenseTotal.toFloat(),
                    colorResource(R.color.dark_red)
                )


                AnimatedVisibility(incomes.isNotEmpty() || expenses.isNotEmpty()) {
                    PieChart(
                        segments = listOf(expenseSegment, incomeSegment),
                        modifier = Modifier
                            .fillMaxWidth(0.7f),
                        donut = true,
                        donutRatio = 0.6f,
                        //style = if (isSystemInDarkTheme()) ChartThemes.Dark else ChartThemes.Light
                    )
                }

                if (incomes.isEmpty() && expenses.isEmpty()) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.no_register_found),
                            Modifier.padding(top = paddingLarge())
                        )
                    }
                }

                var pointsIncome = mutableListOf<DataPoint>()

                var pointsExpense = mutableListOf<DataPoint>()

                var tempDate = fromDate

                var count = 1.0f

                if (graphicsUiState.currentFilter == GraphicsDateFilter.MONTH) {

                    while (tempDate.toLocalDate() <= toDate.toLocalDate()) {

                        val pairsIncome = groupedDateIncome[tempDate]?.toList().orEmpty()

                        val pairsExpense = groupedDateExpense[tempDate]?.toList().orEmpty()

                        val totalIncome = pairsIncome.sumOf { it.first.value }

                        val totalExpense = pairsExpense.sumOf { it.first.value }

                        pointsIncome.add(
                            DataPoint(
                                x = count,
                                y = totalIncome.toFloat(),
                                label = ""
                            )
                        )

                        pointsExpense.add(
                            DataPoint(
                                x = count,
                                y = totalExpense.toFloat(),
                                label = ""
                            )
                        )

                        tempDate = tempDate.plusDays(1)
                        count++
                    }

                }

                if (graphicsUiState.currentFilter == GraphicsDateFilter.YEAR) {

                    while (tempDate.toLocalDate() <= toDate.toLocalDate()) {

                        val pairsIncome =
                            groupedDateIncomeMonthYear[YearMonth.from(tempDate)]?.toList()
                                .orEmpty()

                        val pairsExpense =
                            groupedDateExpenseMonthYear[YearMonth.from(tempDate)]?.toList()
                                .orEmpty()

                        val totalIncome = pairsIncome.sumOf { it.first.value }

                        val totalExpense = pairsExpense.sumOf { it.first.value }

                        pointsIncome.add(
                            DataPoint(
                                x = count,
                                y = totalIncome.toFloat(),
                                label = ""
                            )
                        )

                        pointsExpense.add(
                            DataPoint(
                                x = count,
                                y = totalExpense.toFloat(),
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
                        pointsExpense
                    ),
                    DataSeries(
                        stringResource(R.string.incomes),
                        colorResource(R.color.dark_green),
                        pointsIncome
                    ),
                )

                Text(
                    stringResource(R.string.income_x_expenses_trends),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = paddingLarge())
                )

                val locale = LocalConfiguration.current.locales[0]
                val currencySymbol = Currency.getInstance(locale).symbol

                if (incomes.isNotEmpty() || expenses.isNotEmpty()) {
                    LineChart(
                        dataSeries = data,
                        xAxisTitle = if (graphicsUiState.currentFilter == GraphicsDateFilter.MONTH) stringResource(
                            R.string.days
                        ) else stringResource(R.string.months),
                        yAxisTitle = "${stringResource(R.string.value)} ($currencySymbol)",
                        smoothCurve = false,
                        showPoints = false,
                        fillArea = true,
                        showGrid = false,
                        showTooltip = false,

                        )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.no_register_found),
                            Modifier.padding(top = paddingLarge())
                        )
                    }
                }

                if (!isSharing.value) {
                    Button(
                        onClick = {
                            isSharing.value = true

                        },
                        modifier = Modifier.padding(top = paddingLarge())
                    ) {
                        Text(stringResource(R.string.share))
                    }
                    Spacer(Modifier.height(paddingExtraLargest()))
                }
            }


        }
    }
}
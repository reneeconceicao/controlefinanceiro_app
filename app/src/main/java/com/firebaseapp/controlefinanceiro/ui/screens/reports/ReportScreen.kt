package com.firebaseapp.controlefinanceiro.ui.screens.reports

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.compose_lucide_icons.helpers.IconsHelper
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingExtraLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.helpers.currencyFormat
import com.firebaseapp.controlefinanceiro.helpers.dateToDateWeekString
import com.firebaseapp.controlefinanceiro.helpers.dateToString
import com.firebaseapp.controlefinanceiro.helpers.formatedPriceIndicator
import com.firebaseapp.controlefinanceiro.helpers.toDay
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.CardBordered
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
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(
                onClick = hapticClick {

                },
            ) {
                Row(
                    Modifier.padding(horizontal = paddingDefault(), vertical = paddingSmall()),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    Text("Share")
                }
            }


        }) { innerPadding ->

        val reportUiState by viewModel.reportUiState.collectAsState()

//        AnimatedVisibility(visible = !viewModel.loading.value, enter = fadeIn()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.app_name),
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
                showWordOptions = { word, index ->
//                        currentWord = Pair(word, index)
//                        showWordOptions = true
                },
                onEditNavigate = {},//navigateToEdit,
                onEditNavigateRecurring = { _, _ -> },//navigateToEditRecurring
            )
        }


    }
}


@Composable
fun ReportBody(
    reportUiState: ReportUiState,
    showWordOptions: (Int?, Long?) -> Unit,
    onEditNavigate: (Int) -> Unit,
    onEditNavigateRecurring: (Int, Long?) -> Unit,
) {

    val grouped = reportUiState.list.groupBy {
        it.first.date.toLocalDate().atStartOfDay()
    }

    AnimatedContent(
        targetState = grouped,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) { target ->
        if (target.values.isEmpty()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    stringResource(R.string.no_register_found),
                    Modifier
                        .padding(paddingLarge())
                        .padding(top = paddingLarge())
                        .fillMaxWidth(),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
            }

        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
                target.forEach { (date, words) ->
                    item {
                        Text(
                            toDay(date),
                            modifier = Modifier
                                .padding(start = paddingDefault())
                                .padding(top = paddingSmall())
                        )
                    }
                    items(items = words) { (word, index) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showWordOptions(word.id, index)// = true

                                }
                        ) {

                            Row(
                                Modifier.padding(start = paddingDefault()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {


                                Box(
                                    Modifier
                                        .size(paddingExtraLarge())
                                    //.clip(CircleShape)
                                    //.background(MaterialTheme.colorScheme.outlineVariant)

                                ) {

                                    val icon = word.categoryIcon

                                    if (icon.isNotEmpty()) {

                                        Icon(
                                            painter = IconsHelper.iconByName(icon),
                                            "",
                                            Modifier
                                                .align(Alignment.Center)
                                                .size(24.dp)
                                        )
                                    } else {
                                        Icon(
                                            painter = if (word.type == WordType.Income) painterResource(
                                                R.drawable.__banknote_arrow_up
                                            ) else painterResource(R.drawable.__banknote_arrow_down),
                                            "",
                                            Modifier
                                                .align(Alignment.Center)
                                                .size(24.dp)
                                        )
                                    }
                                }


                                Text(
                                    formatedPriceIndicator(word),
                                    Modifier
                                        .padding(paddingDefault())

                                        .padding(vertical = paddingSmall()),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (word.type == WordType.Income)
                                        colorResource(R.color.dark_green)
                                    else
                                        MaterialTheme.colorScheme.onBackground
                                )
                            }

                        }
                    }
                    item {
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                    }
                }
            }
        }
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

        ReportBalancePanel(reportUiState = reportUiState)

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
fun ReportBalancePanel(reportUiState: ReportUiState) {
    CardBordered(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingLarge(), bottom = paddingDefault())

    ) {
        Column {
            val balanceLabel = when (reportUiState.currentFilter) {
                ReportDateFilter.PERIOD -> stringResource(R.string.period_balance)
                ReportDateFilter.MONTH -> stringResource(R.string.monthly_balance)
                ReportDateFilter.YEAR -> stringResource(R.string.yearly_balance)
            }
            Text(
                balanceLabel,
                Modifier
                    .padding(paddingSmall())
                    .fillMaxWidth(),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            val colorText = if (reportUiState.total >= BigDecimal.ZERO)
                colorResource(R.color.dark_green)
            else colorResource(R.color.dark_red)


            Text(
                currencyFormat(reportUiState.total.toString()),

                Modifier
                    .padding(bottom = paddingDefault())
                    .fillMaxWidth()
                    .animateContentSize(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = colorText,
                fontWeight = FontWeight.Bold
            )


        }


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
                Text(
                    dateToDateWeekString(reportUiState.fromDatePeriod),
                    Modifier
                        .padding(paddingDefault())
                        .clickable {
                            showFromDatePicker = true
                        },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    dateToDateWeekString(reportUiState.toDatePeriod),
                    Modifier
                        .padding(paddingDefault())
                        .clickable {
                            showToDatePicker = true
                        },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}

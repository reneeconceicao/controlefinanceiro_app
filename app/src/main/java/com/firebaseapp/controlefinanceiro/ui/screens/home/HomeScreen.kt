package com.firebaseapp.controlefinanceiro.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.firebaseapp.controlefinanceiro.defaults.paddingLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.currencyFormat
import com.firebaseapp.controlefinanceiro.helpers.formatedPriceIndicator
import com.firebaseapp.controlefinanceiro.helpers.toDay
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.CardBordered
import java.math.BigDecimal
import java.util.Calendar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
    navigateToEdit: (id: Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = ViewModelProviders.Factory),
) {

    val homeUiState = viewModel.homeUiState.collectAsState()

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        floatingActionButton = {

            FloatingActionButton(
                onClick = {
                    navigateToRegister()
                },
            ) {
                Row(
                    Modifier.padding(horizontal = paddingDefault(), vertical = paddingSmall()),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "")
                    Text("Register")
                }
            }


        }) { innerPadding ->
        AnimatedVisibility(visible = !viewModel.loading.value, enter = fadeIn()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    text = stringResource(R.string.app_name),
                    Modifier
                        .padding(paddingDefault()),
                    fontSize = 24.sp,
                )

                HomeHeader(
                    homeUiState = homeUiState.value,
                    onNextMonthClick = {
                        viewModel.changePeriod(1)

                    },
                    onPreviousMonthClick = {
                        viewModel.changePeriod(-1)
                    },
                    onChangeToAllFilter = {
                        viewModel.changeFilter(DateFilter.ALL)
                    },
                    onChangeToMonthFilter = {
                        viewModel.changeFilter(DateFilter.MONTH)
                    },
                    onChangeToYearFilter = {
                        viewModel.changeFilter(DateFilter.YEAR)
                    }
                )

                HomeList(homeUiState = homeUiState.value, onEditNavigate = navigateToEdit )
            }
        }
    }

}

@Composable
fun HomeList(
    homeUiState: HomeUiState,
    onEditNavigate: (Int) -> Unit,
) {

    val grouped = homeUiState.list.groupBy {
        Calendar.getInstance().apply {
            time = it.date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
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
                    "No register found",
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
                    items(items = words) { word ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = paddingTiny())
                                .clickable {
                                    onEditNavigate(word.id)
                                }
                        ) {

                            Row(
                                Modifier.padding(start = paddingDefault()),
                                verticalAlignment = Alignment.CenterVertically
                            ) {


                                Box(
                                    Modifier
                                        .size(paddingExtraLarge())
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.outlineVariant)

                                ) {
                                    Icon(
                                        imageVector = if (word.type == WordType.Income) Icons.Default.ArrowOutward else Icons.Default.Payments,
                                        "",
                                        Modifier
                                            .align(Alignment.Center)
                                            .size(paddingDefault())
                                    )
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
}

@Composable
fun HomeHeader(
    homeUiState: HomeUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onChangeToAllFilter: () -> Unit,
    onChangeToMonthFilter: () -> Unit,
    onChangeToYearFilter: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(paddingDefault()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilterChips(
            onChangeToAllFilter = onChangeToAllFilter,
            onChangeToMonthFilter = onChangeToMonthFilter,
            onChangeToYearFilter = onChangeToYearFilter
        )

        BalancePanel(homeUiState = homeUiState)

        PeriodSelector(
            homeUiState = homeUiState,
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick
        )
    }


}

@Composable
fun FilterChips(
    onChangeToAllFilter: () -> Unit,
    onChangeToMonthFilter: () -> Unit,
    onChangeToYearFilter: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(paddingDefault()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var selected by remember { mutableIntStateOf(0) }

        FilterChip(
            onClick = {
                selected = 0
                onChangeToAllFilter()
            },
            label = {
                Text("All", Modifier.padding(paddingSmall()), fontSize = 16.sp)
            },
            selected = selected == 0,
            leadingIcon = if (selected == 0) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
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
                onChangeToMonthFilter()
            },
            label = {
                Text("Month", Modifier.padding(paddingSmall()), fontSize = 16.sp)
            },
            selected = selected == 1,
            leadingIcon = if (selected == 1) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
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
                onChangeToYearFilter()
            },
            label = {
                Text("Year", Modifier.padding(paddingSmall()), fontSize = 16.sp)
            },
            selected = selected == 2,
            leadingIcon = if (selected == 2) {
                {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = "Done icon",
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
fun BalancePanel(homeUiState: HomeUiState) {
    CardBordered(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = paddingLarge(), bottom = paddingDefault())

    ) {
        Column {
            val balanceLabel = when (homeUiState.currentFilter) {
                DateFilter.ALL -> "Total balance"
                DateFilter.MONTH -> "Monthly balance"
                DateFilter.YEAR -> "Yearly balance"
            }
            Text(
                balanceLabel,
                Modifier
                    .padding(paddingSmall())
                    .fillMaxWidth(),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            val colorText = if (homeUiState.total >= BigDecimal.ZERO)
                colorResource(R.color.dark_green)
            else colorResource(R.color.dark_red)


            val signal = if (homeUiState.total < BigDecimal.ZERO) "-" else ""
            Text(
                "$signal${currencyFormat(homeUiState.total.toString())}",

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
fun PeriodSelector(
    homeUiState: HomeUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (homeUiState.currentFilter != DateFilter.ALL) {
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
                    contentDescription = "Back",
                    Modifier.size(paddingExtraLarge())

                )
            }
        }

        Text(
            homeUiState.periodLabel,
            Modifier.padding(paddingDefault()),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        if (homeUiState.currentFilter != DateFilter.ALL) {
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
                    contentDescription = "Forward",
                    Modifier.size(32.dp)

                )
            }
        }

    }
}



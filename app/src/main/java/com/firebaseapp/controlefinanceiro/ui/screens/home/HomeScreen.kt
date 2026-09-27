package com.firebaseapp.controlefinanceiro.ui.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.firebaseapp.controlefinanceiro.defaults.paddingLarge
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.currencyFormat
import com.firebaseapp.controlefinanceiro.helpers.formatedPriceIndicator
import com.firebaseapp.controlefinanceiro.helpers.toDay
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.AnimatedText
import com.firebaseapp.controlefinanceiro.ui.components.CardBordered
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
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

            HomeList(homeUiState = homeUiState.value)
        }
    }

}

@Composable
fun HomeList(
    homeUiState: HomeUiState,
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
                        Text(toDay(date), modifier = Modifier.padding(paddingDefault()))
                    }
                    items(items = words) { word ->
                        CardBordered(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = paddingDefault(),
                                    vertical = paddingTiny()
                                )
                        ) {
                            Text(
                                formatedPriceIndicator(word),
                                Modifier.padding(paddingDefault()),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (word.type == WordType.Income)
                                    colorResource(R.color.dark_green)
                                else
                                    colorResource(
                                        R.color.dark_red
                                    )
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
    Box(
        Modifier
            .fillMaxWidth()
            .padding(paddingDefault())
    ) {
        Row(
            Modifier
                .align(Alignment.Center),
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
                        Modifier.size(32.dp)

                    )
                }
            }

            Text(homeUiState.periodLabel)

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

            Spacer(Modifier.weight(1f))

        }

        Column(
            Modifier.align(Alignment.CenterEnd)
        ) {

            Button(onClick = onChangeToAllFilter) {
                Text("All")
            }

            Button(onClick = onChangeToMonthFilter) {
                Text("Month")
            }

            Button(onClick = onChangeToYearFilter) {
                Text("Year")
            }
        }

    }



    CardBordered(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingDefault(), vertical = paddingTiny())
    ) {
//        AnimatedContent(
//            targetState = homeUiState.total,
//            transitionSpec = {
//                scaleIn() togetherWith fadeOut()
//            }
//        ) { target ->
        Text(
            currencyFormat(homeUiState.total.toString()),
            Modifier
                .padding(paddingDefault())
                .fillMaxWidth()
                .animateContentSize(),
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            color = if (homeUiState.total >= BigDecimal.ZERO) colorResource(R.color.dark_green) else colorResource(
                R.color.dark_red
            ),
            fontWeight = FontWeight.Bold
        )
//        }


    }
}



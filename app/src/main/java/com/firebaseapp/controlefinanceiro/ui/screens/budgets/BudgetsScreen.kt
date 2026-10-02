package com.firebaseapp.controlefinanceiro.ui.screens.budgets

import com.firebaseapp.controlefinanceiro.ui.screens.home.DateFilter
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeUiState
import com.firebaseapp.controlefinanceiro.ui.screens.home.HomeViewModel
import kotlin.collections.component1
import kotlin.collections.component2

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
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import com.firebaseapp.controlefinanceiro.helpers.currencyFormat
import com.firebaseapp.controlefinanceiro.helpers.formatedPrice
import com.firebaseapp.controlefinanceiro.helpers.formatedPriceIndicator
import com.firebaseapp.controlefinanceiro.helpers.toDay
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.CardBordered
import com.firebaseapp.controlefinanceiro.ui.components.LinearProgressBar
import java.math.BigDecimal
import java.util.Calendar

@Composable
fun BudgetScreen(
    modifier: Modifier = Modifier,
    navigateToRegister: () -> Unit,
    navigateToEdit: (id: Int) -> Unit,
    viewModel: BudgetViewModel = viewModel(factory = ViewModelProviders.Factory),
) {

    val homeUiState = viewModel.budgetUiState.collectAsState()

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
                    Text(stringResource(R.string.add_register))
                }
            }


        }) { innerPadding ->
        AnimatedVisibility(visible = !viewModel.loading.value, enter = fadeIn()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Text(
                    text = stringResource(R.string.budgets),
                    Modifier
                        .padding(paddingDefault()),
                    fontSize = 24.sp,
                )

                BudgetsHeader(
                    budgetUiState = homeUiState.value,
                    onNextMonthClick = {
                        viewModel.changePeriod(1)

                    },
                    onPreviousMonthClick = {
                        viewModel.changePeriod(-1)
                    },
                )

                BudgetsList(homeUiState = homeUiState.value, onEditNavigate = navigateToEdit)
            }
        }
    }

}


@Composable
fun BudgetsHeader(
    budgetUiState: BudgetUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(paddingDefault()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        PeriodSelector(
            budgetUiState = budgetUiState,
            onPreviousMonthClick = onPreviousMonthClick,
            onNextMonthClick = onNextMonthClick
        )
    }
}


@Composable
fun PeriodSelector(
    budgetUiState: BudgetUiState,
    onNextMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

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
            budgetUiState.periodLabel,
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


    }
}

@Composable
fun BudgetsList(
    homeUiState: BudgetUiState,
    onEditNavigate: (Int) -> Unit,
) {

    val list = homeUiState.list

    AnimatedContent(
        targetState = list,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        }
    ) { list ->
        if (list.isEmpty()) {
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

                items(items = list) { budget ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()

                            .clickable {
                                onEditNavigate(budget.id)
                            }
                    ) {

                        Row(
                            Modifier.padding(paddingDefault()),
                            verticalAlignment = Alignment.CenterVertically
                        ) {


                            Box(
                                Modifier
                                    .size(paddingExtraLarge())
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.outlineVariant)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrackChanges,
                                    "",
                                    Modifier
                                        .align(Alignment.Center)
                                        .size(paddingDefault())
                                )
                            }

                            Column(Modifier.padding(start = paddingDefault())) {
                                Row {
                                    Text("Goal: ")
                                    Text(
                                        currencyFormat(budget.value.toString()),

                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }

                                Row {
                                    Text("Current value: ")
                                    Text(
                                        currencyFormat(budget.currentValue.toString()),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (budget.currentValue < budget.value)
                                            colorResource(R.color.dark_green)
                                        else
                                            colorResource(R.color.dark_red)
                                    )
                                }
                            }

                        }

                        LinearProgressBar(
                            currentProgress = budget.currentValue.toFloat()/budget.value.toFloat(),
                            modifier = Modifier.fillMaxWidth().padding(paddingDefault()).height(paddingLarge()),
                        )

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




package com.firebaseapp.controlefinanceiro.ui.screens.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.currencyFormat
import com.firebaseapp.controlefinanceiro.helpers.formatedPriceIndicator
import com.firebaseapp.controlefinanceiro.ui.ViewModelProviders
import com.firebaseapp.controlefinanceiro.ui.components.AnimatedText
import com.firebaseapp.controlefinanceiro.ui.components.CardBordered
import java.math.BigDecimal
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


            val number = remember { mutableStateOf(BigDecimal.ZERO) }

            val grouped = homeUiState.value.list.groupBy {
                Calendar.getInstance().apply {
                    time = it.date
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
            }

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
                }
            )

            LazyColumn(contentPadding = PaddingValues(bottom = 120.dp)) {
                grouped.forEach { bool, ints ->
                    item {
                        Text("List is $bool", modifier = Modifier.padding(paddingDefault()))
                    }
                    items(items = ints) { number ->
                        CardBordered(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = paddingDefault(), vertical = paddingTiny())
                        ) {
                            Text(
                                //formatedPriceIndicator(number.toBigDecimal()),
                                number.notes,
                                Modifier.padding(paddingDefault()),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
//                                color = if (number < 10) colorResource(R.color.dark_green) else colorResource(
//                                    R.color.dark_red
//                                )
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
    onPreviousMonthClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(paddingDefault()),
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
                contentDescription = "Back",
                Modifier.size(32.dp)

            )
        }

        Text(homeUiState.periodLabel)

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


    CardBordered(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = paddingDefault(), vertical = paddingTiny())
    ) {
        Text(
            currencyFormat(homeUiState.total) ,
            Modifier
                .padding(paddingDefault())
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = colorResource(R.color.dark_green),
            fontWeight = FontWeight.Bold
        )
    }
}



package com.firebaseapp.controlefinanceiro.ui.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.firebaseapp.controlefinanceiro.R
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.dateToString
import java.text.DecimalFormatSymbols
import java.util.Date

@Composable
fun Calculator(
    initialValue: String,
    initialDate: Date,
    viewModel: CalculatorViewModal = viewModel {
        CalculatorViewModal(initialValue = initialValue)
    },
    onEntryValue: (String) -> Unit,
    showDateDialog: () -> Unit,
    onUpdatePrice: (String) -> Unit,
) {
    val separator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()

    val input by viewModel.input.collectAsState()

    val buttonState = viewModel.buttonState.collectAsState().value


    LaunchedEffect(input) { viewModel.evaluateExpression() }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(paddingDefault()),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        // Input display
        Text(
            text = input,
            Modifier
                .fillMaxWidth()
                .padding(end = paddingDefault()),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = TextStyle(
                fontSize = 40.sp,
                textAlign = TextAlign.End,
            )
        )


        //First row 7, 8, 9,,Date
        val firstRow = listOf("7", "8", "9") // and Date
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            firstRow.forEach {
                Button(
                    onClick = { viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Button(
                onClick = { showDateDialog() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(paddingTiny()),

                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = colorResource(R.color.yellow),
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                ),
            ) {
                Row(
                    Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, "")
                    Spacer(Modifier.padding(paddingTiny()))
                    val date = dateToString(initialDate)
                    Text(
                        text = date,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 2,
                        fontSize = 12.sp
                    )
                }

            }

        }

        val secondRow = listOf("4", "5", "6") // and Plus and Minus
        Row(horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            secondRow.forEach {
                Button(
                    onClick = { viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(paddingTiny())
            ) {
                Button(
                    onClick = { viewModel.onClick("-", {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Row() {
                        Text(
                            text = "-",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                }

                Button(
                    onClick = { viewModel.onClick("x", {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Row() {
                        Text(
                            text = "x",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                }
            }
        }

        val thirtyRow = listOf("1", "2", "3") // and Multiplication and Divide
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            thirtyRow.forEach {
                Button(
                    onClick = { viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(paddingTiny())
            ) {
                Button(
                    onClick = { viewModel.onClick("+", {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Row() {
                        Text(
                            text = "+",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                }

                Button(
                    onClick = { viewModel.onClick("÷", {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Row() {
                        Text(
                            text = "÷",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                }
            }
        }


        val lastRow = listOf("0", separator) // more delete and Equal/Done
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            lastRow.forEach {
                Button(
                    onClick = { viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Button(
                onClick = { viewModel.onClick("⌫", {}, onUpdateValue = onUpdatePrice) },
                modifier = Modifier.weight(0.5f),
                shape = RoundedCornerShape(paddingTiny()),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                ),
            ) {
                Text(
                    text = "⌫",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace
                )
            }


            Button(
                onClick = {
                    if (buttonState == CalculatorViewModal.ButtonState.DONE) {
                        viewModel.onClick("=", onEntryValue)
                    } else {
                        viewModel.onClick("=")
                    }
                },
                enabled = buttonState != CalculatorViewModal.ButtonState.DISABLED,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(paddingTiny()),
                colors = ButtonColors(
                    containerColor = colorResource(R.color.yellow),
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContainerColor = Color(0xFFBDBDBD),
                ),
            ) {
                Text(
                    text = if (buttonState == CalculatorViewModal.ButtonState.EQUAL) {
                        "="
                    } else {
                        "✓"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

        }

    }

}

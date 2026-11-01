package com.firebaseapp.controlefinanceiro.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.firebaseapp.controlefinanceiro.ui.components.hapticClick
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterDetails
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterUiState
import java.text.DecimalFormatSymbols

@Composable
fun Calculator(
    details: RegisterDetails,
    showResultEmbedded: Boolean = false,
    viewModel: CalculatorViewModal = viewModel {
        CalculatorViewModal(initialValue = details.price)
    },
    onEntryValue: (RegisterDetails) -> Unit,
    showDateDialog: () -> Unit,
    onUpdatePrice: (String) -> Unit,
    onUpdateInput: (String) -> Unit,
    isCompleteCalculator: Boolean = false
) {

    val separator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()

    val input by viewModel.input.collectAsState()

    val buttonState = viewModel.buttonState.collectAsState().value


    LaunchedEffect(input) {
        viewModel.evaluateExpression()
        onUpdateInput(input)
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(paddingDefault()),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        // Output display
        if (showResultEmbedded) {
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
        }

        //First row 7, 8, 9,Date
        val firstRow = listOf("7", "8", "9") // and Date
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            firstRow.forEach {
                Button(
                    onClick = hapticClick { viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Button(
                onClick = hapticClick {
                    showDateDialog()
                },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 0.dp, vertical = ButtonDefaults.ContentPadding.calculateTopPadding()),

                shape = RoundedCornerShape(paddingTiny()),

                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = colorResource(R.color.yellow),
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                ),
            ) {
                Row(
                    Modifier.padding(vertical = 2.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Default.CalendarMonth, "", tint = colorResource(R.color.yellow))
                    val date = dateToString(details.date)
                    Spacer(Modifier.padding(horizontal = 1.dp))
                    Text(
                        text = date,
                        color = colorResource(R.color.yellow),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        fontSize = 14.sp
                    )
                }

            }

        }

        val secondRow = listOf("4", "5", "6") // and Minus and Multiplication
        Row(horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            secondRow.forEach {
                Button(
                    onClick = hapticClick { viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Normal,
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
                    onClick = hapticClick { viewModel.onClick("-", {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Row() {
                        Text(
                            text = "–",
                            fontWeight = FontWeight.Normal,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                }
                if (isCompleteCalculator) {
                    Button(
                        onClick = hapticClick {
                            viewModel.onClick(
                                "x",
                                {},
                                onUpdateValue = onUpdatePrice
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(paddingTiny()),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                        ),
                    ) {
                        Row() {
                            Text(
                                text = "x",
                                fontWeight = FontWeight.Normal,
                                fontSize = 22.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                    }
                }
            }
        }

        val thirtyRow = listOf("1", "2", "3") // and Multiplication and Divide
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            thirtyRow.forEach {
                Button(
                    onClick = hapticClick { viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Normal,
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
                    onClick = hapticClick { viewModel.onClick("+", {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Row() {
                        Text(
                            text = "+",
                            fontWeight = FontWeight.Normal,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                }
                if (isCompleteCalculator) {
                    Button(
                        onClick = hapticClick {
                            viewModel.onClick(
                                "÷",
                                {},
                                onUpdateValue = onUpdatePrice
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(paddingTiny()),
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color.Gray,
                        ),
                    ) {
                        Row() {
                            Text(
                                text = "÷",
                                fontWeight = FontWeight.Normal,
                                fontSize = 22.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                    }
                }
            }
        }


        val lastRow = listOf("0", separator) // more delete and Equal/Done
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            lastRow.forEach {
                Button(
                    onClick = hapticClick { viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice) },
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(paddingTiny()),
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                    ),
                ) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Button(
                onClick = hapticClick { viewModel.onClick("⌫", {}, onUpdateValue = onUpdatePrice) },
                modifier = Modifier.weight(0.5f),
                shape = RoundedCornerShape(paddingTiny()),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                ),
            ) {
                Text(
                    text = "⌫",
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            val context = LocalContext.current
            Button(
                onClick = if (buttonState == CalculatorViewModal.ButtonState.DONE) hapticClick {
                    viewModel.onClick("=", onSave = {onEntryValue(details.copy(price = it))})
                } else hapticClick {
                    viewModel.onClick("=")
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
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace
                )
            }

        }

    }

}

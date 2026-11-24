package com.firebaseapp.controlefinanceiro.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
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
import com.firebaseapp.controlefinanceiro.defaults.paddingSmall
import com.firebaseapp.controlefinanceiro.defaults.paddingTiny
import com.firebaseapp.controlefinanceiro.helpers.dateToString
import com.firebaseapp.controlefinanceiro.ui.screens.register.RegisterDetails
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
    onRepeatChange: (Boolean) -> Unit,
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
            .padding(paddingSmall()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(paddingTiny())
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

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            Box(
                modifier = Modifier
                    .weight(0.49f)
                    .clickable(interactionSource = null, indication = null) {
                        showDateDialog()
                    }
                    .clip(RoundedCornerShape(paddingTiny()))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingSmall()),
                contentAlignment = Alignment.Center) {
                Row(
                    Modifier
                        .padding(vertical = 2.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(imageVector = Icons.Outlined.AccountBalanceWallet, null)
                }

            }

            Box(
                modifier = Modifier
                    .weight(0.49f)
                    .clickable(interactionSource = null, indication = null) {
                        if (!details.isEditRecurring) {
                            onRepeatChange(!details.isRecurring)
                        }
                    }
                    .clip(RoundedCornerShape(paddingTiny()))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingSmall()),
                contentAlignment = Alignment.Center) {
                Row(
                    Modifier
                        .padding(vertical = 2.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (details.isEditRecurring) {

                        Text(
                            stringResource(R.string.repeating_every_month),
                            Modifier.alpha(0.5f),
                            color = colorResource(R.color.yellow),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 11.sp,
                            textAlign = TextAlign.Center
                        )


                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Repeat,
                            contentDescription = null,
                            tint = if (details.isRecurring) colorResource(R.color.yellow) else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable(interactionSource = null, indication = null) {
                        showDateDialog()
                    }
                    .clip(RoundedCornerShape(paddingTiny()))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingSmall()),
                contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .padding(vertical = 2.dp)
                        .fillMaxWidth(),
                ) {

                    val date = dateToString(details.date)

                    Text(
                        text = date,
                        color = colorResource(R.color.yellow),
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        modifier = Modifier.align(Alignment.Center)

                    )

                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        "",
                        tint = colorResource(R.color.yellow),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = paddingTiny())
                    )
                }

            }
        }

        //First row 7, 8, 9, C
        val firstRow = listOf("7", "8", "9", "C") // and C
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            firstRow.forEach {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable(interactionSource = null, indication = null) {
                            viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice)
                        }
                        .clip(RoundedCornerShape(paddingTiny()))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = paddingSmall()),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }


        }

        val secondRow = listOf("4", "5", "6") // and Minus and Multiplication
        Row(horizontalArrangement = Arrangement.spacedBy(paddingTiny())) {
            secondRow.forEach {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable(interactionSource = null, indication = null) {
                            viewModel.onClick(it, {}, onUpdateValue = onUpdatePrice)
                        }
                        .clip(RoundedCornerShape(paddingTiny()))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = paddingSmall()),
                    contentAlignment = Alignment.Center) {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(
                modifier = Modifier.weight(0.5f),
                horizontalArrangement = Arrangement.spacedBy(paddingTiny())
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable(interactionSource = null, indication = null) {
                            viewModel.onClick(
                                "-",
                                {},
                                onUpdateValue = onUpdatePrice
                            )
                        }
                        .clip(RoundedCornerShape(paddingTiny()))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = paddingSmall()),
                    contentAlignment = Alignment.Center)
                {
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
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable(interactionSource = null, indication = null) {
                                viewModel.onClick(
                                    "x",
                                    {},
                                    onUpdateValue = onUpdatePrice
                                )
                            }
                            .clip(RoundedCornerShape(paddingTiny()))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = paddingSmall()),
                        contentAlignment = Alignment.Center)
                    {
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
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable(interactionSource = null, indication = null) {
                            viewModel.onClick(
                                it,
                                {},
                                onUpdateValue = onUpdatePrice
                            )
                        }
                        .clip(RoundedCornerShape(paddingTiny()))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = paddingSmall()),
                    contentAlignment = Alignment.Center)
                {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Row(
                modifier = Modifier.weight(0.5f),
                horizontalArrangement = Arrangement.spacedBy(paddingTiny())
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable(interactionSource = null, indication = null) {
                            viewModel.onClick(
                                "+",
                                {},
                                onUpdateValue = onUpdatePrice
                            )
                        }
                        .clip(RoundedCornerShape(paddingTiny()))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = paddingSmall()),
                    contentAlignment = Alignment.Center)
                {
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

                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .clickable(interactionSource = null, indication = null) {
                                viewModel.onClick(
                                    "÷",
                                    {},
                                    onUpdateValue = onUpdatePrice
                                )
                            }
                            .clip(RoundedCornerShape(paddingTiny()))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = paddingSmall()),
                        contentAlignment = Alignment.Center)

                    {
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
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable(interactionSource = null, indication = null) {
                            viewModel.onClick(
                                it,
                                {},
                                onUpdateValue = onUpdatePrice
                            )
                        }
                        .clip(RoundedCornerShape(paddingTiny()))
                        .background(MaterialTheme.colorScheme.background)
                        .padding(vertical = paddingSmall()),
                    contentAlignment = Alignment.Center)
                {
                    Text(
                        text = it,
                        fontWeight = FontWeight.Normal,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            viewModel.onClick("⌫", {}, onUpdateValue = onUpdatePrice)
                        },
                        onLongClick = {
                            viewModel.clearInput()
                        }
                    )

                    .clip(RoundedCornerShape(paddingTiny()))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(vertical = paddingSmall()),
                contentAlignment = Alignment.Center)

            {
                Text(
                    text = "⌫",
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            val enabled = buttonState != CalculatorViewModal.ButtonState.DISABLED

            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .clickable(enabled = enabled, interactionSource = null, indication = null) {
                        if (buttonState == CalculatorViewModal.ButtonState.DONE) {
                            viewModel.onClick(
                                "=",
                                onSave = { onEntryValue(details.copy(price = it)) })
                        } else {
                            viewModel.onClick("=")
                        }
                    }
                    .clip(RoundedCornerShape(paddingTiny()))
                    .background(color = if (enabled) colorResource(R.color.yellow) else MaterialTheme.colorScheme.outlineVariant)
                    .padding(vertical = paddingSmall()),
                contentAlignment = Alignment.Center)
            {
                Text(
                    text = if (buttonState == CalculatorViewModal.ButtonState.EQUAL) {
                        "="
                    } else {
                        "✓"
                    },
                    fontSize = if (buttonState == CalculatorViewModal.ButtonState.EQUAL) {
                        22.sp
                    } else {
                        20.sp
                    },
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                )
            }

        }

    }

}

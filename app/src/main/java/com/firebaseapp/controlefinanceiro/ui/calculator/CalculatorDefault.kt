package com.firebaseapp.controlefinanceiro.ui.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.firebaseapp.controlefinanceiro.defaults.paddingDefault
import java.text.DecimalFormatSymbols

@Composable
fun CalculatorDefault(
    viewM: CalculatorViewModal,
    onEntryValue: (String) -> Unit
) {
    val separator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
    val calculatorKeys = listOf(


        "AC",  "",   "",  "÷",
        "7", "8", "9", "x",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", separator, "⌫", "=",
        //"", "", "", "",
    )

    val input by viewM.input.collectAsState()

    LaunchedEffect(input) { viewM.evaluateExpression() }

    Box(modifier = Modifier.padding(paddingDefault())) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            // Input display
            Text(
                text = input,
                Modifier.fillMaxWidth().padding(end = paddingDefault()),
                overflow = TextOverflow.Ellipsis,
                maxLines = 5,
                style = TextStyle(
                    fontSize = 40.sp,
                    textAlign = TextAlign.End,
                )
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.Center,

            ) {
                items(calculatorKeys) {
                    ListOfColor(btn = it, viewM, onEntryValue = onEntryValue)
                }
            }
        }
    }
}

@Composable
fun ListOfColor(btn: String, viewModel: CalculatorViewModal, onEntryValue: (String) -> Unit) {
    Box(modifier = Modifier.padding(1.dp)) {
        if (btn == "=") {
            val buttonState = viewModel.buttonState.collectAsState().value
            Button(
                onClick = {
                    if (buttonState == CalculatorViewModal.ButtonState.DONE) {
                        viewModel.onClick(btn, onEntryValue)
                    } else {
                        viewModel.onClick(btn, {})
                    }
                },

                //modifier = Modifier.size(80.dp),
                enabled = buttonState != CalculatorViewModal.ButtonState.DISABLED,
                colors = ButtonColors(
                    containerColor = btnColor(btn),
                    contentColor = Color.White,
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                ),

                //shape = CircleShape,

            ) {

                if (viewModel.buttonState.collectAsState().value == CalculatorViewModal.ButtonState.EQUAL) {
                    Text(text = btn, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                } else {
                    Text(text = "✓", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        } else if (btn.isEmpty()) {
            //Spacer(Modifier.size(80.dp))
        } else {
            Button(
                onClick = { viewModel.onClick(btn, {}) },
                //modifier = Modifier.size(80.dp),
                colors = ButtonColors(
                    containerColor = btnColor(btn),
                    contentColor = Color.White,
                    disabledContentColor = Color.White,
                    disabledContainerColor = Color.Gray,
                ),
            ) {
                Text(text = btn, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun btnColor(btn: String): Color {
    return when (btn) {
        "s", "^" -> Color(0xFFE91E63)
        "AC", "C", "=" -> Color(0xFFFFA726)
        "+", "-", "x", "÷", "(", ")" -> Color(0xFF42A5F5)
        else -> Color(0xFF424242)
    }
}
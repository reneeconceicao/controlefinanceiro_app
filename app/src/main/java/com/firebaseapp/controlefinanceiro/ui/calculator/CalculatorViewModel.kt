package com.firebaseapp.controlefinanceiro.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.mariuszgromada.math.mxparser.Expression
import java.text.DecimalFormatSymbols
import java.util.Locale


class CalculatorViewModal(initialValue: String = "0") : ViewModel() {
    private val _input = MutableStateFlow(initialValue)
    val input: StateFlow<String> = _input

    private val _output = MutableStateFlow("")
    val output: StateFlow<String> = _output

    val separator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()

    var nextResult: Double? = null

    init {
        val result = initialValue.toDoubleOrNull()
        result?.let {
            _input.value = formatResult(it)
        }
    }

    val buttonState: StateFlow<ButtonState> = combine(_input) { out ->
        val currentInput = out[0]

        val value = currentInput.replace(separator, ".").replace("x", "*")

        val expression = Expression(value)

        if (nextResult != null && nextResult!! <= 0.0) {
            nextResult = null
            return@combine ButtonState.DISABLED
        }

        if (!expression.checkSyntax() || value == "0") {
            return@combine ButtonState.DISABLED
        }

        if (currentInput.any { it in "+-x÷" }) {
            return@combine ButtonState.EQUAL
        }

        return@combine ButtonState.DONE
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        initialValue = ButtonState.DISABLED
    )

    // Function to handle button click, including live evaluation
    fun onClick(btn: String, onEntryValue: (String) -> Unit = {}, onUpdateValue: (String) -> Unit = {}) {

        when (btn) {

            "⌫" -> {
                if (_input.value.isNotEmpty()) {
                    _input.value = _input.value.dropLast(1)
                    if (_input.value.isEmpty()) {
                        _input.value = "0"
                    }
                } else {
                    _output.value = "0"
                }

            }

            "=" -> {

                val value = _input.value.replace(separator, ".").replace("x", "*")
                val expression = Expression(value)
                nextResult = expression.calculate()

                _input.value = _output.value

                if (buttonState.value == ButtonState.DONE) {

                    onEntryValue(_input.value.replace(separator, "."))
                }
            }

            "+", "-", "x", "÷" -> {
                // If input is empty, or the last character is already an operator, do nothing
                if (_input.value.isNotEmpty() && !isOperator(_input.value.last())) {
                    _input.value += btn
                }
            }

            else -> {

                if (_input.value == "0" && btn == separator) {
                    _input.value = "0$separator"
                    return
                }

                if (_input.value == "0") {
                    _input.value = btn
                    return
                }


                // If a number or decimal is pressed, simply append to the input
                if (!_input.value.matches(Regex(".*[.,]\\d{2,}$"))) { //avoid more two decimals with regex
                    _input.value += btn
                }
            }
        }

        // Always evaluate the expression after updating input
        evaluateExpression()
        onUpdateValue(_output.value)
    }

    // Function to evaluate the expression when input changes
    fun evaluateExpression() {
        try {
            if (_input.value.isNotEmpty()) {


                val result = formatResult(
                    Expression(
                        _input.value
                            .replace(",", ".")
                            .replace("x", "*")
                    ).calculate()
                )

                val res = result.toDoubleOrNull()?.isNaN()

                if (res == true) {
                    _output.value = "0"
                    return
                }

                val separator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()
                _output.value = result.replace(".", separator)
            }
        } catch (e: Exception) {
            _output.value = "0"
        }
    }

    private fun formatResult(value: Double): String {
        val hasDecimals = value % 1.0 == 0.0
        if (hasDecimals) {
            return value.toInt().toString()
        }
        return String.format(
            Locale.getDefault(),
            "%.2f",
            value
        ).replace(".", separator)
    }

    private fun evaluateExpressionDouble(input: String): Double? {
        try {
            if (input.isNotEmpty()) {

                val result =
                    Expression(
                        input
                            .replace(",", ".")
                            .replace("x", "*")
                    ).calculate()
                return result
            }
            return null
        } catch (e: Exception) {
            return null
        }
    }

    // Helper function to check if the last character is an operator
    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == 'x' || c == '÷' || c == 's' || c == '^'
    }

    enum class ButtonState {
        DISABLED,
        EQUAL,
        DONE
    }
}
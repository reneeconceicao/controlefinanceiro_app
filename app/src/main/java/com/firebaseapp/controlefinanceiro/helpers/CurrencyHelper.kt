package com.firebaseapp.controlefinanceiro.helpers

import java.math.BigDecimal
import java.text.NumberFormat

fun formatedPrice(value: BigDecimal): String {
    return NumberFormat.getCurrencyInstance().format(value)
}

fun formatedPriceIndicator(value: BigDecimal): String {
    return if (value > BigDecimal.ZERO) {
        "+ ${NumberFormat.getCurrencyInstance().format(value)}"
    } else {
        "- ${NumberFormat.getCurrencyInstance().format(value)}"
    }
}

fun currencyFormat(value: String): String {
    val formattedString = value.replace(Regex("\\D"), "")
    if (formattedString.isEmpty()) {
        return NumberFormat.getCurrencyInstance().format(0)
    } else {
        val formatter = NumberFormat.getCurrencyInstance()
        formatter.currency?.defaultFractionDigits?.let {
            return if (it > 0) {
                NumberFormat.getCurrencyInstance().format(formattedString.toLong() / 100.0)
            } else {
                NumberFormat.getCurrencyInstance().format(formattedString.toLong())
            }
        }
    }
    return NumberFormat.getCurrencyInstance().format(0)
}

fun currencyFormat(bigDecimal: BigDecimal): String {

    val formatter = NumberFormat.getCurrencyInstance()
    formatter.currency?.defaultFractionDigits?.let {
        return if (it > 0) {
            NumberFormat.getCurrencyInstance().format(bigDecimal)
        } else {
            NumberFormat.getCurrencyInstance().format(bigDecimal)
        }
    }
    return NumberFormat.getCurrencyInstance().format(bigDecimal)
}
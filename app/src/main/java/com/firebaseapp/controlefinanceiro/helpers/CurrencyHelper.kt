package com.firebaseapp.controlefinanceiro.helpers

import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import java.math.BigDecimal
import java.text.NumberFormat

fun formatedPrice(value: BigDecimal): String {
    return NumberFormat.getCurrencyInstance().format(value)
}

fun formatedPriceIndicator(word: Word): String {
    return if (word.type == WordType.Income) {
        "+ ${currencyFormat(word.value.toString())}"
    } else {
        "- ${currencyFormat(word.value.toString())}"
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


fun toBigDecimal(value: String): BigDecimal {
    val formattedString = value.replace(Regex("\\D"), "")

    if (formattedString.isEmpty()) {
        return BigDecimal.ZERO
    }

    val formatter = NumberFormat.getCurrencyInstance()
    val fractionDigits = formatter.currency?.defaultFractionDigits ?: 0

    return if (fractionDigits > 0) {
        BigDecimal(formattedString)
            .movePointLeft(fractionDigits)
    } else {
        BigDecimal(formattedString)
    }
}


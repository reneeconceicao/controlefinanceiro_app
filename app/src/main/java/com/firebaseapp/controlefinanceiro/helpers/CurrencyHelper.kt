package com.firebaseapp.controlefinanceiro.helpers

import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.WordType
import java.math.BigDecimal
import java.text.DecimalFormatSymbols
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
    val symbols = DecimalFormatSymbols.getInstance()
    val separator = symbols.decimalSeparator

    val normalized = value.replace(",", ".")
        .replace(separator, '.')

    val number = normalized.toBigDecimalOrNull() ?: BigDecimal.ZERO

    return NumberFormat.getCurrencyInstance().format(number)
}

fun toBigDecimal(value: String): BigDecimal {

    val separator = DecimalFormatSymbols.getInstance().decimalSeparator.toString()

    val formattedString = value.replace(separator, ".")

    if (formattedString.isEmpty()) {
        return BigDecimal.ZERO
    }

    return BigDecimal(formattedString)

//    val formatter = NumberFormat.getCurrencyInstance()
//    val fractionDigits = formatter.currency?.defaultFractionDigits ?: 0
//
//    return if (fractionDigits > 0) {
//        BigDecimal(formattedString)
//            .movePointLeft(fractionDigits)
//    } else {
//        BigDecimal(formattedString)
//    }
}


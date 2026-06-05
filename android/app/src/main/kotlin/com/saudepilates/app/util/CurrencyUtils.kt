package com.saudepilates.app.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun symbol(language: String?): String = when (language) {
        "en" -> "$"
        "es", "fr" -> "€"
        else -> "R$"
    }

    fun locale(language: String?): Locale = when (language) {
        "en" -> Locale.US
        "es" -> Locale("es", "ES")
        "fr" -> Locale.FRANCE
        else -> Locale("pt", "BR")
    }

    fun format(value: Double, language: String?): String {
        val formatter = NumberFormat.getNumberInstance(locale(language))
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter.format(value)
    }

    fun formatWithSymbol(value: Double, language: String?): String {
        return "${symbol(language)} ${format(value, language)}"
    }
}

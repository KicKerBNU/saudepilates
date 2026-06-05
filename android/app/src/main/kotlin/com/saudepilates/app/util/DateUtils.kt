package com.saudepilates.app.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    val monthNames = listOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    fun toDate(value: Any?): Date? = when (value) {
        is Timestamp -> value.toDate()
        is Date -> value
        is String -> try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(value.take(19))
        } catch (_: Exception) { null }
        else -> null
    }

    fun isSameDay(a: Date, b: Date): Boolean {
        val calA = Calendar.getInstance().apply { time = a }
        val calB = Calendar.getInstance().apply { time = b }
        return calA.get(Calendar.YEAR) == calB.get(Calendar.YEAR) &&
            calA.get(Calendar.DAY_OF_YEAR) == calB.get(Calendar.DAY_OF_YEAR)
    }

    fun shortDate(date: Date, locale: Locale = Locale("pt", "BR")): String {
        return SimpleDateFormat("dd/MM/yyyy", locale).format(date)
    }

    fun startOfDay(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}

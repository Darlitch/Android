package com.example.lab2.ui.converter

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatCbrDate(date: String): String {
    return try {
        val parsed = OffsetDateTime.parse(date)
        val formatter = DateTimeFormatter.ofPattern(
            "d MMMM yyyy",
            Locale("ru")
        )
        parsed.format(formatter)
    } catch (e: Exception) {
        date
    }
}
package com.example.lab2.domain.utility

import com.example.lab2.core.AppLogger
import com.example.lab2.domain.model.Currency

object CurrencyCalculator {
    fun chooseTarget(currencies: List<Currency>, baseCode: String?, currentTarget: String?): String? {
        if (currencies.isEmpty()) return null
        val codes = currencies.asSequence().map {it.code }.toSet()
        if (currentTarget != null && currentTarget in codes && currentTarget != baseCode) {
            return currentTarget
        }
        val cur = currencies.firstOrNull{ it.code == "USD" }?.code
        if (cur != null && cur != baseCode) return cur
        return currencies.firstOrNull { it.code != baseCode }?.code
    }

    fun parseAmount(input: String): Double? {
        return input.replace(',', '.').toDoubleOrNull()
    }

    fun convert(
        amount: Double,
        base: Currency,
        target: Currency
    ): Double {
        val baseRub = base.value / base.nominal
        val targetRub = target.value / target.nominal
        return amount * baseRub / targetRub
    }
}
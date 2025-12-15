package com.example.lab2

import com.example.lab2.domain.model.Currency
import com.example.lab2.domain.utility.CurrencyCalculator
import org.junit.Assert.*
import org.junit.Test

class CurrencyCalculatorTest {
    private val rub = Currency("RUB", 1, "Рубль", 1.0)
    private val usd = Currency("USD", 1, "Доллар", 90.0)
    private val eur = Currency("EUR", 1, "Евро", 100.0)
    private val currencies = listOf(rub, usd, eur)

    @Test
    fun chooseTarget_prefersUsd() {
        val target = CurrencyCalculator.chooseTarget(
            currencies = currencies,
            baseCode = "RUB",
            currentTarget = null
        )
        assertEquals("USD", target)
    }

    @Test
    fun chooseTarget_notEqualBase() {
        val target = CurrencyCalculator.chooseTarget(
            currencies = currencies,
            baseCode = "USD",
            currentTarget = "USD"
        )
        assertNotEquals("USD", target)
    }

    @Test
    fun parseAmount_withComma() {
        val value = CurrencyCalculator.parseAmount("12,5")
        assertEquals(12.5, value!!, 0.0001)
    }

    @Test
    fun convert_correctCalculation() {
        val result = CurrencyCalculator.convert(
            amount = 2.0,
            base = usd,
            target = eur
        )
        assertEquals(1.8, result, 0.0001)
    }
}
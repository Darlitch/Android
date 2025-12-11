package com.example.lab2.domain.model

data class CurrencyRates(
    val date: String,
    var currencies: List<Currency>
)

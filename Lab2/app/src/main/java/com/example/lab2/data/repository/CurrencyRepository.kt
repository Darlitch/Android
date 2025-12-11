package com.example.lab2.data.repository

import com.example.lab2.data.remote.CbrApi
import com.example.lab2.domain.model.Currency
import com.example.lab2.domain.model.CurrencyRates

class CurrencyRepository(private val api: CbrApi) {
    suspend fun getCurrencies(): CurrencyRates {
        val response = api.getDailyRates()
        val currencies = response.valute.values.map { dto ->
            Currency(
                code = dto.code,
                name = dto.name,
                nominal = dto.nominal,
                value = dto.value
            )
        }.sortedBy { it.code }
        return CurrencyRates(
            date = response.date,
            currencies = currencies
        )
    }
}
package com.example.lab2.data.repository

import com.example.lab2.data.local.dao.CurrencyDao
import com.example.lab2.data.local.entity.CurrencyEntity
import com.example.lab2.data.local.entity.MetaEntity
import com.example.lab2.data.remote.CbrApi
import com.example.lab2.domain.model.Currency
import com.example.lab2.domain.model.CurrencyRates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CurrencyRepository(private val api: CbrApi, private val dao: CurrencyDao) {
    fun observeCurrencies(): Flow<List<Currency>> = dao.observeCurrencies().map { list ->
        val currencies = list.map { Currency(it.code, it.nominal, it.name, it.value) }
        withRub(currencies).sortedBy { it.code }
    }

    suspend fun refresh(): Result<String> {
        return runCatching {
            val response = api.getDailyRates()
            val entities = response.valute.values.map { dto ->
                CurrencyEntity(dto.code, dto.nominal, dto.name, dto.value)
            }
            dao.upsertAll(entities)
            dao.upsertMeta(MetaEntity("last_date", response.date))
            response.date
        }
    }

    suspend fun getLastDate(): String? = dao.getMeta("last_date")?.value

    private fun withRub(currencies: List<Currency>) : List<Currency> {
        val rub = Currency("RUB", 1, "Российский рубль", 1.0)
        return if (currencies.any { it.code == "RUB" }) currencies else listOf(rub) + currencies
    }
}
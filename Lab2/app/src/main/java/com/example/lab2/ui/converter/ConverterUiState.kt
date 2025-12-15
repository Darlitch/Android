package com.example.lab2.ui.converter

import com.example.lab2.domain.model.Currency

data class ConverterUiState(
    val isLoading: Boolean = false,
    val currencies: List<Currency> = emptyList(),
    val favoriteCodes: Set<String> = emptySet(),
    val lastUpdateDate: String? = null,

    val isStale: Boolean = false,
    val snackbarMessage: String? = null,

    val baseCurrencyCode: String? = null,
    val targetCurrencyCode: String? = null,

    val amountInput: String = "",
    val result: String = "",
    val errorMessage: String? = null
)

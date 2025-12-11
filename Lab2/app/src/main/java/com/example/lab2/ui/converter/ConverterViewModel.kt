package com.example.lab2.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab2.data.NetworkModule
import com.example.lab2.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class ConverterViewModel : ViewModel() {
    private val repository = CurrencyRepository(NetworkModule.api)
    private val _uiState = MutableStateFlow(ConverterUiState(isLoading = true))
    val uiState: StateFlow<ConverterUiState> = _uiState

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            try {
                val currencyRates = repository.getCurrencies()
                val currencies = currencyRates.currencies
                val base = currencies.getOrNull(0)?.code
                val target = currencies.getOrNull(1)?.code

                _uiState.update { curr ->
                    curr.copy(
                        isLoading = false,
                        currencies = currencies,
                        lastUpdateDate = currencyRates.date,
                        baseCurrencyCode = base,
                        targetCurrencyCode = target,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { curr ->
                    curr.copy(
                        isLoading = false,
                        errorMessage = "Не удалось загрузить курсы: ${e.message}"
                    )
                }
            }
        }
    }

    private fun recalculateResult() {
        val state = _uiState.value
        val amount = state.amountInput.replace(',', '.').toDoubleOrNull()
        val currencies = state.currencies
        val base = currencies.firstOrNull { it.code == state.baseCurrencyCode }
        val target = currencies.firstOrNull { it.code == state.targetCurrencyCode }

        if (amount == null || base == null || target == null) {
            _uiState.update { it.copy(result = "") }
            return
        }
        val priceOfBaseInRub = base.value / base.nominal
        val priceOfTargetInRub = target.value / target.nominal
        val result = amount * priceOfBaseInRub / priceOfTargetInRub
        val formatted = String.format(
            Locale("ru"),
            "%.2f %s",
            result,
            target.code
        )
        _uiState.update { it.copy(result = formatted) }
    }

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amountInput = value) }
        recalculateResult()
    }

    fun onBaseCurrencySelected(code: String) {
        _uiState.update { it.copy(baseCurrencyCode = code) }
        recalculateResult()
    }

    fun onTargetCurrencySelected(code: String) {
        _uiState.update { it.copy(targetCurrencyCode = code) }
        recalculateResult()
    }

    fun onSwapCurrencies() {
        _uiState.update { state ->
            state.copy(
                baseCurrencyCode = state.targetCurrencyCode,
                targetCurrencyCode = state.baseCurrencyCode
            )
        }
        recalculateResult()
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onUpdateCurrencies() {
        _uiState.update { it.copy(isLoading = true) }
        loadCurrencies()
    }
}
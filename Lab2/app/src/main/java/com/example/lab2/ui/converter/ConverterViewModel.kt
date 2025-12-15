package com.example.lab2.ui.converter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab2.core.AppLogger
import com.example.lab2.data.NetworkModule
import com.example.lab2.data.local.DatabaseProvider
import com.example.lab2.data.local.FavoritesDataStore
import com.example.lab2.data.repository.CurrencyRepository
import com.example.lab2.domain.utility.CurrencyCalculator
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class ConverterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CurrencyRepository(NetworkModule.api, DatabaseProvider.get(application).currencyDao())
    private val _uiState = MutableStateFlow(ConverterUiState(isLoading = true))
    private val ceh = CoroutineExceptionHandler { _, throwable ->
        AppLogger.e("ConverterViewModel: unhandled coroutine exception", throwable)
        _uiState.update {
            it.copy(
                snackbarMessage = "Неожиданная ошибка",
                isLoading = false
            )
        }
    }
    val uiState: StateFlow<ConverterUiState> = _uiState

    init {
        AppLogger.i("ConverterViewModel: init")
        observeFavorites()
        bootstrap()
    }

    private fun bootstrap() {
        viewModelScope.launch(ceh) {
            AppLogger.d("bootstrap: start")
            launch(ceh) { observeCurrenciesFromCache() }
            val firstCurrencies = repository.observeCurrencies().first()
            val hasRealCache = firstCurrencies.any { it.code != "RUB" }
            _uiState.update { it.copy(isLoading = !hasRealCache) }
            refreshCurrencies()
        }
    }

    private suspend fun observeCurrenciesFromCache() {
        AppLogger.d("observeCurrenciesFromCache: start collecting")
        repository.observeCurrencies().collect { currencies ->
            AppLogger.d("observeCurrenciesFromCache: got ${currencies.size} currencies")
            _uiState.update { s ->
                s.copy(
                    currencies = currencies,
                    baseCurrencyCode = s.baseCurrencyCode ?: "RUB",
                    targetCurrencyCode = CurrencyCalculator.chooseTarget(currencies, s.baseCurrencyCode, s.targetCurrencyCode)
                )
            }
            recalculateResult()
        }
    }

    private fun refreshCurrencies() {
        viewModelScope.launch(ceh) {
            AppLogger.d("refreshCurrencies: start")
            val result = repository.refresh()
            val lastDate = repository.getLastDate()
            result.fold(
                onSuccess = { date ->
                    AppLogger.i("refreshCurrencies: success date=$date")
                    _uiState.update {
                        it.copy(
                            isStale = false,
                            lastUpdateDate = date,
                            isLoading = false,
                            targetCurrencyCode = CurrencyCalculator.chooseTarget(it.currencies, it.baseCurrencyCode, it.targetCurrencyCode),
                            errorMessage = null,
                            snackbarMessage = null
                        )
                    }
                },
                onFailure = { e ->
                    val hasCache = _uiState.value.currencies.any { it.code != "RUB"}
                    AppLogger.e("refreshCurrencies: failed, hasCache=$hasCache", e)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isStale = hasCache,
                            lastUpdateDate = lastDate,
                            errorMessage = if (!hasCache) "Не удалось загрузить курсы: ${e.message}" else null,
                            snackbarMessage = if (hasCache) "Нет актуальных данных. Показаны сохранённые курсы." else null
                        )
                    }
                }
            )
        }
    }

    private fun observeFavorites() {
        val appContext = getApplication<Application>().applicationContext
        viewModelScope.launch(ceh) {
            AppLogger.d("observeFavorites: start collecting")
            runCatching {
                FavoritesDataStore.favoritesFlow(appContext).collect { favorites ->
                    AppLogger.d("observeFavorites: favorites size=${favorites.size}")
                    _uiState.update { it.copy(favoriteCodes = favorites) }
                }
            }.onFailure { e ->
                AppLogger.e("observeFavorites: failed", e)
                _uiState.update { it.copy(snackbarMessage = "Ошибка чтения избранного") }
            }
        }
    }

    private fun recalculateResult() {
        val state = _uiState.value
        val amount = CurrencyCalculator.parseAmount(state.amountInput)
        val currencies = state.currencies
        val base = currencies.firstOrNull { it.code == state.baseCurrencyCode }
        val target = currencies.firstOrNull { it.code == state.targetCurrencyCode }

        if (amount == null || base == null || target == null) {
            _uiState.update { it.copy(result = "") }
            return
        }
        val result = CurrencyCalculator.convert(amount, base, target)
        val formatted = String.format(
            Locale("ru"),
            "%.2f %s",
            result,
            target.code
        )
        _uiState.update { it.copy(result = formatted) }
    }

    fun onAmountChange(value: String) {
        AppLogger.d("amountChanged: '$value'")
        _uiState.update { it.copy(amountInput = value) }
        recalculateResult()
    }

    fun onBaseCurrencySelected(code: String) {
        AppLogger.d("baseSelected: $code")
        _uiState.update { it.copy(baseCurrencyCode = code) }
        recalculateResult()
    }

    fun onTargetCurrencySelected(code: String) {
        AppLogger.d("targetSelected: $code")
        _uiState.update { it.copy(targetCurrencyCode = code) }
        recalculateResult()
    }

    fun onSwapCurrencies() {
        AppLogger.d("swap: ${_uiState.value.baseCurrencyCode} <-> ${_uiState.value.targetCurrencyCode}")
        _uiState.update { state ->
            state.copy(
                baseCurrencyCode = state.targetCurrencyCode,
                targetCurrencyCode = state.baseCurrencyCode
            )
        }
        recalculateResult()
    }

    fun onSnackbarShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun onUpdateCurrencies() {
        AppLogger.i("manualRefresh: user clicked refresh")
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                snackbarMessage = null
            )
        }
        refreshCurrencies()
    }

    fun onToggleFavorite(code: String) {
        AppLogger.d("toggleFavorite: $code")
        val appContext = getApplication<Application>().applicationContext
        viewModelScope.launch(ceh) {
            runCatching {
                FavoritesDataStore.toggleFavorite(appContext, code)
            }.onFailure { e ->
                AppLogger.e("toggleFavorite: failed code=$code", e)
                _uiState.update { it.copy(snackbarMessage = "Не удалось изменить избранное") }
            }
        }
    }
}
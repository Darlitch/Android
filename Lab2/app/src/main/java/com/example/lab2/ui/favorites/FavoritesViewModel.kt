package com.example.lab2.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab2.core.AppLogger
import com.example.lab2.data.NetworkModule
import com.example.lab2.data.local.DatabaseProvider
import com.example.lab2.data.local.FavoritesDataStore
import com.example.lab2.data.repository.CurrencyRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CurrencyRepository(NetworkModule.api, DatabaseProvider.get(application).currencyDao())
    private val appContext = application.applicationContext
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage
    private val ceh = CoroutineExceptionHandler { _, throwable ->
        AppLogger.e("FavoritesViewModel: unhandled coroutine exception", throwable)
        _snackbarMessage.value = "Неожиданная ошибка"
    }

    val uiState: StateFlow<FavoritesUiState> = combine(
        repository.observeCurrencies(),
        FavoritesDataStore.favoritesFlow(appContext)
    ) { currencies, favoriteCodes ->
        FavoritesUiState(
            favoriteCodes = favoriteCodes,
            favorites = currencies.filter { it.code in favoriteCodes }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        FavoritesUiState()
    )

    fun onToggleFavorite(code: String) {
        AppLogger.d("Favorites: toggleFavorite $code")
        viewModelScope.launch(ceh) {
            runCatching {
                FavoritesDataStore.toggleFavorite(appContext, code)
            }.onFailure { e ->
                AppLogger.e("Favorites: toggleFavorite failed for $code", e)
                _snackbarMessage.value = "Не удалось изменить избранное"
            }
        }
    }

    fun onSnackbarShown() {
        _snackbarMessage.value = null
    }
}
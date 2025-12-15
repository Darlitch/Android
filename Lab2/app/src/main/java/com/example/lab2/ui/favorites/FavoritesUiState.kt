package com.example.lab2.ui.favorites

import com.example.lab2.domain.model.Currency

data class FavoritesUiState(
    val favorites: List<Currency> = emptyList(),
    val favoriteCodes: Set<String> = emptySet()
)

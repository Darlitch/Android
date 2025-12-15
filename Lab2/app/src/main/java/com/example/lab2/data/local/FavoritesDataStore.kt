package com.example.lab2.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.favoritesDataStore by preferencesDataStore(name = "favorites_prefs")

private object FavoritesPrefsKeys {
    val FAVORITE_CODES: Preferences.Key<Set<String>> =
        stringSetPreferencesKey("favorite_currency_codes")
}

object FavoritesDataStore {
    fun favoritesFlow(context: Context): Flow<Set<String>> {
        return context.favoritesDataStore.data.map { prefs ->
            prefs[FavoritesPrefsKeys.FAVORITE_CODES] ?: emptySet()
        }
    }

    suspend fun toggleFavorite(context: Context, code: String) {
        context.favoritesDataStore.edit { prefs ->
            val current = prefs[FavoritesPrefsKeys.FAVORITE_CODES] ?: emptySet()
            val mutable = current.toMutableSet()
            if (mutable.contains(code)) {
                mutable.remove(code)
            } else {
                mutable.add(code)
            }
            prefs[FavoritesPrefsKeys.FAVORITE_CODES] = mutable
        }
    }
}
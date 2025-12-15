package com.example.lab2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab2.ui.converter.ConverterScreen
import com.example.lab2.ui.favorites.FavoriteScreen

@Composable
fun AppNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.CONVERTER
    ) {
        composable(Routes.CONVERTER) {
            ConverterScreen(
                onOpenFavorites = { navController.navigate(Routes.FAVORITES) { launchSingleTop = true } }
            )
        }
        composable(Routes.FAVORITES) {
            FavoriteScreen(
                onBack = { navController.popBackStack()}
            )
        }
    }
}
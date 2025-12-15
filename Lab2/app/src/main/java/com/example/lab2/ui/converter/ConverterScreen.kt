package com.example.lab2.ui.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(onOpenFavorites: () -> Unit, viewModel: ConverterViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.snackbarMessage) {
        val msg = state.snackbarMessage ?: return@LaunchedEffect
        val res = snackbarHostState.showSnackbar(
            message = msg,
            actionLabel = "Ок",
            withDismissAction = true
        )
        viewModel.onSnackbarShown()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(title = {
                Text(
                "Конвертер валют",
                style = MaterialTheme.typography.titleLarge
                )},
                navigationIcon = {
                    IconButton(onClick = onOpenFavorites) {
                        Icon(Icons.Default.Star, contentDescription = "Избранные")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onUpdateCurrencies() },
                        enabled = !state.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Обновить курсы"
                        )
                    }
                })
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.errorMessage!!,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.onUpdateCurrencies() }) {
                        Text("Повторить попытку")
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.lastUpdateDate?.let { date ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Курсы на: ${formatCbrDate(date)}",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            if (state.isStale) {
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "Данные устаревшие"
                                )
                            }
                        }
                    }

                    Text(
                        text = "Всего валют: ${state.currencies.size}",
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Из валюты:",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    CurrencyDropdown(
                        items = state.currencies,
                        selectedCode = state.baseCurrencyCode,
                        favoriteCodes = state.favoriteCodes,
                        onSelect = { currency -> viewModel.onBaseCurrencySelected(currency.code) },
                        onToggleFavorite = { code -> viewModel.onToggleFavorite(code) }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.onSwapCurrencies()},
                            modifier = Modifier.padding(vertical = 0.dp)) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = "Поменять валюты местами"
                            )
                        }
                    }

                    Text(
                        text = "В валюту:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    CurrencyDropdown(
                        items = state.currencies,
                        selectedCode = state.targetCurrencyCode,
                        favoriteCodes = state.favoriteCodes,
                        onSelect = { currency -> viewModel.onTargetCurrencySelected(currency.code) },
                        onToggleFavorite = { code -> viewModel.onToggleFavorite(code) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Сумма:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    TextField(
                        value = state.amountInput,
                        onValueChange = { viewModel.onAmountChange(it) },
                        modifier = Modifier.fillMaxWidth().testTag("amount_input"),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        placeholder = { Text("Введите сумму") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    if (state.result.isNotEmpty()) {
                        Text(
                            text = "Результат:",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = state.result,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.testTag("result_text")
                        )
                    }
                }
            }
        }

    }
}
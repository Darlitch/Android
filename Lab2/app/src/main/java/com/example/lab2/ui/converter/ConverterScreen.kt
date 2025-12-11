package com.example.lab2.ui.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(viewModel: ConverterViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                "Конвертер валют",
                style = MaterialTheme.typography.titleLarge
                )},
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
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    state.lastUpdateDate?.let { date ->
                        Text(
                            text = "Курсы на: ${formatCbrDate(date)}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
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
                        onSelect = { currency -> viewModel.onBaseCurrencySelected(currency.code) }
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
                        onSelect = { currency -> viewModel.onTargetCurrencySelected(currency.code) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Сумма:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    TextField(
                        value = state.amountInput,
                        onValueChange = { viewModel.onAmountChange(it) },
                        modifier = Modifier.fillMaxWidth(),
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
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }

    }
}
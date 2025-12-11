package com.example.lab2.ui.converter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import com.example.lab2.domain.model.Currency

@Composable
fun CurrencyDropdown(
    items: List<Currency>,
    selectedCode: String?,
    onSelect: (Currency) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedCurrency = items.firstOrNull { it.code == selectedCode }
    Box {
        OutlinedButton(
            onClick = { expanded.value = true },
            shape = RectangleShape,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedCurrency?.let {"${it.code} — ${it.name}"}  ?: "Выберите валюту",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Открыть список"
            )
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            items.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(
                        "${currency.code} — ${currency.name}",
                        style = MaterialTheme.typography.bodyMedium
                    ) },
                    onClick = {
                        onSelect(currency)
                        expanded.value = false
                    }
                )
            }
        }
    }
}
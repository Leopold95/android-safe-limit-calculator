package com.alexandr.safelimitcalculator.ui.screen.expenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.data.model.Expense
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(viewModel: ExpensesViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }

    val zoneId = remember { ZoneId.systemDefault() }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(zoneId)
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.common_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val categories = listOf(
        stringResource(R.string.category_food),
        stringResource(R.string.category_transport),
        stringResource(R.string.category_entertainment),
        stringResource(R.string.category_shopping),
        stringResource(R.string.category_health),
        stringResource(R.string.category_utilities),
        stringResource(R.string.category_other)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = LocalAppTheme.colors.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { newValue ->
                            if (newValue.length <= 12 && newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                                amount = newValue
                                amountError = false
                            }
                        },
                        label = { Text(stringResource(R.string.expenses_amount)) },
                        isError = amountError,
                        supportingText = if (amountError) {{ Text(stringResource(R.string.expenses_amount_error)) }} else null,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )

                    ExposedDropdownMenuBox(
                        expanded = showCategoryMenu,
                        onExpandedChange = { showCategoryMenu = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.expenses_category)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu) },
                            isError = categoryError,
                            supportingText = if (categoryError) {{ Text(stringResource(R.string.expenses_category_error)) }} else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(
                                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                    enabled = true
                                ),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        categoryError = false
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { if (it.length <= 100) description = it },
                        label = { Text(stringResource(R.string.expenses_description)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    OutlinedTextField(
                        value = selectedDate.format(DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format))),
                        onValueChange = {},
                        label = { Text(stringResource(R.string.expenses_date)) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.common_edit)
                                )
                            }
                        }
                    )

                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue != null && amountValue > uiState.dailyLimit && uiState.dailyLimit > 0) {
                        Text(
                            text = stringResource(R.string.expenses_limit_warning),
                            style = LocalAppTheme.typography.bodySmall,
                            color = LocalAppTheme.colors.error
                        )
                    }

                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull()
                            if (amt == null) {
                                amountError = true
                            }
                            if (selectedCategory.isBlank()) {
                                categoryError = true
                            }
                            if (amt != null && selectedCategory.isNotBlank()) {
                                viewModel.addExpense(
                                    amount = amt,
                                    category = selectedCategory,
                                    description = description,
                                    date = selectedDate
                                )
                                amount = ""
                                selectedCategory = ""
                                description = ""
                                selectedDate = LocalDate.now()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.expenses_add))
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.expenses_recent),
                style = LocalAppTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.onBackground
            )
        }

        if (uiState.expenses.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.expenses_no_expenses),
                    style = LocalAppTheme.typography.bodyMedium,
                    color = LocalAppTheme.colors.onSurfaceVariant
                )
            }
        } else {
            items(uiState.expenses) { expense ->
                ExpenseItem(expense)
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppTheme.colors.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.category,
                    style = LocalAppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = LocalAppTheme.colors.onSurface
                )
                if (expense.description != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = expense.description,
                        style = LocalAppTheme.typography.bodySmall,
                        color = LocalAppTheme.colors.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = expense.date.format(
                        DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format))
                    ),
                    style = LocalAppTheme.typography.bodySmall,
                    color = LocalAppTheme.colors.onSurfaceVariant
                )
            }
            Text(
                text = "$${"%.2f".format(expense.amount)}",
                style = LocalAppTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.onSurface
            )
        }
    }
}

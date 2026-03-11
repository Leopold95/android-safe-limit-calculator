package com.alexandr.safelimitcalculator.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.data.model.Payment
import com.alexandr.safelimitcalculator.data.model.UserData
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.large)
            ) {
                item {
                    BalanceCard(uiState.userData?.balance ?: 0.0)
                }

                item {
                    DailyLimitCard(
                        dailyLimit = uiState.dailyLimit,
                        todaySpent = uiState.todaySpent,
                        daysUntilIncome = uiState.daysUntilIncome
                    )
                }

                if (uiState.userData != null) {
                    item {
                        IncomeReserveCard(
                            nextIncomeDate = uiState.userData!!.nextIncomeDate,
                            reserve = uiState.userData!!.reserve
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.home_upcoming_payments),
                        style = LocalAppTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = LocalAppTheme.colors.onBackground
                    )
                }

                if (uiState.upcomingPayments.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.home_no_payments),
                            style = LocalAppTheme.typography.bodyMedium,
                            color = LocalAppTheme.colors.onSurfaceVariant
                        )
                    }
                } else {
                    items(uiState.upcomingPayments) { payment ->
                        PaymentItem(payment)
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomEnd),
                onClick = viewModel::showUpdateDialog,
                containerColor = LocalAppTheme.colors.primary
            ) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.home_update_data))
            }
        }
    }

    if (showUpdateDialog) {
        UpdateDataDialog(
            currentUserData = uiState.userData,
            onDismiss = { viewModel.hideUpdateDialog() },
            onConfirm = { balance, incomeDate, reserve ->
                viewModel.updateUserData(balance, incomeDate, reserve)
            }
        )
    }
}

@Composable
fun BalanceCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppTheme.colors.primaryContainer
        ),
        shape = LocalAppTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalAppTheme.dimen.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.home_balance),
                style = LocalAppTheme.typography.titleMedium,
                color = LocalAppTheme.colors.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(LocalAppTheme.dimen.extraSmall))
            Text(
                text = "$${"%.2f".format(balance)}",
                style = LocalAppTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.onPrimaryContainer
            )
        }
    }
}

@Composable
fun DailyLimitCard(dailyLimit: Double, todaySpent: Double, daysUntilIncome: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppTheme.colors.secondaryContainer
        ),
        shape = LocalAppTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalAppTheme.dimen.large)
        ) {
            Text(
                text = stringResource(R.string.home_daily_limit),
                style = LocalAppTheme.typography.titleMedium,
                color = LocalAppTheme.colors.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(LocalAppTheme.dimen.extraSmall))
            Text(
                text = "$${"%.2f".format(dailyLimit)}",
                style = LocalAppTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(LocalAppTheme.dimen.medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.home_today_spent),
                        style = LocalAppTheme.typography.bodySmall,
                        color = LocalAppTheme.colors.onSecondaryContainer
                    )
                    Text(
                        text = "$${"%.2f".format(todaySpent)}",
                        style = LocalAppTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (todaySpent > dailyLimit)
                            LocalAppTheme.colors.error
                        else
                            LocalAppTheme.colors.onSecondaryContainer
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.home_days_until_income),
                        style = LocalAppTheme.typography.bodySmall,
                        color = LocalAppTheme.colors.onSecondaryContainer
                    )
                    Text(
                        text = "$daysUntilIncome",
                        style = LocalAppTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = LocalAppTheme.colors.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun IncomeReserveCard(nextIncomeDate: LocalDate, reserve: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppTheme.colors.surface
        ),
        shape = LocalAppTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalAppTheme.dimen.medium),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.home_next_income),
                    style = LocalAppTheme.typography.bodyMedium,
                    color = LocalAppTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(LocalAppTheme.dimen.extraSmall))
                Text(
                    text = nextIncomeDate.format(
                        DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format)))
                    ,
                    style = LocalAppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = LocalAppTheme.colors.onSurface
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.home_reserve),
                    style = LocalAppTheme.typography.bodyMedium,
                    color = LocalAppTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(LocalAppTheme.dimen.extraSmall))
                Text(
                    text = "$${"%.2f".format(reserve)}",
                    style = LocalAppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = LocalAppTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
fun PaymentItem(payment: Payment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppTheme.colors.surfaceVariant
        ),
        shape = LocalAppTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalAppTheme.dimen.medium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payment.name,
                    style = LocalAppTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = LocalAppTheme.colors.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = payment.dueDate.format(
                        DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format_short)))
                    ,
                    style = LocalAppTheme.typography.bodySmall,
                    color = LocalAppTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "$${"%.2f".format(payment.amount)}",
                style = LocalAppTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.onSurfaceVariant
            )
        }
    }
}

@Composable
fun UpdateDataDialog(
    currentUserData: UserData?,
    onDismiss: () -> Unit,
    onConfirm: (Double, LocalDate, Double) -> Unit
) {
    var balance by remember { mutableStateOf(currentUserData?.balance?.toString() ?: "") }
    var reserve by remember { mutableStateOf(currentUserData?.reserve?.toString() ?: "0") }
    var selectedDate by remember { mutableStateOf(currentUserData?.nextIncomeDate ?: LocalDate.now().plusDays(30)) }
    var showDatePicker by remember { mutableStateOf(false) }

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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_update_balance_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppTheme.dimen.large),
                verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.medium)
            ) {
                OutlinedTextField(
                    value = balance,
                    onValueChange = { newValue ->
                        if (newValue.length <= 12 && newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                            balance = newValue
                        }
                    },
                    label = { Text(stringResource(R.string.home_balance)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = selectedDate.format(DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format))),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.home_next_income)) },
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

                OutlinedTextField(
                    value = reserve,
                    onValueChange = { newValue ->
                        if (newValue.length <= 12 && newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                            reserve = newValue
                        }
                    },
                    label = { Text(stringResource(R.string.home_reserve)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val balanceValue = balance.toDoubleOrNull() ?: 0.0
                    val reserveValue = reserve.toDoubleOrNull() ?: 0.0
                    onConfirm(balanceValue, selectedDate, reserveValue)
                }
            ) {
                Text(stringResource(R.string.common_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.common_cancel))
            }
        }
    )
}

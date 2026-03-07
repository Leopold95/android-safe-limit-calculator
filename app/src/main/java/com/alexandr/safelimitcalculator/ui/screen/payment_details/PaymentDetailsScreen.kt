package com.alexandr.safelimitcalculator.ui.screen.payment_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailsScreen(
    paymentId: Long,
    navController: NavController,
    viewModel: PaymentDetailsViewModel = koinViewModel { parametersOf(paymentId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()

    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    var isPaid by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    val zoneId = remember { ZoneId.systemDefault() }

    LaunchedEffect(uiState.payment?.id) {
        val payment = uiState.payment
        if (payment != null) {
            name = payment.name
            amount = payment.amount.toString()
            selectedDate = payment.dueDate
            isPaid = payment.isPaid
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()
    )

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LocalAppTheme.colors.primary)
            }
        }

        uiState.isMissing -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.payment_not_found),
                    style = LocalAppTheme.typography.bodyLarge,
                    color = LocalAppTheme.colors.onSurfaceVariant
                )
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(LocalAppTheme.dimen.medium),
                verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.medium)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text(stringResource(R.string.payment_name)) },
                    isError = nameError,
                    supportingText = if (nameError) { { Text(stringResource(R.string.payment_name_error)) } } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it; amountError = false },
                    label = { Text(stringResource(R.string.payment_amount)) },
                    isError = amountError,
                    supportingText = if (amountError) { { Text(stringResource(R.string.payment_amount_error)) } } else null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = selectedDate.format(DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format))),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.payment_due_date)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        TextButton(onClick = { showDatePicker = true }) {
                            Text(stringResource(R.string.common_edit))
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.payment_status),
                        style = LocalAppTheme.typography.titleMedium
                    )
                    Switch(checked = isPaid, onCheckedChange = { isPaid = it })
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val amt = amount.toDoubleOrNull()
                        if (name.isBlank()) nameError = true
                        if (amt == null || amt <= 0.0) amountError = true
                        if (name.isNotBlank() && amt != null && amt > 0.0) {
                            viewModel.savePayment(name, amt, selectedDate, isPaid)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.common_save))
                }

                if (!uiState.isNewPayment) {
                    OutlinedButton(
                        onClick = { viewModel.showDeleteDialog() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = LocalAppTheme.colors.error
                        )
                    ) {
                        Text(stringResource(R.string.payment_delete))
                    }
                }
            }
        }
    }

    if (showDatePicker) {
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text(stringResource(R.string.payment_delete)) },
            text = { Text(stringResource(R.string.payment_delete_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        uiState.payment?.let { payment ->
                            viewModel.deletePayment(payment)
                            navController.popBackStack()
                        }
                    }
                ) {
                    Text(stringResource(R.string.common_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                    Text(stringResource(R.string.common_no))
                }
            }
        )
    }

}

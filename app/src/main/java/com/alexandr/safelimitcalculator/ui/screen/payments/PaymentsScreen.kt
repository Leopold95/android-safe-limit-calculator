package com.alexandr.safelimitcalculator.ui.screen.payments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.data.model.Payment
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import com.alexandr.safelimitcalculator.ui.navigation.Routes
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentsScreen(
    navController: NavController,
    viewModel: PaymentsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showAddDialog by viewModel.showAddDialog.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(LocalAppTheme.dimen.medium),
            verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.medium)
        ) {
            item {
                PaymentsSummaryCard(
                    totalDue = uiState.totalDue,
                    paidCount = uiState.paidCount,
                    unpaidCount = uiState.unpaidCount
                )
            }

            if (uiState.filteredPayments.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.payments_no_payments),
                        style = LocalAppTheme.typography.bodyMedium,
                        color = LocalAppTheme.colors.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.filteredPayments.sortedBy { it.dueDate }) { payment ->
                    PaymentItemCard(
                        payment = payment,
                        onClick = { navController.navigate(Routes.paymentDetails(payment.id)) }
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = LocalAppTheme.dimen.spacing48)
        )

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = viewModel::showAddDialog,
            containerColor = LocalAppTheme.colors.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.payments_add))
        }

        if (showAddDialog) {
            AddPaymentDialog(
                onDismiss = viewModel::hideAddDialog,
                onConfirm = { name, amount, dueDate, isPaid ->
                    viewModel.addPayment(name, amount, dueDate, isPaid)
                    val addedMessage = context.getString(R.string.payments_added_success, name)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = addedMessage,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun PaymentsSummaryCard(totalDue: Double, paidCount: Int, unpaidCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LocalAppTheme.colors.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalAppTheme.dimen.spacing20),
            verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.spacing12)
        ) {
            Text(
                text = stringResource(R.string.payments_total),
                style = LocalAppTheme.typography.titleMedium,
                color = LocalAppTheme.colors.onPrimaryContainer
            )
            Text(
                text = "$${"%.2f".format(totalDue)}",
                style = LocalAppTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = LocalAppTheme.colors.onPrimaryContainer
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.payments_paid),
                        style = LocalAppTheme.typography.bodySmall,
                        color = LocalAppTheme.colors.onPrimaryContainer
                    )
                    Text(
                        text = "$paidCount",
                        style = LocalAppTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = LocalAppTheme.colors.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.payments_unpaid),
                        style = LocalAppTheme.typography.bodySmall,
                        color = LocalAppTheme.colors.onPrimaryContainer
                    )
                    Text(
                        text = "$unpaidCount",
                        style = LocalAppTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = LocalAppTheme.colors.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentItemCard(payment: Payment, onClick: () -> Unit) {
    val isOverdue = !payment.isPaid && payment.dueDate < LocalDate.now()
    val isDueSoon = !payment.isPaid && payment.dueDate <= LocalDate.now().plusDays(3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = when {
                payment.isPaid -> LocalAppTheme.colors.surfaceVariant
                isOverdue -> LocalAppTheme.colors.errorContainer
                isDueSoon -> LocalAppTheme.colors.tertiaryContainer
                else -> LocalAppTheme.colors.surface
            }
        )
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
                    style = LocalAppTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        payment.isPaid -> LocalAppTheme.colors.onSurfaceVariant
                        isOverdue -> LocalAppTheme.colors.onErrorContainer
                        isDueSoon -> LocalAppTheme.colors.onTertiaryContainer
                        else -> LocalAppTheme.colors.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(LocalAppTheme.dimen.extraSmall))
                Text(
                    text = payment.dueDate.format(
                        DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format))
                    ),
                    style = LocalAppTheme.typography.bodySmall,
                    color = when {
                        payment.isPaid -> LocalAppTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                        isOverdue -> LocalAppTheme.colors.onErrorContainer.copy(alpha = 0.7f)
                        isDueSoon -> LocalAppTheme.colors.onTertiaryContainer.copy(alpha = 0.7f)
                        else -> LocalAppTheme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                    }
                )
                if (isOverdue) {
                    Text(
                        text = stringResource(R.string.payments_overdue),
                        style = LocalAppTheme.typography.labelSmall,
                        color = LocalAppTheme.colors.error,
                        fontWeight = FontWeight.Bold
                    )
                } else if (isDueSoon) {
                    Text(
                        text = stringResource(R.string.payments_due_soon),
                        style = LocalAppTheme.typography.labelSmall,
                        color = LocalAppTheme.colors.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$${"%.2f".format(payment.amount)}",
                    style = LocalAppTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        payment.isPaid -> LocalAppTheme.colors.onSurfaceVariant
                        isOverdue -> LocalAppTheme.colors.onErrorContainer
                        isDueSoon -> LocalAppTheme.colors.onTertiaryContainer
                        else -> LocalAppTheme.colors.onSurface
                    }
                )
                if (payment.isPaid) {
                    Spacer(modifier = Modifier.height(LocalAppTheme.dimen.extraSmall))
                    Text(
                        text = stringResource(R.string.payments_paid),
                        style = LocalAppTheme.typography.labelSmall,
                        color = LocalAppTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, LocalDate, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now().plusDays(7)) }
    var isPaid by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
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
        title = { Text(stringResource(R.string.dialog_add_payment_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppTheme.dimen.small),
                verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.spacing12)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 50) {
                            name = it
                            nameError = false
                        }
                    },
                    label = { Text(stringResource(R.string.payment_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = nameError,
                    supportingText = if (nameError) {{
                        Text(stringResource(R.string.payment_name_error))
                    }} else null
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { newValue ->
                        if (newValue.length <= 12 && newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                            amount = newValue
                            amountError = false
                        }
                    },
                    label = { Text(stringResource(R.string.payment_amount)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = amountError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    supportingText = if (amountError) {{
                        Text(stringResource(R.string.payment_amount_error))
                    }} else null
                )

                OutlinedTextField(
                    value = selectedDate.format(
                        DateTimeFormatter.ofPattern(stringResource(R.string.common_date_format))
                    ),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.payment_due_date)) },
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.payment_status),
                            style = LocalAppTheme.typography.titleMedium
                        )
                        Text(
                            text = if (isPaid)
                                stringResource(R.string.payments_paid)
                            else
                                stringResource(R.string.payments_unpaid),
                            style = LocalAppTheme.typography.bodySmall,
                            color = if (isPaid)
                                LocalAppTheme.colors.primary
                            else
                                LocalAppTheme.colors.error
                        )
                    }
                    Switch(
                        checked = isPaid,
                        onCheckedChange = { isPaid = it }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val nameVal = name.trim()
                    val amountVal = amount.toDoubleOrNull()

                    var hasError = false

                    if (nameVal.isEmpty()) {
                        nameError = true
                        hasError = true
                    }

                    if (amountVal == null || amountVal <= 0) {
                        amountError = true
                        hasError = true
                    }

                    if (!hasError) {
                        onConfirm(nameVal, amountVal!!, selectedDate, isPaid)
                    }
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

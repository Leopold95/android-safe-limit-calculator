package com.alexandr.safelimitcalculator.ui.screen.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat
import com.alexandr.safelimitcalculator.BuildConfig
import com.alexandr.safelimitcalculator.R
import com.alexandr.safelimitcalculator.theme.LocalAppTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val showReserveDialog by viewModel.showReserveDialog.collectAsState()
    val showResetDialog by viewModel.showResetDialog.collectAsState()
    val context = LocalContext.current

    // Track which toggle triggered the permission request
    var pendingToggle by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && pendingToggle != null) {
            when (pendingToggle) {
                "payment_reminders" -> viewModel.setPaymentRemindersEnabled(true)
                "limit_warnings" -> viewModel.setLimitWarningsEnabled(true)
                "daily_summary" -> viewModel.setDailySummaryEnabled(true)
            }
        }
        pendingToggle = null
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestNotificationPermissionAndEnable(toggleKey: String, enableAction: (Boolean) -> Unit, enabled: Boolean) {
        if (!enabled) {
            enableAction(false)
            return
        }
        if (hasNotificationPermission()) {
            enableAction(true)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pendingToggle = toggleKey
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.medium)
    ) {
        Text(
            text = stringResource(R.string.settings_general),
            style = LocalAppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = LocalAppTheme.colors.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.showReserveDialog() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppTheme.dimen.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.settings_reserve_amount),
                        style = LocalAppTheme.typography.titleMedium,
                        color = LocalAppTheme.colors.onSurface
                    )
                    Text(
                        text = "$${"%.2f".format(uiState.userData?.reserve ?: 0.0)}",
                        style = LocalAppTheme.typography.bodyMedium,
                        color = LocalAppTheme.colors.onSurfaceVariant
                    )
                }
            }
        }

        HorizontalDivider()

        Text(
            text = stringResource(R.string.settings_notifications),
            style = LocalAppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = LocalAppTheme.colors.primary
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppTheme.dimen.medium),
                verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.small)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_payment_reminders),
                        style = LocalAppTheme.typography.bodyLarge,
                        color = LocalAppTheme.colors.onSurface
                    )
                    Switch(
                        checked = uiState.paymentRemindersEnabled,
                        onCheckedChange = { enabled ->
                            requestNotificationPermissionAndEnable("payment_reminders", viewModel::setPaymentRemindersEnabled, enabled)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_limit_warnings),
                        style = LocalAppTheme.typography.bodyLarge,
                        color = LocalAppTheme.colors.onSurface
                    )
                    Switch(
                        checked = uiState.limitWarningsEnabled,
                        onCheckedChange = { enabled ->
                            requestNotificationPermissionAndEnable("limit_warnings", viewModel::setLimitWarningsEnabled, enabled)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_daily_summary),
                        style = LocalAppTheme.typography.bodyLarge,
                        color = LocalAppTheme.colors.onSurface
                    )
                    Switch(
                        checked = uiState.dailySummaryEnabled,
                        onCheckedChange = { enabled ->
                            requestNotificationPermissionAndEnable("daily_summary", viewModel::setDailySummaryEnabled, enabled)
                        }
                    )
                }
            }
        }

        HorizontalDivider()

        Text(
            text = stringResource(R.string.settings_data),
            style = LocalAppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = LocalAppTheme.colors.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = LocalAppTheme.colors.errorContainer
            ),
            onClick = { viewModel.showResetDialog() }
        ) {
            Text(
                text = stringResource(R.string.settings_reset_data),
                style = LocalAppTheme.typography.titleMedium,
                color = LocalAppTheme.colors.onErrorContainer,
                modifier = Modifier.padding(LocalAppTheme.dimen.medium)
            )
        }

        HorizontalDivider()

        Text(
            text = stringResource(R.string.settings_about),
            style = LocalAppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = LocalAppTheme.colors.primary
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(LocalAppTheme.dimen.medium),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.settings_version),
                    style = LocalAppTheme.typography.bodyLarge,
                    color = LocalAppTheme.colors.onSurface
                )
                Text(
                    text = BuildConfig.VERSION_NAME,
                    style = LocalAppTheme.typography.bodyLarge,
                    color = LocalAppTheme.colors.onSurfaceVariant
                )
            }
        }
    }

    if (showReserveDialog) {
        var reserveAmount by remember(uiState.userData) {
            mutableStateOf(uiState.userData?.reserve?.toString() ?: "")
        }

        AlertDialog(
            onDismissRequest = viewModel::hideReserveDialog,
            title = { Text(stringResource(R.string.settings_reserve_amount)) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(LocalAppTheme.dimen.small),
                    verticalArrangement = Arrangement.spacedBy(LocalAppTheme.dimen.spacing12)
                ) {
                    OutlinedTextField(
                        value = reserveAmount,
                        onValueChange = { newValue ->
                            if (newValue.length <= 12 && newValue.all { it.isDigit() || it == '.' } && newValue.count { it == '.' } <= 1) {
                                reserveAmount = newValue
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
                        val reserve = reserveAmount.toDoubleOrNull() ?: 0.0
                        viewModel.updateReserve(reserve)
                    }
                ) {
                    Text(stringResource(R.string.common_save))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideReserveDialog) {
                    Text(stringResource(R.string.common_cancel))
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = viewModel::hideResetDialog,
            title = { Text(stringResource(R.string.settings_reset_data)) },
            text = { Text(stringResource(R.string.settings_reset_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                    }
                ) {
                    Text(stringResource(R.string.common_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::hideResetDialog) {
                    Text(stringResource(R.string.common_no))
                }
            }
        )
    }
}

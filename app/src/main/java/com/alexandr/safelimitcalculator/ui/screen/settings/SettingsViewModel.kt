package com.alexandr.safelimitcalculator.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.model.UserData
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class SettingsUiState(
    val userData: UserData? = null,
    val paymentRemindersEnabled: Boolean = false,
    val limitWarningsEnabled: Boolean = false,
    val dailySummaryEnabled: Boolean = false,
    val isLoading: Boolean = true
)

class SettingsViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _showReserveDialog = MutableStateFlow(false)
    val showReserveDialog: StateFlow<Boolean> = _showReserveDialog.asStateFlow()

    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog.asStateFlow()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                repository.userDataFlow,
                repository.paymentRemindersEnabledFlow,
                repository.limitWarningsEnabledFlow,
                repository.dailySummaryEnabledFlow
            ) { userData, paymentReminders, limitWarnings, dailySummary ->
                SettingsUiState(
                    userData = userData,
                    paymentRemindersEnabled = paymentReminders,
                    limitWarningsEnabled = limitWarnings,
                    dailySummaryEnabled = dailySummary,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun showReserveDialog() {
        _showReserveDialog.value = true
    }

    fun hideReserveDialog() {
        _showReserveDialog.value = false
    }

    fun showResetDialog() {
        _showResetDialog.value = true
    }

    fun hideResetDialog() {
        _showResetDialog.value = false
    }

    fun updateReserve(reserve: Double) {
        viewModelScope.launch {
            val currentUserData = repository.userDataFlow.firstOrNull()
            if (currentUserData != null) {
                repository.saveUserData(
                    UserData(
                        balance = currentUserData.balance,
                        nextIncomeDate = currentUserData.nextIncomeDate,
                        reserve = reserve
                    )
                )
            }
            hideReserveDialog()
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.expensesFlow.firstOrNull()?.forEach { expense ->
                repository.deleteExpense(expense)
            }
            repository.paymentsFlow.firstOrNull()?.forEach { payment ->
                repository.deletePayment(payment)
            }
            hideResetDialog()
        }
    }

    fun setPaymentRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setPaymentRemindersEnabled(enabled)
        }
    }

    fun setLimitWarningsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setLimitWarningsEnabled(enabled)
        }
    }

    fun setDailySummaryEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setDailySummaryEnabled(enabled)
        }
    }
}

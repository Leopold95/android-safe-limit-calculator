package com.alexandr.safelimitcalculator.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.model.Payment
import com.alexandr.safelimitcalculator.data.model.UserData
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class HomeUiState(
    val userData: UserData? = null,
    val dailyLimit: Double = 0.0,
    val todaySpent: Double = 0.0,
    val upcomingPayments: List<Payment> = emptyList(),
    val daysUntilIncome: Long = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.userDataFlow,
        repository.getDailyLimitFlow(),
        repository.paymentsFlow,
        repository.getTotalSpentFlow(LocalDate.now(), LocalDate.now())
    ) { userData, dailyLimit, payments, todaySpent ->
        val daysUntilIncome = userData?.let {
            ChronoUnit.DAYS.between(LocalDate.now(), it.nextIncomeDate)
        } ?: 0

        val upcoming = payments
            .filter { !it.isPaid && it.dueDate >= LocalDate.now() }
            .sortedBy { it.dueDate }
            .take(5)

        HomeUiState(
            userData = userData,
            dailyLimit = dailyLimit,
            todaySpent = todaySpent ?: 0.0,
            upcomingPayments = upcoming,
            daysUntilIncome = daysUntilIncome,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun updateUserData(balance: Double, nextIncomeDate: LocalDate, reserve: Double) {
        viewModelScope.launch {
            val currentUserData = repository.userDataFlow.firstOrNull()
            val updatedUserData = currentUserData?.copy(
                balance = balance,
                nextIncomeDate = nextIncomeDate,
                reserve = reserve
            ) ?: UserData(
                balance = balance,
                nextIncomeDate = nextIncomeDate,
                reserve = reserve
            )
            repository.saveUserData(updatedUserData)
            _showUpdateDialog.value = false
        }
    }

    fun showUpdateDialog() {
        _showUpdateDialog.value = true
    }

    fun hideUpdateDialog() {
        _showUpdateDialog.value = false
    }
}

package com.alexandr.safelimitcalculator.ui.screen.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.model.Payment
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PaymentsUiState(
    val payments: List<Payment> = emptyList(),
    val filteredPayments: List<Payment> = emptyList(),
    val totalDue: Double = 0.0,
    val paidCount: Int = 0,
    val unpaidCount: Int = 0,
    val nextIncomeDate: LocalDate = LocalDate.now().plusMonths(1),
    val isLoading: Boolean = true
)

class PaymentsViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    val uiState: StateFlow<PaymentsUiState> = combine(
        repository.paymentsFlow,
        repository.userDataFlow
    ) { payments, userData ->
        val today = LocalDate.now()
        // If next income date is today/past (default/unset), keep a forward window so newly added payments are visible.
        val nextIncomeDate = if (userData.nextIncomeDate.isAfter(today)) {
            userData.nextIncomeDate
        } else {
            today.plusDays(30)
        }

        val filteredPayments = payments.filter { it.dueDate <= nextIncomeDate }
        val totalDue = filteredPayments.filter { !it.isPaid }.sumOf { it.amount }
        val paidCount = filteredPayments.count { it.isPaid }
        val unpaidCount = filteredPayments.count { !it.isPaid }

        PaymentsUiState(
            payments = payments,
            filteredPayments = filteredPayments,
            totalDue = totalDue,
            paidCount = paidCount,
            unpaidCount = unpaidCount,
            nextIncomeDate = nextIncomeDate,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PaymentsUiState()
    )

    fun showAddDialog() {
        _showAddDialog.value = true
    }

    fun hideAddDialog() {
        _showAddDialog.value = false
    }

    fun addPayment(name: String, amount: Double, dueDate: LocalDate, isPaid: Boolean) {
        viewModelScope.launch {
            repository.addPayment(
                Payment(
                    name = name,
                    amount = amount,
                    dueDate = dueDate,
                    isPaid = isPaid
                )
            )
            hideAddDialog()
        }
    }
}

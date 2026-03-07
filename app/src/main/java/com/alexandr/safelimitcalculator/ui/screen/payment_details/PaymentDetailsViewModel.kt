package com.alexandr.safelimitcalculator.ui.screen.payment_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.model.Payment
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PaymentDetailsUiState(
    val payment: Payment? = null,
    val isNewPayment: Boolean = true,
    val isLoading: Boolean = true,
    val isMissing: Boolean = false
)

class PaymentDetailsViewModel(
    private val repository: BudgetRepository,
    private val paymentId: Long
) : ViewModel() {

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    val uiState: StateFlow<PaymentDetailsUiState> = repository.paymentsFlow
        .map { payments ->
            val isNewPayment = paymentId == -1L
            val payment = payments.find { it.id == paymentId }
            PaymentDetailsUiState(
                payment = payment,
                isNewPayment = isNewPayment,
                isLoading = false,
                isMissing = !isNewPayment && payment == null
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaymentDetailsUiState(isNewPayment = paymentId == -1L)
        )

    fun showDeleteDialog() {
        _showDeleteDialog.value = true
    }

    fun hideDeleteDialog() {
        _showDeleteDialog.value = false
    }

    fun savePayment(name: String, amount: Double, dueDate: LocalDate, isPaid: Boolean) {
        viewModelScope.launch {
            val isNewPayment = paymentId == -1L
            val payment = Payment(
                id = if (isNewPayment) 0L else paymentId,
                name = name,
                amount = amount,
                dueDate = dueDate,
                isPaid = isPaid
            )

            if (isNewPayment) {
                repository.addPayment(payment)
            } else {
                repository.updatePayment(payment)
            }
        }
    }

    fun deletePayment(payment: Payment) {
        viewModelScope.launch {
            repository.deletePayment(payment)
            hideDeleteDialog()
        }
    }
}

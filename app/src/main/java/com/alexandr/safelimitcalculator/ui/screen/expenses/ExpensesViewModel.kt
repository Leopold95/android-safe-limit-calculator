package com.alexandr.safelimitcalculator.ui.screen.expenses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.model.Expense
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ExpensesUiState(
    val expenses: List<Expense> = emptyList(),
    val dailyLimit: Double = 0.0,
    val todaySpent: Double = 0.0,
    val categories: List<String> = listOf(
        "Food", "Transport", "Entertainment",
        "Shopping", "Health", "Utilities", "Other"
    ),
    val isLoading: Boolean = true
)

class ExpensesViewModel(
    private val repository: BudgetRepository
) : ViewModel() {

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    val uiState: StateFlow<ExpensesUiState> = combine(
        repository.expensesFlow,
        repository.getDailyLimitFlow(),
        repository.getTotalSpentFlow(LocalDate.now(), LocalDate.now())
    ) { expenses, dailyLimit, todaySpent ->
        ExpensesUiState(
            expenses = expenses.sortedByDescending { it.date },
            dailyLimit = dailyLimit,
            todaySpent = todaySpent ?: 0.0,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExpensesUiState()
    )

    fun showAddDialog() {
        _showAddDialog.value = true
    }

    fun hideAddDialog() {
        _showAddDialog.value = false
    }

    fun addExpense(amount: Double, category: String, description: String, date: LocalDate) {
        viewModelScope.launch {
            repository.addExpense(
                Expense(
                    amount = amount,
                    category = category,
                    description = description.ifBlank { null },
                    date = date
                )
            )
            hideAddDialog()
        }
    }
}


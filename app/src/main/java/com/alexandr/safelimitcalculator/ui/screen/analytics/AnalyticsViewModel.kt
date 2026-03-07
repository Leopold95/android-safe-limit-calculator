package com.alexandr.safelimitcalculator.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexandr.safelimitcalculator.data.model.Expense
import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class CategorySpending(
    val category: String,
    val amount: Double
)

data class AnalyticsUiState(
    val expenses: List<Expense> = emptyList(),
    val totalSpent: Double = 0.0,
    val averageDailySpending: Double = 0.0,
    val categoryBreakdown: List<CategorySpending> = emptyList(),
    val nextIncomeDate: LocalDate = LocalDate.now().plusMonths(1),
    val dailyLimit: Double = 0.0,
    val plannedTotalUntilIncome: Double = 0.0,
    val forecastTotalUntilIncome: Double = 0.0,
    val isLoading: Boolean = true
)

class AnalyticsViewModel(
    repository: BudgetRepository
) : ViewModel() {

    val uiState: StateFlow<AnalyticsUiState> = combine(
        repository.expensesFlow,
        repository.userDataFlow,
        repository.getDailyLimitFlow()
    ) { expenses, userData, dailyLimit ->
        val today = LocalDate.now()
        val nextIncome = userData.nextIncomeDate

        val totalSpent = expenses.sumOf { it.amount }

        val daysTracked = expenses.map { it.date }.distinct().size
        val averageDailySpending = if (daysTracked > 0) totalSpent / daysTracked else 0.0

        val categoryBreakdown = expenses
            .groupBy { it.category }
            .map { (category, expenseList) ->
                CategorySpending(
                    category = category,
                    amount = expenseList.sumOf { it.amount }
                )
            }
            .sortedByDescending { it.amount }

        val daysUntilIncome = ChronoUnit.DAYS.between(today, nextIncome).coerceAtLeast(0)
        val plannedTotalUntilIncome = dailyLimit * daysUntilIncome
        val forecastTotalUntilIncome = averageDailySpending * daysUntilIncome

        AnalyticsUiState(
            expenses = expenses,
            totalSpent = totalSpent,
            averageDailySpending = averageDailySpending,
            categoryBreakdown = categoryBreakdown,
            nextIncomeDate = nextIncome,
            dailyLimit = dailyLimit,
            plannedTotalUntilIncome = plannedTotalUntilIncome,
            forecastTotalUntilIncome = forecastTotalUntilIncome,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )
}

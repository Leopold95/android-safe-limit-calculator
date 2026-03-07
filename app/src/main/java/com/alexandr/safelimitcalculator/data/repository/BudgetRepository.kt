package com.alexandr.safelimitcalculator.data.repository

import com.alexandr.safelimitcalculator.data.local.datastore.PreferencesDataStore
import com.alexandr.safelimitcalculator.data.local.db.ExpenseDao
import com.alexandr.safelimitcalculator.data.local.db.PaymentDao
import com.alexandr.safelimitcalculator.data.model.Expense
import com.alexandr.safelimitcalculator.data.model.Payment
import com.alexandr.safelimitcalculator.data.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class BudgetRepository(
    private val expenseDao: ExpenseDao,
    private val paymentDao: PaymentDao,
    private val preferencesDataStore: PreferencesDataStore
) {

    val userDataFlow: Flow<UserData> = preferencesDataStore.userDataFlow

    val expensesFlow: Flow<List<Expense>> = expenseDao.getAll()

    val paymentsFlow: Flow<List<Payment>> = paymentDao.getAll()

    val onboardingShownFlow: Flow<Boolean> = preferencesDataStore.onboardingShownFlow

    val paymentRemindersEnabledFlow: Flow<Boolean> = preferencesDataStore.paymentRemindersEnabledFlow

    val limitWarningsEnabledFlow: Flow<Boolean> = preferencesDataStore.limitWarningsEnabledFlow

    val dailySummaryEnabledFlow: Flow<Boolean> = preferencesDataStore.dailySummaryEnabledFlow

    suspend fun saveUserData(userData: UserData) {
        preferencesDataStore.saveUserData(userData)
    }

    suspend fun setOnboardingShown(shown: Boolean) {
        preferencesDataStore.setOnboardingShown(shown)
    }

    suspend fun addExpense(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.update(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.delete(expense)
    }

    suspend fun addPayment(payment: Payment) {
        paymentDao.insert(payment)
    }

    suspend fun updatePayment(payment: Payment) {
        paymentDao.update(payment)
    }

    suspend fun deletePayment(payment: Payment) {
        paymentDao.delete(payment)
    }

    suspend fun setPaymentRemindersEnabled(enabled: Boolean) {
        preferencesDataStore.setPaymentRemindersEnabled(enabled)
    }

    suspend fun setLimitWarningsEnabled(enabled: Boolean) {
        preferencesDataStore.setLimitWarningsEnabled(enabled)
    }

    suspend fun setDailySummaryEnabled(enabled: Boolean) {
        preferencesDataStore.setDailySummaryEnabled(enabled)
    }

    fun getDailyLimitFlow(): Flow<Double> {
        return combine(userDataFlow, paymentsFlow) { userData, payments ->
            calculateDailyLimit(userData, payments)
        }
    }

    private fun calculateDailyLimit(userData: UserData, payments: List<Payment>): Double {
        val today = LocalDate.now()
        val daysUntilIncome = ChronoUnit.DAYS.between(today, userData.nextIncomeDate).toInt()
        if (daysUntilIncome <= 0) return 0.0

        val unpaidPayments = payments.filter { !it.isPaid && it.dueDate <= userData.nextIncomeDate }.sumOf { it.amount }
        val available = userData.balance - unpaidPayments - userData.reserve
        return if (available > 0) available / daysUntilIncome else 0.0
    }

    fun getTotalSpentFlow(startDate: LocalDate, endDate: LocalDate): Flow<Double?> {
        return expenseDao.getTotalSpent(startDate.toString(), endDate.toString())
    }

    fun getUnpaidPaymentsBefore(date: LocalDate): Flow<Double?> {
        return paymentDao.getUnpaidTotalBefore(date.toString())
    }
}

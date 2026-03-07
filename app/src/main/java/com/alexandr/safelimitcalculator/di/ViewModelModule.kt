package com.alexandr.safelimitcalculator.di

import com.alexandr.safelimitcalculator.ui.screen.analytics.AnalyticsViewModel
import com.alexandr.safelimitcalculator.ui.screen.expenses.ExpensesViewModel
import com.alexandr.safelimitcalculator.ui.screen.home.HomeViewModel
import com.alexandr.safelimitcalculator.ui.screen.main.MainViewModel
import com.alexandr.safelimitcalculator.ui.screen.onboarding.OnboardingViewModel
import com.alexandr.safelimitcalculator.ui.screen.payment_details.PaymentDetailsViewModel
import com.alexandr.safelimitcalculator.ui.screen.payments.PaymentsViewModel
import com.alexandr.safelimitcalculator.ui.screen.settings.SettingsViewModel
import com.alexandr.safelimitcalculator.ui.screen.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { PaymentsViewModel(get()) }
    viewModel { ExpensesViewModel(get()) }
    viewModel { AnalyticsViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { (paymentId: Long) -> PaymentDetailsViewModel(get(), paymentId) }
}


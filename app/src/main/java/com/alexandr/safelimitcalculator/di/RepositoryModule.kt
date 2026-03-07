package com.alexandr.safelimitcalculator.di

import com.alexandr.safelimitcalculator.data.repository.BudgetRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { BudgetRepository(get(), get(), get()) }
}

package com.alexandr.safelimitcalculator.di

import com.alexandr.safelimitcalculator.data.local.datastore.PreferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreModule = module {
    single { PreferencesDataStore(androidContext()) }
}

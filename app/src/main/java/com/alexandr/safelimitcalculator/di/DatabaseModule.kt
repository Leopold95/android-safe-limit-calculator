package com.alexandr.safelimitcalculator.di

import androidx.room.Room
import com.alexandr.safelimitcalculator.data.local.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "budget_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().expenseDao() }
    single { get<AppDatabase>().paymentDao() }
}

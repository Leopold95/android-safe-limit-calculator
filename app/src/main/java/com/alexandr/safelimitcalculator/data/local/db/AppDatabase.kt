package com.alexandr.safelimitcalculator.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alexandr.safelimitcalculator.data.model.Expense
import com.alexandr.safelimitcalculator.data.model.Payment

@Database(entities = [Expense::class, Payment::class], version = 1, exportSchema = false)
@TypeConverters(LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun paymentDao(): PaymentDao
}

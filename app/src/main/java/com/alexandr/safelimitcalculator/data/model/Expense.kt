package com.alexandr.safelimitcalculator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    val description: String? = null
)

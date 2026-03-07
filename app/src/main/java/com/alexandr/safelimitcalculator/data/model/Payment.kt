package com.alexandr.safelimitcalculator.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val amount: Double,
    @Serializable(with = LocalDateSerializer::class)
    val dueDate: LocalDate,
    val isPaid: Boolean = false
)

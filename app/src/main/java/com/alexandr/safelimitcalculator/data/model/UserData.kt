package com.alexandr.safelimitcalculator.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UserData(
    val balance: Double,
    @Serializable(with = LocalDateSerializer::class)
    val nextIncomeDate: LocalDate,
    val reserve: Double
)

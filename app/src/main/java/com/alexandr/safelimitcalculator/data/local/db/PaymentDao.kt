package com.alexandr.safelimitcalculator.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alexandr.safelimitcalculator.data.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(payment: Payment)

    @Update
    suspend fun update(payment: Payment)

    @Delete
    suspend fun delete(payment: Payment)

    @Query("SELECT * FROM payments ORDER BY dueDate ASC")
    fun getAll(): Flow<List<Payment>>

    @Query("SELECT SUM(amount) FROM payments WHERE isPaid = 0 AND dueDate <= :date")
    fun getUnpaidTotalBefore(date: String): Flow<Double?>
}

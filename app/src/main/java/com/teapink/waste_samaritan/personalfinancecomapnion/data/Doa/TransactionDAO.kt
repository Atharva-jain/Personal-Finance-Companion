package com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import androidx.room.OnConflictStrategy
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTransactions(transactions: List<Transaction>)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // Get all transactions ordered by most recent
    @Query("SELECT * FROM transactions ORDER BY dateMillis DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // Get transactions by specific type (Useful for charts/insights)
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY dateMillis DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>>

    // Calculate total balance (Income - Expense)
    @Query("""
        SELECT 
            COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) - 
            COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) 
        FROM transactions
    """)
    fun getTotalBalance(): Flow<Double>

    @Query("SELECT * FROM transactions WHERE dateMillis >= :startOfDay AND dateMillis <= :endOfDay")
    suspend fun getTransactionsForDateRange(startOfDay: Long, endOfDay: Long): List<Transaction>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND dateMillis >= :start AND dateMillis <= :end")
    suspend fun getExpenseSumForDateRange(start: Long, end: Long): Double?

}
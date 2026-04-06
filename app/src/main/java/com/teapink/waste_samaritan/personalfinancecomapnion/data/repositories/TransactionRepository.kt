package com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories

import com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa.TransactionDao
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.SavingsGoalEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.TransactionType
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart


class TransactionRepository(
    private val transactionDao: TransactionDao
) {

    fun getAllTransactions(): Flow<Resources<List<Transaction>>> {
        return transactionDao.getAllTransactions()
            .map<List<Transaction>, Resources<List<Transaction>>> { list ->
                Resources.Success(list)
            }.onStart { emit(Resources.Loading()) }.catch { e ->
                emit(Resources.Error(e.localizedMessage ?: "Unknown Database Error"))
            }
    }

    fun getTransactionsByType(type: TransactionType): Flow<Resources<List<Transaction>>> {
        return transactionDao.getTransactionsByType(type)
            .map<List<Transaction>, Resources<List<Transaction>>> { Resources.Success(it) }
            .onStart { emit(Resources.Loading()) }
            .catch { e -> emit(Resources.Error(e.localizedMessage ?: "Unknown Error")) }
    }

    fun getTotalBalance(): Flow<Resources<Double>> {
        return transactionDao.getTotalBalance().map<Double?, Resources<Double>> { balance ->
            // If the database is empty, balance might be null. Default to 0.0.
            Resources.Success(balance ?: 0.0)
        }.onStart {
            emit(Resources.Loading())
        }.catch { e ->
            emit(Resources.Error(e.localizedMessage ?: "Failed to load total balance"))
        }
    }

    suspend fun insertTransaction(transaction: Transaction): Resources<Unit> {
        return try {
            transactionDao.insertTransaction(transaction)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Failed to save transaction")
        }
    }

    suspend fun insertAllTransactions(transactions: List<Transaction>): Resources<Unit> {
        return try {
            transactionDao.insertAllTransactions(transactions)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Error saving")
        }
    }

    suspend fun updateTransaction(transaction: Transaction): Resources<Unit> {
        return try {
            transactionDao.updateTransaction(transaction)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Failed to update transaction")
        }
    }

    suspend fun deleteTransaction(transaction: Transaction): Resources<Unit> {
        return try {
            transactionDao.deleteTransaction(transaction)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Failed to delete transaction")
        }
    }


}
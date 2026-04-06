package com.teapink.waste_samaritan.personalfinancecomapnion.data.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val dateMillis: Long, // Store dates as epoch milliseconds
    val note: String? = null
)

enum class TransactionType {
    INCOME,
    EXPENSE
}
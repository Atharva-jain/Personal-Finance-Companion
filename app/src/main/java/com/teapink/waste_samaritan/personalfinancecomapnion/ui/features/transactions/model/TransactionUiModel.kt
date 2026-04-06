package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.model

// UI Model
data class TransactionUiModel(
    val id: Int,
    val amount: Double,
    val type: String,
    val category: String,
    val dateMillis: Long,
    val note: String
)
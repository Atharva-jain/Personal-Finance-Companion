package com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency

import java.text.NumberFormat
import java.util.Locale

// Utility function for formatting money
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return format.format(amount)
}

fun formatCurrencyWithMaxFraction(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}
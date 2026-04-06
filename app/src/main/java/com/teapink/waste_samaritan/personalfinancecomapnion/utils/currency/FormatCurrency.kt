package com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

// Update your formatter to inject the chosen symbol
fun formatCurrency(amount: Double, symbol: String): String {
    val format = NumberFormat.getCurrencyInstance(Locale.getDefault()) as DecimalFormat
    val symbols = format.decimalFormatSymbols
    symbols.currencySymbol = symbol // Overrides the default system symbol
    format.decimalFormatSymbols = symbols

    // Optional: Remove decimals for whole numbers to make it look cleaner
    if (amount % 1.0 == 0.0) {
        format.maximumFractionDigits = 0
    }

    return format.format(amount)
}
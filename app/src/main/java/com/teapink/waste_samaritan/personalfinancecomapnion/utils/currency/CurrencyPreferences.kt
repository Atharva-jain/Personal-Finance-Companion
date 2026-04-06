package com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

// 1. Define supported currencies
data class AppCurrency(val code: String, val symbol: String, val name: String)

object SupportedCurrencies {
    val list = listOf(
        AppCurrency("INR", "₹", "Indian Rupee"),
        AppCurrency("USD", "$", "US Dollar"),
        AppCurrency("EUR", "€", "Euro"),
        AppCurrency("GBP", "£", "British Pound"),
        AppCurrency("JPY", "¥", "Japanese Yen"),
        AppCurrency("AUD", "A$", "Australian Dollar"),
        AppCurrency("CAD", "C$", "Canadian Dollar")
    )

    // Default fallback
    val DEFAULT = list[0]
}

// 2. Preference Manager
class CurrencyPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)

    fun saveCurrencySymbol(symbol: String) {
        prefs.edit { putString("selected_currency", symbol) }
    }

    fun getCurrencySymbol(): String {
        return prefs.getString("selected_currency", SupportedCurrencies.DEFAULT.symbol) ?: SupportedCurrencies.DEFAULT.symbol
    }
}
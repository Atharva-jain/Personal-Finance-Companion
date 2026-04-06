package com.teapink.waste_samaritan.personalfinancecomapnion.utils.date

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// FORMATTERS
fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}

fun formatDate(dateMillis: Long): String {
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    return formatter.format(Date(dateMillis))
}

fun formatActiveDateRange(startMillis: Long, endMillis: Long): String {
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    val startStr = formatter.format(Date(startMillis))
    if (startMillis == endMillis) return startStr
    val endStr = formatter.format(Date(endMillis))
    return "$startStr - $endStr"
}

// Helper function to format relative dates for Sticky Headers
fun formatRelativeDate(millis: Long): String {
    val cal = Calendar.getInstance().apply { timeInMillis = millis }
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "Today"
        cal.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && cal.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR) -> "Yesterday"
        else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
    }
}


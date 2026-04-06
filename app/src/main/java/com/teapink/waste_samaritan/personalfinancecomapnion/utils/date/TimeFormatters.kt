package com.teapink.waste_samaritan.personalfinancecomapnion.utils.date

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun formatRelativeTime(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    val minutes = diff / (1000 * 60)
    val hours = minutes / 60
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min"
        hours < 24 -> "$hours h"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(millis))
    }
}
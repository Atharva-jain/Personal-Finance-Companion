package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.TransactionType
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.formatCurrencyWithMaxFraction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionRowItem(transaction: Transaction) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val iconBg = if (isExpense) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
    val iconColor = if (isExpense) Color(0xFFD32F2F) else Color(0xFF388E3C)
    val icon = if (isExpense) Icons.Rounded.ArrowUpward else Icons.Rounded.ArrowDownward

    val sdf = remember { SimpleDateFormat("dd MMM", Locale.getDefault()) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(iconBg), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(transaction.category, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(sdf.format(Date(transaction.dateMillis)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Text(
            text = "${if (isExpense) "-" else "+"}${formatCurrencyWithMaxFraction(transaction.amount)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isExpense) MaterialTheme.colorScheme.onSurface else Color(0xFF388E3C)
        )
    }
}

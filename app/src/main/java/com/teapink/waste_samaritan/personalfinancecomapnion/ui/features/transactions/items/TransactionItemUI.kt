package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.TransactionUiModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.date.formatCurrency



@Composable
fun TransactionItemUI(transaction: TransactionUiModel, onEdit: () -> Unit, onCardClick: () -> Unit) {
    val isIncome = transaction.type == "Income"
    val amountColor = if (isIncome) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface

    val icon: ImageVector = when (transaction.category) {
        "Food", "Dining Out" -> Icons.Rounded.Fastfood
        "Transport" -> Icons.Rounded.DirectionsCar
        "Bills" -> Icons.Rounded.Receipt
        "Shopping" -> Icons.Rounded.ShoppingBag
        "Salary" -> Icons.Rounded.AttachMoney
        else -> Icons.Rounded.AccountBalanceWallet
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onCardClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer) }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.category, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(transaction.note.ifBlank { "No note" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), maxLines = 1)
            }

            Text(
                text = "${if (isIncome) "+" else "-"} ${formatCurrency(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = amountColor
            )
        }
    }
}


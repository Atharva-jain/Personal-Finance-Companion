package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.Transaction
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.home.items.TransactionRowItem
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants

@Composable
fun RecentTransactionsSection(transactions: List<Transaction>, navController: NavHostController) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = { navController.navigate(Constants.TRANSACTIONS_SCREEN) }) {
                Text("See All")
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                transactions.forEachIndexed { index, transaction ->
                    TransactionRowItem(transaction)
                    if (index < transactions.size - 1) {
                        Divider(modifier = Modifier.padding(horizontal = 48.dp, vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}

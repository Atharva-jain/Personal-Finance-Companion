package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.dialog


import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Notes
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Category
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.TransactionUiModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.items.DetailRow
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.currency.formatCurrency
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.date.formatDate


@Composable
fun TransactionDetailDialog(
    transaction: TransactionUiModel, onDismiss: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit
) {
    val isIncome = transaction.type == "Income"
    val color = if (isIncome) Color(0xFF4CAF50) else Color(0xFFFF5252)
    val iconBgColor = if (isIncome) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp), contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(iconBgColor)
                            .padding(top = 24.dp, bottom = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 8.dp, top = 0.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = color)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = color.copy(alpha = 0.2f),
                                contentColor = color
                            ) {
                                Text(
                                    text = transaction.type.uppercase(),
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp, vertical = 4.dp
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "${if (isIncome) "+" else "-"} ${formatCurrency(transaction.amount)}",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = color
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DetailRow(
                            icon = Icons.Rounded.Category,
                            label = "Category",
                            value = transaction.category
                        )
                        DetailRow(
                            icon = Icons.Rounded.CalendarToday,
                            label = "Date",
                            value = formatDate(transaction.dateMillis)
                        )

                        if (transaction.note.isNotBlank()) {
                            DetailRow(
                                icon = Icons.Rounded.Notes, label = "Note", value = transaction.note
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onDelete()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Delete")
                            }

                            Button(
                                onClick = {
                                    onEdit()
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Edit")
                            }
                        }
                    }
                }
            }
        }
    }
}
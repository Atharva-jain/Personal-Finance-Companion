package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.TransactionUiModel
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChipDefaults
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.toggle_button.TypeToggleButton
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Constants



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTransactionForm(
    transactionToEdit: TransactionUiModel?,
    onSave: (amount: Double, type: String, category: String, dateMillis: Long, note: String) -> Unit
) {
    var amount by remember(transactionToEdit) {
        val initialAmount = transactionToEdit?.amount
        mutableStateOf(if (initialAmount != null) { if (initialAmount % 1.0 == 0.0) initialAmount.toInt().toString() else initialAmount.toString() } else "")
    }
    var note by remember(transactionToEdit) { mutableStateOf(transactionToEdit?.note ?: "") }
    var selectedType by remember(transactionToEdit) { mutableStateOf(transactionToEdit?.type ?: Constants.EXPENSE) }

    // NEW: Error state for the amount field
    var amountError by remember { mutableStateOf(false) }

    val expenseCategories = listOf("Food", "Transport", "Bills", "Shopping", "Health", "Housing", "Other")
    val incomeCategories = listOf("Salary", "Freelance", "Investment", "Gift", "Other")
    val categories = if (selectedType == Constants.EXPENSE) expenseCategories else incomeCategories

    var selectedCategory by remember(transactionToEdit, selectedType) {
        mutableStateOf(transactionToEdit?.category?.takeIf { categories.contains(it) } ?: categories.first())
    }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 32.dp)) {
        Text(if (transactionToEdit == null) "New Transaction" else "Edit Transaction", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TypeToggleButton("Expense", selectedType == Constants.EXPENSE, Color(0xFFFF5252), Modifier.weight(1f)) { selectedType = Constants.EXPENSE }
            TypeToggleButton("Income", selectedType == Constants.INCOME, Color(0xFF4CAF50), Modifier.weight(1f)) { selectedType = Constants.INCOME }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = {
                amount = it
                amountError = false // Clear error when typing
            },
            label = { Text("Amount") },
            prefix = { Text("₹ ") },
            isError = amountError, // Show red border if error
            supportingText = { if (amountError) Text("Please enter a valid amount greater than 0") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = note, onValueChange = { note = it }, label = { Text("Note (Optional)") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // FIX: Clean the string to prevent silent crashes (remove commas, spaces)
                val cleanAmount = amount.replace(",", "").trim()
                val parsedAmount = cleanAmount.toDoubleOrNull()

                if (parsedAmount != null && parsedAmount > 0) {
                    amountError = false
                    onSave(parsedAmount, selectedType, selectedCategory, transactionToEdit?.dateMillis ?: System.currentTimeMillis(), note)
                } else {
                    // FIX: Show error state if amount is invalid, so the button doesn't look "dead"
                    amountError = true
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)
        ) {
            Text("Save Transaction", fontSize = MaterialTheme.typography.titleMedium.fontSize, fontWeight = FontWeight.Bold)
        }
    }
}

package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.transactions.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDateRangePickerDialog(
    onDismiss: () -> Unit,
    onDateSelected: (startMillis: Long, endMillis: Long) -> Unit
) {
    // Creates a rule that only allows dates from today or the past
    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val start = dateRangePickerState.selectedStartDateMillis
                val end = dateRangePickerState.selectedEndDateMillis

                if (start != null) {
                    val finalEnd = end ?: start
                    // Send the selected dates back to the parent screen
                    onDateSelected(start, finalEnd)
                }
                onDismiss()
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            modifier = Modifier.weight(1f),
            title = {
                Text(
                    text = "Select Date Filter",
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp)
                )
            },
            showModeToggle = false
        )
    }
}
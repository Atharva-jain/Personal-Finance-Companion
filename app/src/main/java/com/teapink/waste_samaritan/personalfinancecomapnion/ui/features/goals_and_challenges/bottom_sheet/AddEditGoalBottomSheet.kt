package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LaptopMac
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.SavingsGoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalBottomSheet(
    goalToEdit: SavingsGoal?,
    onDismiss: () -> Unit,
    onSave: (title: String, target: Double, iconName: String, colorArgb: Long) -> Unit
) {
    var title by remember { mutableStateOf(goalToEdit?.title ?: "") }
    var targetAmount by remember {
        mutableStateOf(
            goalToEdit?.targetAmount?.toString()?.replace(".0", "") ?: ""
        )
    }

    // Pickers
    val availableColors = listOf(
        Color(0xFF2962FF),
        Color(0xFF00C853),
        Color(0xFFFFB300),
        Color(0xFFFF5252),
        Color(0xFF9C27B0),
        Color(0xFFE91E63)
    )
    var selectedColor by remember { mutableStateOf(goalToEdit?.color ?: availableColors[0]) }

    val availableIcons = listOf(
        "Laptop" to Icons.Rounded.LaptopMac,
        "Flight" to Icons.Rounded.FlightTakeoff,
        "Health" to Icons.Rounded.HealthAndSafety,
        "Car" to Icons.Rounded.DirectionsCar,
        "Home" to Icons.Rounded.Home
    )
    var selectedIconName by remember {
        mutableStateOf(
            availableIcons.find { it.second == goalToEdit?.icon }?.first ?: "Laptop"
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                if (goalToEdit == null) "Create New Goal" else "Edit Goal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Goal Title (e.g. MacBook)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = targetAmount,
                onValueChange = { targetAmount = it },
                label = { Text("Target Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                "Theme Color",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(availableColors) { color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                2.dp,
                                if (selectedColor == color) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                CircleShape
                            )
                            .clickable { selectedColor = color })
                }
            }

            Text(
                "Icon",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(availableIcons) { (name, icon) ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedIconName == name) selectedColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { selectedIconName = name },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = name,
                            tint = if (selectedIconName == name) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val amount = targetAmount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amount > 0) onSave(
                        title, amount, selectedIconName, selectedColor.toArgb().toLong()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save Goal", fontWeight = FontWeight.Bold) }
        }
    }
}
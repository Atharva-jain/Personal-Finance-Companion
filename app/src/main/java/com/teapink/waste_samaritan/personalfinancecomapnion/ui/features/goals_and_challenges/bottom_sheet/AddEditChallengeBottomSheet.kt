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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.view_model.Challenge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditChallengeBottomSheet(
    challengeToEdit: Challenge?,
    onDismiss: () -> Unit,
    onSave: (title: String, currentDays: Int, targetDays: Int, colorArgb: Long) -> Unit
) {
    var title by remember { mutableStateOf(challengeToEdit?.title ?: "") }
    var targetDays by remember { mutableStateOf(challengeToEdit?.targetDays?.toString() ?: "") }

    val availableColors = listOf(
        Color(0xFFFF5252),
        Color(0xFFFF9800),
        Color(0xFF00C853),
        Color(0xFF2962FF),
        Color(0xFF9C27B0)
    )
    var selectedColor by remember { mutableStateOf(challengeToEdit?.color ?: availableColors[0]) }

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
                if (challengeToEdit == null) "New Challenge" else "Edit Challenge",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Challenge Title (e.g. No Coffee)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = targetDays,
                onValueChange = { targetDays = it },
                label = { Text("Target Days (e.g. 30)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )

            Text(
                "Challenge Color",
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

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val days = targetDays.toIntOrNull() ?: 0
                    if (title.isNotBlank() && days > 0) onSave(
                        title,
                        challengeToEdit?.currentDays ?: 0,
                        days,
                        selectedColor.toArgb().toLong()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Save Challenge", fontWeight = FontWeight.Bold) }
        }
    }
}
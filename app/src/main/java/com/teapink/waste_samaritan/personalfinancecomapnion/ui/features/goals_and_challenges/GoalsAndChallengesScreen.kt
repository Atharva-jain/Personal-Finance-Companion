package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.bottom_sheet.AddEditChallengeBottomSheet
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.bottom_sheet.AddEditGoalBottomSheet
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.cards.DynamicChallengeCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.cards.EmptyStateCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.cards.SavingsGoalCard
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.dialog.AddFundsDialog
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.view_model.Challenge
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.view_model.GoalViewModel
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources

// --- UI Data Models ---
data class SavingsGoal(
    val id: Int,
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsAndChallengesScreen(
    modifier: Modifier = Modifier,
    viewModel: GoalViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val challengeState by viewModel.challengeState.collectAsStateWithLifecycle()
    val actionState by viewModel.actionState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog & Sheet States
    var isGoalSheetOpen by remember { mutableStateOf(false) }
    var goalToEdit by remember { mutableStateOf<SavingsGoal?>(null) }
    var goalToAddFunds by remember { mutableStateOf<SavingsGoal?>(null) }
    var addAmountText by remember { mutableStateOf("") }

    var isChallengeSheetOpen by remember { mutableStateOf(false) }
    var challengeToEdit by remember { mutableStateOf<Challenge?>(null) }

    LaunchedEffect(actionState) {
        if (actionState is Resources.Error) {
            snackbarHostState.showSnackbar((actionState as Resources.Error).message ?: "Error")
            viewModel.resetActionState()
        } else if (actionState is Resources.Success) {
            isGoalSheetOpen = false
            isChallengeSheetOpen = false
            viewModel.resetActionState()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },

        ) { internalPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = internalPadding.calculateBottomPadding())
        ) {
            val goals = (uiState as? Resources.Success)?.data ?: emptyList()
            val challenges = (challengeState as? Resources.Success)?.data ?: emptyList()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = internalPadding.calculateBottomPadding())
                    .padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(24.dp),

                contentPadding = PaddingValues(top = 0.dp, bottom = 32.dp)

            ) {
                // --- CHALLENGES SECTION ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Challenges",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                challengeToEdit = null
                                isChallengeSheetOpen = true
                            }, modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant, CircleShape
                                )
                                .size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Challenge",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (challenges.isEmpty()) {
                    item { EmptyStateCard("No active challenges.", Icons.Rounded.Flag) }
                } else {
                    items(challenges, key = { "chal_${it.id}" }) { challenge ->
                        DynamicChallengeCard(
                            modifier = Modifier.animateItem(), // Smooth animations!
                            challenge = challenge,
                            onIncrement = { viewModel.incrementChallenge(challenge.id) },
                            onEdit = {
                                challengeToEdit = challenge
                                isChallengeSheetOpen = true
                            },
                            onDelete = { viewModel.deleteChallenge(challenge.id) })
                    }
                }

                // --- GOALS SECTION ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    // REPLACED standard text with a Row and Add Button!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Active Goals",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = {
                                goalToEdit = null
                                isGoalSheetOpen = true
                            }, modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant, CircleShape
                                )
                                .size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Goal",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                if (goals.isEmpty()) {
                    item {
                        EmptyStateCard(
                            "No active goals yet. Add one!", Icons.Rounded.TrackChanges
                        )
                    }
                } else {
                    items(goals, key = { "goal_${it.id}" }) { goal ->
                        SavingsGoalCard(
                            modifier = Modifier.animateItem(), // Smooth animations!
                            goal = goal, onAddFunds = {
                                goalToAddFunds = goal
                                addAmountText = ""
                            }, onEdit = {
                                goalToEdit = goal
                                isGoalSheetOpen = true
                            }, onDelete = { viewModel.deleteGoal(goal.id) })
                    }
                }
            }
        }
    }

    // --- Popups and Sheets ---
    if (isGoalSheetOpen) {
        AddEditGoalBottomSheet(
            goalToEdit = goalToEdit,
            onDismiss = { isGoalSheetOpen = false },
            onSave = { title, target, icon, color ->
                if (goalToEdit == null) viewModel.addGoal(title, target, icon, color)
                else viewModel.updateGoalDetails(goalToEdit!!.id, title, target, icon, color)
            })
    }

    if (isChallengeSheetOpen) {
        AddEditChallengeBottomSheet(
            challengeToEdit = challengeToEdit,
            onDismiss = { isChallengeSheetOpen = false },
            onSave = { title, current, target, color ->
                if (challengeToEdit == null) viewModel.addChallenge(title, target, color)
                else viewModel.updateChallengeDetails(
                    challengeToEdit!!.id, title, current, target, color
                )
            })
    }

    // 3. Add Funds Dialog
    if (goalToAddFunds != null) {
        AddFundsDialog(
            goal = goalToAddFunds!!,
            onDismiss = { goalToAddFunds = null },
            onConfirm = { amount ->
                viewModel.addFundsToGoal(goalToAddFunds!!.id, amount)
                goalToAddFunds = null // Close dialog after adding
            })
    }
}



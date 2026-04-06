package com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.view_model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FlightTakeoff
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.LaptopMac
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.ChallengeEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.SavingsGoalEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.ChallengeRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories.GoalRepository
import com.teapink.waste_samaritan.personalfinancecomapnion.ui.features.goals_and_challenges.SavingsGoal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Home

data class Challenge(
    val id: Int,
    val title: String,
    val currentDays: Int,
    val targetDays: Int,
    val icon: ImageVector,
    val color: Color
)


class GoalViewModel(
    private val repository: GoalRepository, private val challengeRepository: ChallengeRepository
) : ViewModel() {

    // 1. UI State for the List of Goals
    val uiState: StateFlow<Resources<List<SavingsGoal>>> =
        repository.getAllGoals().map { resource ->
                when (resource) {
                    is Resources.Loading -> Resources.Loading()
                    is Resources.Error -> Resources.Error(resource.message ?: "Unknown Error")
                    is Resources.Idle -> Resources.Idle()
                    is Resources.Success -> {
                        val mappedGoals = resource.data?.map { entity ->
                            SavingsGoal(
                                id = entity.id,
                                title = entity.title,
                                targetAmount = entity.targetAmount,
                                savedAmount = entity.savedAmount,
                                icon = mapStringToIcon(entity.iconName),
                                // FIX: Convert the saved integer back to a valid Compose Color
                                color = Color(entity.colorArgb.toInt())
                            )
                        } ?: emptyList()
                        Resources.Success(mappedGoals)
                    }
                }
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Resources.Loading()
            )

    // 2. Action State for showing Snackbars on Save/Update/Delete
    private val _actionState = MutableStateFlow<Resources<Unit>>(Resources.Idle())
    val actionState: StateFlow<Resources<Unit>> = _actionState.asStateFlow()

    fun resetActionState() {
        _actionState.value = Resources.Idle()
    }


    // 3. Logic to add funds
    fun addFundsToGoal(goalId: Int, amountToAdd: Double) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()
            val currentList = (uiState.value as? Resources.Success)?.data ?: return@launch
            val currentGoal = currentList.find { it.id == goalId } ?: return@launch

            val updatedEntity = SavingsGoalEntity(
                id = currentGoal.id, title = currentGoal.title, targetAmount = currentGoal.targetAmount,
                savedAmount = currentGoal.savedAmount + amountToAdd,

                // FIX: Pass the icon directly to the mapper!
                iconName = mapIconToString(currentGoal.icon),

                colorArgb = currentGoal.color.toArgb().toLong()
            )
            _actionState.value = repository.updateGoal(updatedEntity)
        }
    }

    // --- CRUD OPERATIONS ---
    fun addGoal(title: String, targetAmount: Double, iconName: String, colorArgb: Long) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()
            val newGoal = SavingsGoalEntity(
                title = title, targetAmount = targetAmount, savedAmount = 0.0, // Starts at 0
                iconName = iconName, colorArgb = colorArgb
            )
            _actionState.value = repository.insertGoal(newGoal)
        }
    }

    fun updateGoalDetails(
        goalId: Int, title: String, targetAmount: Double, iconName: String, colorArgb: Long
    ) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()
            val currentList = (uiState.value as? Resources.Success)?.data ?: return@launch
            val currentGoal = currentList.find { it.id == goalId } ?: return@launch

            val updatedEntity = SavingsGoalEntity(
                id = goalId,
                title = title,
                targetAmount = targetAmount,
                savedAmount = currentGoal.savedAmount,
                iconName = iconName,
                // FIX: Save it safely as an ARGB Int cast to Long
                colorArgb = Color(colorArgb).toArgb().toLong()
            )
            _actionState.value = repository.updateGoal(updatedEntity)
        }
    }

    fun deleteGoal(goalId: Int) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()
            val currentList = (uiState.value as? Resources.Success)?.data ?: return@launch
            val goalToDelete = currentList.find { it.id == goalId } ?: return@launch

            val entityToDelete = SavingsGoalEntity(
                id = goalToDelete.id, title = goalToDelete.title, targetAmount = goalToDelete.targetAmount,
                savedAmount = goalToDelete.savedAmount,

                // FIX: Pass the icon directly to the mapper!
                iconName = mapIconToString(goalToDelete.icon),

                colorArgb = goalToDelete.color.toArgb().toLong()
            )
            _actionState.value = repository.deleteGoal(entityToDelete)
        }
    }


    // --- MAPPING HELPERS ---
    private fun mapStringToIcon(iconName: String) = when (iconName) {
        "Laptop" -> Icons.Rounded.LaptopMac
        "Flight" -> Icons.Rounded.FlightTakeoff
        "Health" -> Icons.Rounded.HealthAndSafety
        "Car" -> Icons.Rounded.DirectionsCar    // NEW
        "Home" -> Icons.Rounded.Home            // NEW
        else -> Icons.Rounded.Star
    }

    private fun mapIconToString(icon: ImageVector) = when (icon) {
        Icons.Rounded.LaptopMac -> "Laptop"
        Icons.Rounded.FlightTakeoff -> "Flight"
        Icons.Rounded.HealthAndSafety -> "Health"
        Icons.Rounded.DirectionsCar -> "Car"    // NEW
        Icons.Rounded.Home -> "Home"            // NEW
        else -> "Star"
    }

    // challenges section
    val challengeState: StateFlow<Resources<List<Challenge>>> =
        challengeRepository.getAllChallenges().map { resource ->
                when (resource) {
                    is Resources.Loading -> Resources.Loading()
                    is Resources.Error -> Resources.Error(resource.message ?: "Unknown Error")
                    is Resources.Idle -> Resources.Idle()
                    is Resources.Success -> {
                        val mapped = resource.data?.map { entity ->
                            Challenge(
                                id = entity.id,
                                title = entity.title,
                                currentDays = entity.currentDays,
                                targetDays = entity.targetDays,
                                icon = Icons.Rounded.LocalFireDepartment, // Defaulting to Fire for challenges
                                color = Color(entity.colorArgb.toInt())
                            )
                        } ?: emptyList()
                        Resources.Success(mapped)
                    }
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resources.Loading())

    // --- CHALLENGE CRUD FUNCTIONS ---
    fun addChallenge(title: String, targetDays: Int, colorArgb: Long) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()
            val newChallenge = ChallengeEntity(
                title = title,
                currentDays = 0,
                targetDays = targetDays,
                iconName = "Fire",
                colorArgb = Color(colorArgb).toArgb().toLong()
            )
            _actionState.value = challengeRepository.insertChallenge(newChallenge)
        }
    }

    fun updateChallengeDetails(
        id: Int, title: String, currentDays: Int, targetDays: Int, colorArgb: Long
    ) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()
            val updated = ChallengeEntity(
                id = id,
                title = title,
                currentDays = currentDays,
                targetDays = targetDays,
                iconName = "Fire",
                colorArgb = Color(colorArgb).toArgb().toLong()
            )
            _actionState.value = challengeRepository.updateChallenge(updated)
        }
    }

    fun incrementChallenge(challengeId: Int) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()
            val currentList = (challengeState.value as? Resources.Success)?.data ?: return@launch
            val chal = currentList.find { it.id == challengeId } ?: return@launch

            if (chal.currentDays < chal.targetDays) {
                // FIX: Update the database directly using the safe ARGB extraction
                val updatedEntity = ChallengeEntity(
                    id = chal.id,
                    title = chal.title,
                    currentDays = chal.currentDays + 1,
                    targetDays = chal.targetDays,
                    iconName = "Fire", // Defaults to Fire for challenges
                    colorArgb = chal.color.toArgb().toLong() // Safely extract the raw color!
                )
                _actionState.value = challengeRepository.updateChallenge(updatedEntity)
            } else {
                // Challenge is already completed
                _actionState.value = Resources.Success(Unit)
            }
        }
    }

    fun deleteChallenge(challengeId: Int) {
        viewModelScope.launch {
            _actionState.value = Resources.Loading()
            val currentList = (challengeState.value as? Resources.Success)?.data ?: return@launch
            val chal = currentList.find { it.id == challengeId } ?: return@launch
            val entity = ChallengeEntity(
                chal.id,
                chal.title,
                chal.currentDays,
                chal.targetDays,
                "Fire",
                chal.color.toArgb().toLong()
            )
            _actionState.value = challengeRepository.deleteChallenge(entity)
        }
    }


}
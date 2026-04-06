package com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories

import com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa.GoalDao
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.ChallengeEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.SavingsGoalEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class GoalRepository(
    private val goalDao: GoalDao
) {

    fun getAllGoals(): Flow<Resources<List<SavingsGoalEntity>>> {
        return goalDao.getAllGoals()
            .map<List<SavingsGoalEntity>, Resources<List<SavingsGoalEntity>>> { goals ->
                Resources.Success(goals)
            }
            .onStart {
                emit(Resources.Loading())
            }
            .catch { e ->
                emit(Resources.Error(e.localizedMessage ?: "Failed to fetch goals"))
            }
    }

    suspend fun insertGoal(goal: SavingsGoalEntity): Resources<Unit> {
        return try {
            goalDao.insertGoal(goal)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Failed to save goal")
        }
    }

    suspend fun insertAllGoals(goals: List<SavingsGoalEntity>): Resources<Unit> {
        return try {
            goalDao.insertAllGoals(goals)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Error saving")
        }
    }

    suspend fun updateGoal(goal: SavingsGoalEntity): Resources<Unit> {
        return try {
            goalDao.updateGoal(goal)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Failed to update goal")
        }
    }

    suspend fun deleteGoal(goal: SavingsGoalEntity): Resources<Unit> {
        return try {
            goalDao.deleteGoal(goal)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Failed to delete goal")
        }
    }
}
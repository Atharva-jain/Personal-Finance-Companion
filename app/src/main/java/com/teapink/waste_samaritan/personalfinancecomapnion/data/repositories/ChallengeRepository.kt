package com.teapink.waste_samaritan.personalfinancecomapnion.data.repositories

import com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa.ChallengeDao
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.ChallengeEntity
import com.teapink.waste_samaritan.personalfinancecomapnion.utils.Resources
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ChallengeRepository(private val challengeDao: ChallengeDao) {
    fun getAllChallenges(): Flow<Resources<List<ChallengeEntity>>> {
        return challengeDao.getAllChallenges()
            .map<List<ChallengeEntity>, Resources<List<ChallengeEntity>>> { Resources.Success(it) }
            .onStart { emit(Resources.Loading()) }.catch { e ->
                emit(
                    Resources.Error(
                        e.localizedMessage ?: "Failed to fetch challenges"
                    )
                )
            }
    }

    suspend fun insertChallenge(challenge: ChallengeEntity): Resources<Unit> {
        return try {
            challengeDao.insertChallenge(challenge)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Error saving")
        }
    }

    suspend fun insertAllChallenges(challenges: List<ChallengeEntity>): Resources<Unit> {
        return try {
            challengeDao.insertAllChallenges(challenges)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Error saving")
        }
    }

    suspend fun updateChallenge(challenge: ChallengeEntity): Resources<Unit> {
        return try {
            challengeDao.updateChallenge(challenge)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Error updating")
        }
    }

    suspend fun deleteChallenge(challenge: ChallengeEntity): Resources<Unit> {
        return try {
            challengeDao.deleteChallenge(challenge)
            Resources.Success(Unit)
        } catch (e: Exception) {
            Resources.Error(e.localizedMessage ?: "Error deleting")
        }
    }
}
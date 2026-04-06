package com.teapink.waste_samaritan.personalfinancecomapnion.data.Doa

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.teapink.waste_samaritan.personalfinancecomapnion.data.model.ChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY id ASC")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChallenges(challenges: List<ChallengeEntity>)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Delete
    suspend fun deleteChallenge(challenge: ChallengeEntity)


    @Query("SELECT * FROM challenges ORDER BY id DESC")
    suspend fun getAllChallengesSync(): List<ChallengeEntity>


}
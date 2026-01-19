package com.peekr.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.peekr.core.data.source.local.database.entity.UserKeywordDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserKeywordDetailDao {
    @Query("SELECT * FROM UserKeywordDetailEntity")
    fun getAll(): Flow<List<UserKeywordDetailEntity>>

    @Query("SELECT * FROM UserKeywordDetailEntity WHERE userKeywordId = :userKeywordId")
    suspend fun getById(userKeywordId: Long): UserKeywordDetailEntity?

    @Upsert
    suspend fun upsertAll(userKeywordDetails: List<UserKeywordDetailEntity>)

    @Query("DELETE FROM UserKeywordDetailEntity")
    suspend fun deleteAll()
}

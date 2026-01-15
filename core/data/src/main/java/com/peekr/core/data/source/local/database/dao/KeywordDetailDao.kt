package com.peekr.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.peekr.core.data.source.local.database.entity.KeywordDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeywordDetailDao {
    @Query("SELECT * FROM KeywordDetailEntity")
    fun getAll(): Flow<List<KeywordDetailEntity>>

    @Query("SELECT * FROM KeywordDetailEntity WHERE userKeywordId = :userKeywordId")
    fun getById(userKeywordId: Long): Flow<KeywordDetailEntity?>

    @Upsert
    suspend fun upsertAll(keywordDetails: List<KeywordDetailEntity>)

    @Query("DELETE FROM KeywordDetailEntity")
    suspend fun deleteAll()
}

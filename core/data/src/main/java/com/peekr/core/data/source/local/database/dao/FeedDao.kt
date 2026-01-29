package com.peekr.core.data.source.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.peekr.core.data.source.local.database.entity.FeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Query("SELECT * FROM FeedEntity ORDER BY score DESC, createdAt DESC, userKeywordId DESC")
    fun getPagingSource(): PagingSource<Int, FeedEntity>

    @Query("SELECT * FROM FeedEntity ORDER BY score DESC, createdAt DESC, userKeywordId DESC")
    fun getAll(): Flow<List<FeedEntity>>

    @Query("SELECT * FROM FeedEntity WHERE userKeywordId = :userKeywordId")
    suspend fun getById(userKeywordId: Long): FeedEntity?

    @Upsert
    suspend fun upsertAll(feeds: List<FeedEntity>)

    @Query("DELETE FROM FeedEntity")
    suspend fun deleteAll()
}

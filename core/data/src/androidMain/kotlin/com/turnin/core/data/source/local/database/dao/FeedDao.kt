package com.turnin.core.data.source.local.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.turnin.core.data.source.local.database.entity.FeedEntity
import com.turnin.core.domain.feed.model.FeedType
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {
    @Query("SELECT * FROM FeedEntity WHERE type = :type ORDER BY sortOrder ASC")
    fun getPagingSource(type: FeedType): PagingSource<Int, FeedEntity>

    @Query("SELECT COUNT(*) FROM FeedEntity WHERE type = :type")
    suspend fun countByType(type: FeedType): Int

    @Query("SELECT * FROM FeedEntity WHERE type = :type ORDER BY sortOrder ASC")
    fun getAll(type: FeedType): Flow<List<FeedEntity>>

    @Query("SELECT * FROM FeedEntity WHERE userKeywordId = :userKeywordId")
    suspend fun getById(userKeywordId: Long): FeedEntity?

    @Upsert
    suspend fun upsertAll(feeds: List<FeedEntity>)

    @Query("DELETE FROM FeedEntity WHERE type = :type")
    suspend fun clearByType(type: FeedType)
}

package com.turnin.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.turnin.core.data.source.local.database.entity.FeedRemoteKeyEntity
import com.turnin.core.domain.feed.model.FeedType

@Dao
interface FeedRemoteKeyDao {
    @Query("SELECT * FROM FeedRemoteKeyEntity WHERE type = :type")
    suspend fun getRemoteKeyByType(type: FeedType): FeedRemoteKeyEntity?

    @Upsert
    suspend fun upsert(remoteKey: FeedRemoteKeyEntity)

    @Query("DELETE FROM FeedRemoteKeyEntity WHERE type = :type")
    suspend fun clearByType(type: FeedType)
}

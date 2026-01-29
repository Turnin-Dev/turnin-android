package com.peekr.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.peekr.core.data.source.local.database.entity.FeedRemoteKeyEntity

@Dao
interface FeedRemoteKeyDao {
    @Query("SELECT * FROM FeedRemoteKeyEntity WHERE id = '${FeedRemoteKeyEntity.SINGLE_CURSOR_ID}'")
    suspend fun getRemoteKey(): FeedRemoteKeyEntity?

    @Upsert
    suspend fun upsert(remoteKey: FeedRemoteKeyEntity)

    @Query("DELETE FROM FeedRemoteKeyEntity")
    suspend fun deleteAll()
}

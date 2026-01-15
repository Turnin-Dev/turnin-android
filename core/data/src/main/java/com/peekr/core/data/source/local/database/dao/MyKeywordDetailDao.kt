package com.peekr.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.peekr.core.data.source.local.database.entity.MyKeywordDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyKeywordDetailDao {
    @Query("SELECT * FROM MyKeywordDetailEntity")
    fun getAll(): Flow<List<MyKeywordDetailEntity>>

    @Query("SELECT * FROM MyKeywordDetailEntity WHERE userKeywordId = :userKeywordId")
    fun getById(userKeywordId: Long): Flow<MyKeywordDetailEntity?>

    @Upsert
    suspend fun upsertAll(myKeywordDetails: List<MyKeywordDetailEntity>)

    @Upsert
    suspend fun upsert(myKeywordDetail: MyKeywordDetailEntity)

    @Query("UPDATE MyKeywordDetailEntity SET description = :description WHERE userKeywordId = :userKeywordId")
    suspend fun updateDescription(
        userKeywordId: Long,
        description: String,
    )

    @Query("DELETE FROM MyKeywordDetailEntity WHERE userKeywordId = :userKeywordId")
    suspend fun deleteById(userKeywordId: Long)

    @Query("DELETE FROM MyKeywordDetailEntity")
    suspend fun deleteAll()
}

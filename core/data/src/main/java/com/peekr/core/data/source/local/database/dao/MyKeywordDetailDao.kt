package com.peekr.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
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
    suspend fun upsertAll(keywordDetails: List<MyKeywordDetailEntity>)

    @Upsert
    suspend fun upsert(keywordDetail: MyKeywordDetailEntity)

    @Query("DELETE FROM MyKeywordDetailEntity")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(keywordDetail: MyKeywordDetailEntity)
}

package com.peekr.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.peekr.core.data.source.local.database.entity.MyKeywordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyKeywordDao {
    @Query("SELECT * FROM MyKeywordEntity")
    fun getAll(): Flow<List<MyKeywordEntity>>

    @Query("SELECT * FROM MyKeywordEntity WHERE userKeywordId = :userKeywordId")
    fun getById(userKeywordId: Long): Flow<MyKeywordEntity?>

    @Upsert
    suspend fun upsertAll(myKeywordDetails: List<MyKeywordEntity>)

    @Upsert
    suspend fun upsert(myKeywordDetail: MyKeywordEntity)

    @Query("UPDATE MyKeywordEntity SET description = :description WHERE userKeywordId = :userKeywordId")
    suspend fun updateDescription(
        userKeywordId: Long,
        description: String,
    )

    @Query("DELETE FROM MyKeywordEntity WHERE userKeywordId = :userKeywordId")
    suspend fun deleteById(userKeywordId: Long)

    @Query("DELETE FROM MyKeywordEntity")
    suspend fun deleteAll()
}

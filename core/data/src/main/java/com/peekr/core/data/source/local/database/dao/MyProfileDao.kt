package com.peekr.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.peekr.core.data.source.local.database.entity.MyProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyProfileDao {
    @Query("SELECT * FROM MyProfileEntity where userId = :userId")
    fun getByUserId(userId: Long): Flow<MyProfileEntity>

    @Upsert
    suspend fun upsert(myProfileEntity: MyProfileEntity)

    @Query("DELETE FROM MyProfileEntity")
    suspend fun deleteAll()
}

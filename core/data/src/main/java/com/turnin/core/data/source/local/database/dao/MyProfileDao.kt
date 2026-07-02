package com.turnin.core.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.turnin.core.data.source.local.database.entity.MyProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MyProfileDao {
    @Query("SELECT * FROM MyProfileEntity where userId = :userId")
    fun getByUserId(userId: Long): Flow<MyProfileEntity?>

    @Upsert
    suspend fun upsert(myProfileEntity: MyProfileEntity)

    @Query(
        """
        UPDATE MyProfileEntity
        SET displayId = :displayId,
            name = :name,
            profileImageUrl = :profileImageUrl,
            introduce = :introduce
        WHERE userId = :userId
    """,
    )
    suspend fun updateProfile(
        userId: Long,
        displayId: String,
        name: String,
        profileImageUrl: String?,
        introduce: String,
    )

    @Query("UPDATE MyProfileEntity SET introduce = :introduce WHERE userId = :userId")
    suspend fun updateIntroduce(
        userId: Long,
        introduce: String,
    )

    @Query("DELETE FROM MyProfileEntity")
    suspend fun deleteAll()
}

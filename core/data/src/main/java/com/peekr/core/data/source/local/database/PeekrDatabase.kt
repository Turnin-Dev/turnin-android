package com.peekr.core.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.peekr.core.data.source.local.database.dao.KeywordDetailDao
import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.peekr.core.data.source.local.database.entity.KeywordDetailEntity
import com.peekr.core.data.source.local.database.entity.MyProfileEntity

@Database(
    version = 1,
    entities = [MyProfileEntity::class, KeywordDetailEntity::class],
)
abstract class PeekrDatabase : RoomDatabase() {
    abstract fun myProfileDao(): MyProfileDao

    abstract fun keywordDetailDao(): KeywordDetailDao
}

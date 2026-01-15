package com.peekr.core.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.peekr.core.data.source.local.database.dao.KeywordDetailDao
import com.peekr.core.data.source.local.database.dao.MyKeywordDetailDao
import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.peekr.core.data.source.local.database.entity.KeywordDetailEntity
import com.peekr.core.data.source.local.database.entity.MyKeywordDetailEntity
import com.peekr.core.data.source.local.database.entity.MyProfileEntity

@Database(
    version = 1,
    entities = [MyProfileEntity::class, MyKeywordDetailEntity::class, KeywordDetailEntity::class],
)
abstract class PeekrDatabase : RoomDatabase() {
    abstract fun myProfileDao(): MyProfileDao

    abstract fun myKeywordDetailDao(): MyKeywordDetailDao

    abstract fun keywordDetailDao(): KeywordDetailDao
}

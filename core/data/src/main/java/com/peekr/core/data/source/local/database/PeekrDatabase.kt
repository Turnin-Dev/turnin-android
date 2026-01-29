package com.peekr.core.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.peekr.core.data.source.local.database.dao.FeedDao
import com.peekr.core.data.source.local.database.dao.FeedRemoteKeyDao
import com.peekr.core.data.source.local.database.dao.MyKeywordDao
import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.peekr.core.data.source.local.database.entity.FeedEntity
import com.peekr.core.data.source.local.database.entity.FeedRemoteKeyEntity
import com.peekr.core.data.source.local.database.entity.MyKeywordEntity
import com.peekr.core.data.source.local.database.entity.MyProfileEntity

@Database(
    version = 1,
    entities = [
        MyProfileEntity::class,
        MyKeywordEntity::class,
        FeedEntity::class,
        FeedRemoteKeyEntity::class,
    ],
)
abstract class PeekrDatabase : RoomDatabase() {
    abstract fun myProfileDao(): MyProfileDao

    abstract fun myKeywordDao(): MyKeywordDao

    abstract fun feedDao(): FeedDao

    abstract fun feedRemoteKeyDao(): FeedRemoteKeyDao
}

package com.turnin.core.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.turnin.core.data.source.local.database.dao.FeedDao
import com.turnin.core.data.source.local.database.dao.FeedRemoteKeyDao
import com.turnin.core.data.source.local.database.dao.MyKeywordDao
import com.turnin.core.data.source.local.database.dao.MyProfileDao
import com.turnin.core.data.source.local.database.entity.FeedEntity
import com.turnin.core.data.source.local.database.entity.FeedRemoteKeyEntity
import com.turnin.core.data.source.local.database.entity.MyKeywordEntity
import com.turnin.core.data.source.local.database.entity.MyProfileEntity

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

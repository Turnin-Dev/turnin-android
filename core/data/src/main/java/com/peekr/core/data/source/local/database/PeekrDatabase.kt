package com.peekr.core.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.peekr.core.data.source.local.database.dao.MyKeywordDao
import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.peekr.core.data.source.local.database.entity.MyKeywordEntity
import com.peekr.core.data.source.local.database.entity.MyProfileEntity

@Database(
    version = 1,
    entities = [MyProfileEntity::class, MyKeywordEntity::class],
)
abstract class PeekrDatabase : RoomDatabase() {
    abstract fun myProfileDao(): MyProfileDao

    abstract fun myKeywordDao(): MyKeywordDao
}

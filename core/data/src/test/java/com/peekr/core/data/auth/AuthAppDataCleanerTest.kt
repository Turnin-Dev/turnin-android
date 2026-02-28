package com.peekr.core.data.auth

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.peekr.core.data.source.local.database.PeekrDatabase
import com.peekr.core.data.source.local.database.entity.FeedEntity
import com.peekr.core.data.source.local.database.entity.FeedRemoteKeyEntity
import com.peekr.core.data.source.local.database.entity.MyKeywordEntity
import com.peekr.core.data.source.local.database.entity.MyProfileEntity
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class)
class AuthAppDataCleanerTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: PeekrDatabase

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var appDataCleaner: AuthAppDataCleaner

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun `clearAll 하면 모든 로컬 데이터(DB, DataStore)가 삭제되어야 한다`() = runTest {
        // 1. 더미 데이터 삽입
        withContext(Dispatchers.IO) {
            insertDummyData()
        }
        // DataStore는 내부적으로 IO 처리
        dataStoreManager.saveStringData(DataStoreKey.Auth.AccessToken, "aaa.bbb.ccc")

        // 2. 데이터가 실제로 삽입됐는지 검증 (테스트 신뢰성 확보)
        val tableNames = withContext(Dispatchers.IO) { getCustomTableNames() }
        val emptyTablesBeforeClear = withContext(Dispatchers.IO) {
            tableNames.filter { getTableRowCount(it) == 0 }
        }
        assert(emptyTablesBeforeClear.isEmpty()) {
            "더미 데이터가 삽입되지 않은 테이블 발견: $emptyTablesBeforeClear"
        }
        assert(dataStoreManager.getStringData(DataStoreKey.Auth.AccessToken).first() != null) {
            "DataStore 더미 데이터가 삽입되지 않았습니다."
        }

        // 3. 데이터 삭제 실행
        appDataCleaner.clearAll()

        // 4. DB 테이블 검증
        val failedTables = withContext(Dispatchers.IO) {
            tableNames.filter { getTableRowCount(it) > 0 }
        }
        assert(failedTables.isEmpty()) {
            "삭제되지 않은 DB 테이블 발견: $failedTables"
        }

        // 5. DataStore 검증
        assert(dataStoreManager.getStringData(DataStoreKey.Auth.AccessToken).first() == null) {
            "DataStore가 삭제되지 않았습니다."
        }
    }

    /**
     * 각 테이블에 더미 데이터를 삽입한다.
     * 새로운 엔티티가 추가되면 여기에도 추가해야 한다.
     */
    private suspend fun insertDummyData() {
        database.feedRemoteKeyDao().upsert(
            FeedRemoteKeyEntity(
                cursorScore = 1.0,
                cursorCreatedAt = 1L,
                cursorUserKeywordId = 1L,
            ),
        )
        database.feedDao().upsertAll(
            listOf(
                FeedEntity(
                    userKeywordId = 1L,
                    userId = 1L,
                    userName = "name",
                    profileImageUrl = "",
                    keywordId = 1L,
                    keyword = "",
                    description = "",
                    createdAt = 1L,
                    score = 1.0,
                    similarity = 1.0,
                ),
            ),
        )
        database.myProfileDao().upsert(
            MyProfileEntity(
                userId = 1L,
                displayId = "did",
                name = "name",
                profileImageUrl = "",
                introduce = "hello",
                lastLoginAt = 1000L,
                friendsCount = 10,
                active = true,
            ),
        )
        database.myKeywordDao().upsert(
            MyKeywordEntity(
                userKeywordId = 1L,
                keywordId = 1L,
                keywordName = "name",
                description = "desc",
                createdAt = 1000L,
                updatedAt = 1000L,
            ),
        )
    }

    private fun getCustomTableNames(): List<String> {
        val tableNames = mutableListOf<String>()
        val query = "SELECT name FROM sqlite_master WHERE type='table' " +
            "AND name NOT LIKE 'android_metadata' " +
            "AND name NOT LIKE 'sqlite_sequence' " +
            "AND name NOT LIKE 'room_master_table'"

        val cursor = database.openHelper.readableDatabase.query(query)
        while (cursor.moveToNext()) {
            tableNames.add(cursor.getString(0))
        }
        cursor.close()
        return tableNames
    }

    private fun getTableRowCount(tableName: String): Int {
        val cursor = database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM $tableName")
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }
}

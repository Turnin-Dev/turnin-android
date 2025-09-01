package com.peekr.data.common.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.rules.TemporaryFolder

/** 테스트용 DataStore */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDataStoreFactory(private val temporaryFolder: TemporaryFolder) {
    private lateinit var dataStore: DataStore<Preferences>

    /** 테스트용 DataStore를 생성한다. */
    fun create(): DataStore<Preferences> {
        dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(UnconfinedTestDispatcher()),
            produceFile = {
                // 파일 이름의 확장자는 반드시 'preferences_pb' 이어야 함
                temporaryFolder.newFile("test.preferences_pb")
            },
        )
        return dataStore
    }
}

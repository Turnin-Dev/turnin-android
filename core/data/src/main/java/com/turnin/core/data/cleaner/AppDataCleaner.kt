package com.turnin.core.data.cleaner

import com.turnin.core.common.coroutine.IO
import com.turnin.core.data.source.local.datastore.DataStoreManager
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * 로그아웃 또는 계정 탈퇴 시 앱 내 데이터 정리를 수행하는 클래스.
 */
class AppDataCleaner @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val clearables: Set<@JvmSuppressWildcards Clearable>,
    @IO private val ioDispatcher: CoroutineDispatcher,
) {
    /**
     * 앱 내 모든 데이터를 삭제한다.
     *
     * **삭제 범위**
     * * 로컬 DB (Room DB)
     * * 메모리 캐시, 일반 캐시
     * * DataStore
     */
    suspend fun clearAll() = withContext(ioDispatcher) {
        // 1. 일반 데이터 (DB, 캐시) 삭제
        clearables.forEach { it.clear() }

        // 2. DataStore 삭제
        dataStoreManager.clearAll()
    }
}

package com.turnin.core.data.source.local.memory

import androidx.collection.LruCache
import com.turnin.core.common.logger.AppLogger
import com.turnin.core.data.BuildConfig
import kotlin.time.Duration

private data class CacheEntry<V>(
    val value: V,
    val timestamp: Long = System.currentTimeMillis(),
) {
    fun isExpired(ttl: Duration): Boolean =
        System.currentTimeMillis() - timestamp > ttl.inWholeMilliseconds
}

/**
 * LRU 캐시를 사용하는 메모리 캐시
 *
 * @property maxSize 캐시 최대 사이즈
 * @property ttl 캐시 만료 시간 (null이면 무제한)
 * @property name 캐시 이름 (디버깅용)
 * @property memoryThresholdRatio 메모리 임계값 비율 (해당 비율만큼의 메모리가 부족하면 캐시를 비운다)
 */
class LruMemoryCache<K : Any, V : Any>(
    private val maxSize: Int,
    private val ttl: Duration?,
    private val name: String,
    private val memoryThresholdRatio: Float = 0.1f,
) : MemoryCache<K, V> {
    private val tag = this::class.java.simpleName

    private val cache = LruCache<K, CacheEntry<V>>(maxSize)

    override fun get(key: K): V? {
        val entry = cache[key] ?: return null

        // ttl 체크
        if (ttl != null && entry.isExpired(ttl)) {
            cache.remove(key)
            return null
        }

        return entry.value
    }

    override fun set(key: K, value: V) {
        if (isMemoryLow()) {
            if (BuildConfig.DEBUG) {
                AppLogger.w(tag, "Memory is low. Evicting cache entries.")
            }
            clear()
        }

        cache.put(key, CacheEntry(value))

        if (BuildConfig.DEBUG) {
            logMemoryStatus()
        }
    }

    override fun remove(key: K): V? = cache.remove(key)?.value

    override fun clear() {
        cache.evictAll()
    }

    override fun size(): Int = cache.size()

    /**
     * 메모리 부족 여부 확인
     * 사용 가능한 메모리가 임계값 미만이면 true를 반환한다
     */
    private fun isMemoryLow(): Boolean {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val availableMemory = maxOf(0L, maxMemory - usedMemory)

        // 비율에 따른 임계값 계산 (256MB 기기라면 기본값이 10%이므로 약 25MB)
        val thresholdMemory = (maxMemory * memoryThresholdRatio).toLong()

        return availableMemory < thresholdMemory
    }

    /**
     * 현재 메모리 및 캐시 상태 로깅 (디버깅용)
     */
    private fun logMemoryStatus() {
        val runtime = Runtime.getRuntime()
        val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMb = runtime.maxMemory() / 1024 / 1024
        val availableMb = (maxMb - usedMb)

        AppLogger.d(
            tag,
            "$name: ${cache.size()}/$maxSize items | " +
                "Memory: ${usedMb}MB/${maxMb}MB (Available: ${availableMb}MB)",
        )
    }
}

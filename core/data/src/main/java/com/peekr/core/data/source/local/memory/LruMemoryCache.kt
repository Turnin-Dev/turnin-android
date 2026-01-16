package com.peekr.core.data.source.local.memory

import androidx.collection.LruCache
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
 * @property ttl 캐시 만료 시간
 */
class LruMemoryCache<K : Any, V : Any>(
    private val maxSize: Int,
    private val ttl: Duration?,
) : MemoryCache<K, V> {
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
        cache.put(key, CacheEntry(value))
    }

    override fun remove(key: K): V? = cache.remove(key)?.value

    override fun clear() {
        cache.evictAll()
    }

    override fun size(): Int = cache.size()
}

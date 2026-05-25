package com.turnin.core.data

import com.turnin.core.data.source.local.memory.MemoryCache

class FakeMemoryCache<K : Any, V : Any> : MemoryCache<K, V> {
    private val cache = mutableMapOf<K, V>()

    override fun get(key: K): V? = cache[key]

    override fun set(key: K, value: V) {
        cache[key] = value
    }

    override fun remove(key: K): V? = cache.remove(key)

    override fun clear() = cache.clear()

    override fun size(): Int = cache.size
}

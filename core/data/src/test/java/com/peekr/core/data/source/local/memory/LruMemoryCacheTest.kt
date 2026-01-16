package com.peekr.core.data.source.local.memory

import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LruMemoryCacheTest {
    @Test
    fun `캐시에 값을 저장하고 조회할 수 있다`() = runTest {
        // given
        val cache = LruMemoryCache<String, String>(maxSize = 10, ttl = null)

        // when
        cache["key1"] = "value1"

        // then
        assertEquals("value1", cache["key1"])
    }

    @Test
    fun `존재하지 않는 키 조회 시 null을 반환한다`() = runTest {
        // given
        val cache = LruMemoryCache<String, String>(maxSize = 10, ttl = null)

        // when
        val result = cache["key1"]

        // then
        assertNull(result)
    }

    @Test
    fun `TTL이 만료된 데이터는 null을 반환한다`() = runTest {
        // given
        val cache = LruMemoryCache<String, String>(
            maxSize = 10,
            ttl = 100.milliseconds,
        )
        cache["key1"] = "value1"

        // when
        Thread.sleep(150)
        val result = cache["key1"]

        // then
        assertNull(result)
    }

    @Test
    fun `TTL 옵션이 null이면 데이터가 만료되지 않는다`() = runTest {
        // given
        val cache = LruMemoryCache<String, String>(maxSize = 10, ttl = null)
        cache["key1"] = "value1"

        // when
        Thread.sleep(150)
        val result = cache["key1"]

        // then
        assertEquals("value1", result)
    }

    @Test
    fun `remove 호출 시 해당 키의 값을 반환하고 삭제한다`() = runTest {
        // given
        val cache = LruMemoryCache<String, String>(maxSize = 10, ttl = null)
        cache["key1"] = "value1"

        // when
        val removed = cache.remove("key1")

        // then
        assertEquals("value1", removed)
        assertNull(cache["key1"])
    }

    @Test
    fun `clear 호출 시 모든 캐시가 삭제된다`() = runTest {
        // given
        val cache = LruMemoryCache<String, String>(maxSize = 10, ttl = null)
        cache["key1"] = "value1"
        cache["key2"] = "value2"
        cache["key3"] = "value3"

        // when
        cache.clear()

        // then
        assertEquals(0, cache.size())
        assertNull(cache["key1"])
        assertNull(cache["key2"])
        assertNull(cache["key3"])
    }

    @Test
    fun `maxSize를 초과하면 가장 오래된 항목이 제거된다`() = runTest {
        // given
        val cache = LruMemoryCache<String, String>(maxSize = 3, ttl = null)

        // when: key4 저장 시 maxSize를 초과
        cache["key1"] = "value1"
        cache["key2"] = "value2"
        cache["key3"] = "value3"
        cache["key4"] = "value4"

        // then: 가장 오래된 항목인 key1이 제거됨
        assertNull(cache["key1"])
        assertEquals("value2", cache["key2"])
        assertEquals("value3", cache["key3"])
        assertEquals("value4", cache["key4"])
        assertEquals(3, cache.size())
    }

    @Test
    fun `같은 키에 새 값을 저장하면 기존 값을 덮어쓴다`() = runTest {
        // given
        val cache = LruMemoryCache<String, String>(maxSize = 10, ttl = null)
        cache["key1"] = "oldValue"

        // when
        cache["key1"] = "newValue"

        // then: 새 값으로 덮어 씌워지고 캐시 크기는 그대로여야 한다.
        assertEquals("newValue", cache["key1"])
        assertEquals(1, cache.size())
    }
}

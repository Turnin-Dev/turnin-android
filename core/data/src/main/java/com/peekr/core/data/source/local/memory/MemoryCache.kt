package com.peekr.core.data.source.local.memory

/**
 * 메모리 캐시 인터페이스
 */
interface MemoryCache<K : Any, V : Any> {
    /**
     * 캐시 조회
     *
     * @return 조회된 값이 있으면 [V], 없으면 `null`을 반환한다.
     */
    operator fun get(key: K): V?

    /** 캐시 저장 */
    operator fun set(key: K, value: V)

    /**
     * 캐시 삭제
     *
     * @return 삭제 성공하면 [V], 실패하면 `null`을 반환한다.
     */
    fun remove(key: K): V?

    /** 캐시 전부 삭제 */
    fun clear()

    /** 캐시 사이즈 반환 */
    fun size(): Int
}

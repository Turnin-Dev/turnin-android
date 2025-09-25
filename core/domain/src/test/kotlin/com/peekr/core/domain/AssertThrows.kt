package com.peekr.core.domain

import org.junit.Assert.fail

inline fun <reified T : Throwable> assertThrows(noinline block: () -> Unit): T {
    try {
        block()
    } catch (e: Throwable) {
        if (e is T) return e
        throw AssertionError("Expected ${T::class}, but was ${e::class}", e)
    }
    fail("Expected ${T::class} to be thrown, but nothing was thrown.")
    throw IllegalStateException("unreachable")
}

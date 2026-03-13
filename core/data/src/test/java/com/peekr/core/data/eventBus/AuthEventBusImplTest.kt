package com.peekr.core.data.eventBus

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthEventBusImplTest {
    private lateinit var authEventBus: AuthEventBusImpl

    @Before
    fun setUp() {
        authEventBus = AuthEventBusImpl()
    }

    @Test
    fun `단일 구독자가 로그아웃 이벤트를 수신한다`() = runTest {
        val received = mutableListOf<Unit>()

        val job = launch {
            authEventBus.logoutEvent.collect { received.add(it) }
        }
        advanceUntilIdle()

        authEventBus.emitLogout()
        advanceUntilIdle()

        assertEquals(1, received.size)
        job.cancel()
    }

    @Test
    fun `구독자가 없어도 이벤트 발행이 즉시 완료된다`() = runTest {
        val job = launch {
            authEventBus.emitLogout()
        }
        advanceUntilIdle()

        assertTrue(job.isCompleted)
    }

    @Test
    fun `연속으로 여러 번 발행해도 구독자는 최소 1번 수신한다`() = runTest {
        val received = mutableListOf<Unit>()

        val collectJob = launch {
            authEventBus.logoutEvent.collect { received.add(it) }
        }
        advanceUntilIdle()

        repeat(5) { authEventBus.emitLogout() }
        advanceUntilIdle()

        assertTrue(received.isNotEmpty())
        collectJob.cancel()
    }

    @Test
    fun `이벤트 발행 이후 구독한 구독자도 이벤트를 수신한다`() = runTest {
        authEventBus.emitLogout()
        advanceUntilIdle()

        val received = mutableListOf<Unit>()
        val job = launch {
            authEventBus.logoutEvent.collect { received.add(it) }
        }
        advanceUntilIdle()

        assertEquals(1, received.size)
        job.cancel()
    }
}

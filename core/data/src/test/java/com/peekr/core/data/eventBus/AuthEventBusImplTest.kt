package com.peekr.core.data.eventBus

import java.util.Collections
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun `다수의 구독자가 로그아웃 이벤트를 동시에 수신한다`() = runTest {
        val received1 = mutableListOf<Unit>()
        val received2 = mutableListOf<Unit>()
        val received3 = mutableListOf<Unit>()

        val job1 = launch { authEventBus.logoutEvent.collect { received1.add(it) } }
        val job2 = launch { authEventBus.logoutEvent.collect { received2.add(it) } }
        val job3 = launch { authEventBus.logoutEvent.collect { received3.add(it) } }
        advanceUntilIdle()

        authEventBus.emitLogout()
        advanceUntilIdle()

        assertEquals(1, received1.size)
        assertEquals(1, received2.size)
        assertEquals(1, received3.size)

        job1.cancel()
        job2.cancel()
        job3.cancel()
    }

    @Test
    fun `이벤트 발행 이후 구독한 구독자는 이벤트를 수신하지 못한다`() = runTest {
        authEventBus.emitLogout()
        advanceUntilIdle()

        val received = mutableListOf<Unit>()
        val job = launch {
            authEventBus.logoutEvent.collect { received.add(it) }
        }
        advanceUntilIdle()

        assertEquals(0, received.size)
        job.cancel()
    }

    @Test
    fun `구독자가 없어도 이벤트 발행이 즉시 완료된다`() = runTest {
        // extraBufferCapacity = 1 이므로 suspend 없이 완료되어야 함
        val job = launch {
            authEventBus.emitLogout()
        }
        advanceUntilIdle()

        assertTrue(job.isCompleted)
    }

    @Test
    fun `동시에 여러 코루틴에서 이벤트를 발행해도 정상 동작한다`() = runTest {
        val received = Collections.synchronizedList(mutableListOf<Unit>())

        val collectJob = launch {
            authEventBus.logoutEvent.collect { received.add(it) }
        }

        val emitJobs = (1..5).map {
            launch { authEventBus.emitLogout() }
        }

        emitJobs.joinAll()
        advanceUntilIdle()

        assertTrue(received.isNotEmpty())
        collectJob.cancel()
    }

    @Test
    fun `구독자가 취소되어도 다른 구독자는 정상적으로 이벤트를 수신한다`() = runTest {
        val received1 = mutableListOf<Unit>()
        val received2 = mutableListOf<Unit>()

        val job1 = launch { authEventBus.logoutEvent.collect { received1.add(it) } }
        val job2 = launch { authEventBus.logoutEvent.collect { received2.add(it) } }

        // job1 취소
        job1.cancel()
        advanceUntilIdle()

        authEventBus.emitLogout()
        advanceUntilIdle()

        assertEquals(0, received1.size)
        assertEquals(1, received2.size)
        job2.cancel()
    }

    @Test
    fun `logoutEvent는 SharedFlow이며 외부에서 MutableSharedFlow로 캐스팅할 수 없다`() {
        val logoutEvent = authEventBus.logoutEvent

        assertFalse(logoutEvent is MutableSharedFlow<Unit>)
    }
}

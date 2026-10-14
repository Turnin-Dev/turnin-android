package com.turnin.core.domain.coroutine

import com.turnin.core.domain.common.coroutine.runCatchingSafe
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RunCatchingSafeTest {
    @Test
    fun `runCatchingSafe - 일반 예외는 삼킨다`() {
        val result = runCatchingSafe { throw IllegalStateException("error") }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `runCatchingSafe - CancellationException은 재전파한다`() {
        assertFailsWith<CancellationException> {
            runCatchingSafe { throw CancellationException("cancelled") }
        }
    }
}

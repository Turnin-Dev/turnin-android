package com.peekr.core.domain.coroutine

import com.peekr.core.domain.assertThrows
import com.peekr.core.domain.common.coroutine.runCatchingSafe
import kotlin.coroutines.cancellation.CancellationException
import org.junit.Assert.assertTrue
import org.junit.Test

class RunCatchingSafeTest {
    @Test
    fun `runCatchingSafe - 일반 예외는 삼킨다`() {
        val result = runCatchingSafe { throw IllegalStateException("error") }

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `runCatchingSafe - CancellationException은 재전파한다`() {
        assertThrows<CancellationException> {
            runCatchingSafe { throw CancellationException("cancelled") }
        }
    }
}

package com.turnin.core.data

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

/**
 * 기본 로그를 모킹한다.
 *
 * **만약, [ServerTestRule]을 사용중이라면 이를 사용할 필요가 없다.**
 */
object MockLog {
    fun mock() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.v(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.i(any(), any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.w(any(), any<String>(), any()) } returns 0
        every { Log.w(any(), any<Throwable>()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.println(any(), any(), any()) } returns 0
        every { Log.isLoggable(any(), any()) } returns true
        every { Log.getStackTraceString(any()) } returns ""
    }

    fun cleanUp() {
        unmockkStatic(Log::class)
    }
}

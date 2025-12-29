package com.peekr.core.data

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic

object MockLog {
    fun mock() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.v(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.d(any(), any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.isLoggable(any<String>(), any()) } returns true
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.w(any(), any<String>(), any()) } returns 0
    }

    fun cleanUp() {
        unmockkStatic(Log::class)
    }
}

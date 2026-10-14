package com.turnin.core.domain.common.coroutine

import kotlin.coroutines.cancellation.CancellationException

inline fun <T> runCatchingSafe(block: () -> T): Result<T> =
    runCatching(block).also { result ->
        result.exceptionOrNull()
            ?.let { if (it is CancellationException) throw it }
    }

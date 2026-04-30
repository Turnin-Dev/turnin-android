package com.turnin.core.designsystem.util.click

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 클릭 후 특정 시간 이후 마지막에 클릭된 요청을 처리하는 Debounce 클릭 이벤트 프로세서
 *
 * Debounce 는 빠른 클릭 연속 입력이 있으면 마지막 클릭만 유효하게 처리하는 방식입니다.
 */
class DebounceClickEventProcessor(
    private val coroutineScope: CoroutineScope,
    private val debounceTimeMs: Long = DEBOUNCE_TIME_MS,
) : ClickEventProcessor {
    private var debounceJob: Job? = null

    private fun debounce(onClick: () -> Unit) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(debounceTimeMs)
            onClick()
        }
    }

    override fun processEvent(event: () -> Unit) {
        debounce(onClick = event)
    }

    companion object {
        const val DEBOUNCE_TIME_MS = 200L
    }
}

fun ClickEventProcessor.Companion.getDebounce(
    coroutineScope: CoroutineScope,
    debounceTimeMs: Long,
): ClickEventProcessor =
    DebounceClickEventProcessor(coroutineScope, debounceTimeMs)

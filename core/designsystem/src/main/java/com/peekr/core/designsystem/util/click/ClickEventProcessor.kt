package com.peekr.core.designsystem.util.click

/** 클릭 이벤트 프로세서 */
interface ClickEventProcessor {
    fun processEvent(event: () -> Unit)

    companion object
}

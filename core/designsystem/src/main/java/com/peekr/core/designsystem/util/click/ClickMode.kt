package com.peekr.core.designsystem.util.click

/** 클릭 모드 */
enum class ClickMode {
    /** 쓰로틀 방식으로, 연속적인 클릭이 있을 때 일정 시간마다 한 번씩만 클릭이 적용된다. */
    Throttle,

    /** 디바운스 방식으로, 연속적인 클릭이 일정 시간 동안 중단된 후에만 클릭이 적용되어 마지막 클릭만 적용된다. */
    Debounce,
}

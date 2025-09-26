package com.peekr.core.designsystem.util.click

import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role

/**
 * **`클릭 모드`** 에 따라 추가 기능이 있고 Ripple 효과가 비활성화된 Clickable
 *
 * **`클릭 모드`**
 * - [throttleClickableWithoutRipple]
 * - [debounceClickableWithoutRipple]
 *
 * @param clickMode 클릭 모드
 * @param delayTimeMs [ClickMode]별 딜레이 타임
 * @param enabled 기존 파라미터와 동일
 * @param onClickLabel 기존 파라미터와 동일
 * @param role 기존 파라미터와 동일
 * @param onClick 기존 파라미터와 동일
 *
 * @see throttleClickableWithoutRipple
 * @see debounceClickableWithoutRipple
 */
fun Modifier.clickableSingleWithoutRipple(
    clickMode: ClickMode = ClickMode.Throttle,
    delayTimeMs: Long? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = this.then(
    when (clickMode) {
        ClickMode.Throttle -> {
            val time = delayTimeMs ?: ThrottleClickEventProcessor.THROTTLE_TIME_MS
            Modifier.throttleClickableWithoutRipple(
                throttleTime = time,
                enabled = enabled,
                onClickLabel = onClickLabel,
                role = role,
                onClick = onClick,
            )
        }

        ClickMode.Debounce -> {
            val time = delayTimeMs ?: DebounceClickEventProcessor.DEBOUNCE_TIME_MS
            Modifier.debounceClickableWithoutRipple(
                debounceClick = time,
                enabled = enabled,
                onClickLabel = onClickLabel,
                role = role,
                onClick = onClick,
            )
        }
    },
)

/**
 * Throttle 기능을 포함하고 Ripple 효과가 비활성화된 Clickable
 *
 * Throttle 기능은 사용자가 버튼을 빠르게 여러 번 눌러도 지정된 시간 동안 한 번만 동작 하도록 제한합니다.
 *
 * @param throttleTime 클릭 후 다음 클릭을 허용하는 시간 (throttle time)
 * @param enabled 기존 파라미터와 동일
 * @param onClickLabel 기존 파라미터와 동일
 * @param role 기존 파라미터와 동일
 * @param onClick 기존 파라미터와 동일
 */
private fun Modifier.throttleClickableWithoutRipple(
    throttleTime: Long = ThrottleClickEventProcessor.THROTTLE_TIME_MS,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val throttleClickEventProcessor = remember { ClickEventProcessor.getThrottle(throttleTime) }

    this.then(
        Modifier.clickable(
            interactionSource = null,
            indication = null,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = { throttleClickEventProcessor.processEvent(onClick) },
        ),
    )
}

/**
 * Debounce 기능을 포함하고 Ripple 효과가 비활성화된 Clickable
 *
 * Debounce 기능은 빠른 클릭 연속 입력이 있으면 마지막 클릭만 유효하게 처리하는 방식입니다.
 *
 * @param debounceClick 클릭 후 마지막 클릭을 받기까지 허
 * @param enabled 기존 파라미터와 동일
 * @param onClickLabel 기존 파라미터와 동일
 * @param role 기존 파라미터와 동일
 * @param onClick 기존 파라미터와 동일
 */
private fun Modifier.debounceClickableWithoutRipple(
    debounceClick: Long = DebounceClickEventProcessor.DEBOUNCE_TIME_MS,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val debounceClickEventProcessor = remember {
        ClickEventProcessor.getDebounce(coroutineScope, debounceClick)
    }

    this.then(
        Modifier.clickable(
            interactionSource = null,
            indication = null,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = { debounceClickEventProcessor.processEvent(onClick) },
        ),
    )
}

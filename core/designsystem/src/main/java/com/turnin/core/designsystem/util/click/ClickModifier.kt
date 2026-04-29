package com.turnin.core.designsystem.util.click

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * **`클릭 모드`** 에 따라 추가 기능이 있는 Clickable
 *
 * **`클릭 모드`**
 * - [throttleClickable]
 * - [debounceClickable]
 *
 * @param clickMode 클릭 모드
 * @param delayTimeMs [ClickMode]별 딜레이 타임
 * @param interactionSource 기존 파라미터와 동일
 * @param indication 기존 파라미터와 동일
 * @param enabled 기존 파라미터와 동일
 * @param onLongClickLabel 기존 파라미터와 동일
 * @param onClickLabel 기존 파라미터와 동일
 * @param role 기존 파라미터와 동일
 * @param onLongClick 기존 파라미터와 동일
 * @param onClick 기존 파라미터와 동일
 *
 * @see throttleClickable
 * @see debounceClickable
 */
fun Modifier.clickableSingle(
    clickMode: ClickMode = ClickMode.Throttle,
    delayTimeMs: Long? = null,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onLongClickLabel: String? = null,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier = this.then(
    when (clickMode) {
        ClickMode.Throttle -> {
            val time = delayTimeMs ?: ThrottleClickEventProcessor.THROTTLE_TIME_MS
            Modifier.throttleClickable(
                throttleTime = time,
                interactionSource = interactionSource,
                indication = indication,
                enabled = enabled,
                onLongClickLabel = onLongClickLabel,
                onClickLabel = onClickLabel,
                role = role,
                onLongClick = onLongClick,
                onClick = onClick,
            )
        }

        ClickMode.Debounce -> {
            val time = delayTimeMs ?: DebounceClickEventProcessor.DEBOUNCE_TIME_MS
            Modifier.debounceClickable(
                debounceClick = time,
                interactionSource = interactionSource,
                indication = indication,
                enabled = enabled,
                onLongClickLabel = onLongClickLabel,
                onClickLabel = onClickLabel,
                role = role,
                onLongClick = onLongClick,
                onClick = onClick,
            )
        }
    },
)

/**
 * Throttle 기능을 포함한 Clickable
 *
 * Throttle 기능은 사용자가 버튼을 빠르게 여러 번 눌러도 지정된 시간 동안 한 번만 동작 하도록 제한합니다.
 *
 * @param throttleTime 클릭 후 다음 클릭을 허용하는 시간 (throttle time)
 * @param interactionSource 기존 파라미터와 동일
 * @param indication 기존 파라미터와 동일
 * @param enabled 기존 파라미터와 동일
 * @param onLongClickLabel 기존 파라미터와 동일
 * @param onClickLabel 기존 파라미터와 동일
 * @param role 기존 파라미터와 동일
 * @param onLongClick 기존 파라미터와 동일
 * @param onClick 기존 파라미터와 동일
 */
private fun Modifier.throttleClickable(
    throttleTime: Long = ThrottleClickEventProcessor.THROTTLE_TIME_MS,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onLongClickLabel: String? = null,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val throttleClickEventProcessor = remember { ClickEventProcessor.getThrottle(throttleTime) }

    this.then(
        Modifier.combinedClickable(
            interactionSource = interactionSource ?: remember { MutableInteractionSource() },
            indication = indication ?: ripple(),
            enabled = enabled,
            onLongClickLabel = onLongClickLabel,
            onClickLabel = onClickLabel,
            role = role,
            onLongClick = onLongClick,
            onClick = { throttleClickEventProcessor.processEvent(onClick) },
        ),
    )
}

/**
 * Debounce 기능을 포함한 Clickable
 *
 * Debounce 기능은 빠른 클릭 연속 입력이 있으면 마지막 클릭만 유효하게 처리하는 방식입니다.
 *
 * @param debounceClick 클릭 후 마지막 클릭을 받기까지 허
 * @param interactionSource 기존 파라미터와 동일
 * @param indication 기존 파라미터와 동일
 * @param enabled 기존 파라미터와 동일
 * @param onLongClickLabel 기존 파라미터와 동일
 * @param onClickLabel 기존 파라미터와 동일
 * @param role 기존 파라미터와 동일
 * @param onLongClick 기존 파라미터와 동일
 * @param onClick 기존 파라미터와 동일
 */
private fun Modifier.debounceClickable(
    debounceClick: Long = DebounceClickEventProcessor.DEBOUNCE_TIME_MS,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onLongClickLabel: String? = null,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val coroutineScope = rememberCoroutineScope()
    val debounceClickEventProcessor = remember {
        ClickEventProcessor.getDebounce(coroutineScope, debounceClick)
    }

    this.then(
        Modifier.combinedClickable(
            interactionSource = interactionSource ?: remember { MutableInteractionSource() },
            indication = indication ?: ripple(),
            enabled = enabled,
            onLongClickLabel = onLongClickLabel,
            onClickLabel = onClickLabel,
            role = role,
            onLongClick = onLongClick,
            onClick = { debounceClickEventProcessor.processEvent(onClick) },
        ),
    )
}

@Preview
@Composable
private fun ClickableSample() {
    var count by remember { mutableLongStateOf(0L) }

    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column {
            Text("$count", fontSize = 20.sp, color = Color.Black)
            Row {
                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .clickableSingle(ClickMode.Throttle) {
                            count++
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Throttle") }
                Box(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .clickableSingle(ClickMode.Debounce) {
                            count++
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) { Text("Debounce") }
            }
        }
    }
}

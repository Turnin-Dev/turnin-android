package com.peekr.presentation.keyword.common.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * [NodeState]에서 사용하는 노드 상태 값 홀더 클래스
 */
@Immutable
data class NodeStateHolder(
    /** 계속해서 변하는 노드 오프셋 X 값 */
    val offsetX: Float,
    /** 계속해서 변하는 노드 오프셋 Y 값 */
    val offsetY: Float,
    /** 노드의 가로 길이 px 값 */
    val widthPx: Float,
    /** 노드의 세로 길이 px 값 */
    val heightPx: Float,
)

/**
 * 키워드 노드 상태 클래스
 *
 * @param initialOffsetX 초기 오프셋 X 값
 * @param initialOffsetY 초기 오프셋 Y 값
 */
@Stable
class NodeState(
    initialOffsetX: Float = 0f,
    initialOffsetY: Float = 0f,
) {
    private var nodeStateHolder by mutableStateOf(
        NodeStateHolder(
            offsetX = initialOffsetX,
            offsetY = initialOffsetY,
            widthPx = 0f,
            heightPx = 0f,
        ),
    )

    /** 계속해서 변하는 노드 오프셋 X 값 */
    val offsetX: Float get() = nodeStateHolder.offsetX

    /** 계속해서 변하는 노드 오프셋 Y 값 */
    val offsetY: Float get() = nodeStateHolder.offsetY

    /** 노드의 가로 길이 px 값 */
    val widthPx: Float get() = nodeStateHolder.widthPx

    /** 노드의 세로 길이 px 값 */
    val heightPx: Float get() = nodeStateHolder.heightPx

    /**
     * 새로운 노드 오프셋으로 업데이트 한다.
     *
     * @param newOffsetX 새로운 노드 오프셋 X
     * @param newOffsetY 새로운 노드 오프셋 Y
     */
    fun updatePosition(newOffsetX: Float, newOffsetY: Float) {
        nodeStateHolder = nodeStateHolder.copy(offsetX = newOffsetX, offsetY = newOffsetY)
    }

    /**
     * 새로운 노드 사이즈로 업데이트 한다.
     *
     * @param newWidthPx 새로운 노드 가로 길이 (px)
     * @param newHeightPx 새로운 노드 세로 길이 (px)
     */
    fun updateSize(newWidthPx: Float, newHeightPx: Float) {
        nodeStateHolder = nodeStateHolder.copy(widthPx = newWidthPx, heightPx = newHeightPx)
    }

    companion object {
        /**
         * 커스텀 [rememberNodeState] Saver
         *
         * [screenWidth]와 [screenHeight]를 이용해서 현재 노드 오프셋과 중심 오프셋의 차이로 상대 좌표를 구한다.
         */
        fun createSaver(screenWidth: Float, screenHeight: Float) = Saver<NodeState, List<Float>>(
            save = { nodeState ->
                val centerX = screenWidth / 2f
                val centerY = screenHeight / 2f

                listOf(
                    nodeState.offsetX - centerX,
                    nodeState.offsetY - centerY,
                    nodeState.widthPx,
                    nodeState.heightPx,
                )
            },
            restore = { savedValues ->
                val centerX = screenWidth / 2f
                val centerY = screenHeight / 2f

                NodeState(
                    initialOffsetX = savedValues[0] + centerX,
                    initialOffsetY = savedValues[1] + centerY,
                ).apply {
                    updateSize(savedValues[2], savedValues[3])
                }
            },
        )
    }
}

@Composable
fun rememberNodeState(
    initialOffsetX: Float = 0f,
    initialOffsetY: Float = 0f,
): NodeState {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidth = with(density) { config.screenWidthDp.dp.toPx() }
    val screenHeight = with(density) { config.screenHeightDp.dp.toPx() }

    return rememberSaveable(
        screenWidth,
        screenHeight,
        saver = NodeState.createSaver(screenWidth, screenHeight),
        init = { NodeState(initialOffsetX, initialOffsetY) },
    )
}

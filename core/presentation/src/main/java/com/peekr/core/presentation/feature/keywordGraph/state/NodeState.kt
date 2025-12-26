package com.peekr.core.presentation.feature.keywordGraph.state

import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalWindowInfo

/**
 * 노드와 관련된 토큰 값들
 */
object NodeTokens {
    const val LABEL_OFFSET_ANIM = "node-offset-animation"
    val animation = spring<Float>(dampingRatio = 0.75f, stiffness = 400f)
}

/**
 * 키워드 노드 상태 클래스
 *
 * @param initialOffsetX 초기 오프셋 X 값
 * @param initialOffsetY 초기 오프셋 Y 값
 */
@Stable
class NodeState(
    val initialOffsetX: Float = 0f,
    val initialOffsetY: Float = 0f,
) {
    /** 계속해서 변하는 노드 오프셋 X 값 */
    var offsetX: Float by mutableFloatStateOf(initialOffsetX)

    /** 계속해서 변하는 노드 오프셋 Y 값 */
    var offsetY: Float by mutableFloatStateOf(initialOffsetY)

    /** 노드의 가로 길이 px 값 */
    var widthPx: Float by mutableFloatStateOf(0f)

    /** 노드의 세로 길이 px 값 */
    var heightPx: Float by mutableFloatStateOf(0f)

    /**
     * 새로운 노드 오프셋으로 업데이트 한다.
     *
     * @param newOffsetX 새로운 노드 오프셋 X
     * @param newOffsetY 새로운 노드 오프셋 Y
     */
    fun updatePosition(newOffsetX: Float, newOffsetY: Float) {
        offsetX = newOffsetX
        offsetY = newOffsetY
    }

    /**
     * 새로운 노드 사이즈로 업데이트 한다.
     *
     * @param newWidthPx 새로운 노드 가로 길이 (px)
     * @param newHeightPx 새로운 노드 세로 길이 (px)
     */
    fun updateSize(newWidthPx: Float, newHeightPx: Float) {
        widthPx = newWidthPx
        heightPx = newHeightPx
    }

    /**
     * 노드 오프셋 초기 값으로 리셋
     */
    fun resetPosition() {
        offsetX = initialOffsetX
        offsetY = initialOffsetY
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
                    initialOffsetX = centerX + savedValues[0],
                    initialOffsetY = centerY + savedValues[1],
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
    val containerSize = LocalWindowInfo.current.containerSize
    val screenWidth = containerSize.width.toFloat()
    val screenHeight = containerSize.height.toFloat()

    return rememberSaveable(
        saver = NodeState.createSaver(screenWidth, screenHeight),
        init = { NodeState(initialOffsetX, initialOffsetY) },
    )
}

package com.peekr.core.presentation.keyword.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.peekr.core.presentation.keyword.state.NodeState
import com.peekr.core.presentation.keyword.state.NodeTokens
import kotlin.math.roundToInt

/**
 * [KeywordNode]와 [KeywordEdge] 통합 버전이며, [NodeState]가 필요하다.
 *
 * @param modifier [Modifier]
 * @param nodeState 키워드 노드 상태 [NodeState]
 * @param label 키워드 이름
 * @param onNodeClick 키워드 노드 클릭 시
 */
@Composable
fun KeywordNodeEdge(
    modifier: Modifier = Modifier,
    nodeState: NodeState,
    label: String,
    onNodeClick: () -> Unit,
) {
    val animatedNodeOffsetX by animateFloatAsState(
        targetValue = nodeState.offsetX,
        animationSpec = NodeTokens.animation,
        label = NodeTokens.LABEL_OFFSET_ANIM,
    )
    val animatedNodeOffsetY by animateFloatAsState(
        targetValue = nodeState.offsetY,
        animationSpec = NodeTokens.animation,
        label = NodeTokens.LABEL_OFFSET_ANIM,
    )

    Box(modifier) {
        // 키워드 엣지(간선)
        KeywordEdge(
            targetX = animatedNodeOffsetX + nodeState.widthPx / 2,
            targetY = animatedNodeOffsetY + nodeState.heightPx / 2,
        )

        // 키워드 노드
        KeywordNode(
            modifier = Modifier
                .onSizeChanged { intSize ->
                    nodeState.updateSize(
                        newWidthPx = intSize.width.toFloat(),
                        newHeightPx = intSize.height.toFloat(),
                    )
                }.graphicsLayer {
                    translationX = animatedNodeOffsetX
                    translationY = animatedNodeOffsetY
                }.pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val sensitivity = 1.0f
                        nodeState.updatePosition(
                            newOffsetX = nodeState.offsetX + (dragAmount.x * sensitivity).roundToInt(),
                            newOffsetY = nodeState.offsetY + (dragAmount.y * sensitivity).roundToInt(),
                        )
                    }
                },
            label = label,
            onClick = onNodeClick,
        )
    }
}

package com.peekr.core.presentation.feature.keyword.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.peekr.core.presentation.feature.keyword.NodeOffsetXType
import com.peekr.core.presentation.feature.keyword.NodeOffsetYType
import com.peekr.core.presentation.feature.keyword.state.NodeTokens
import com.peekr.core.presentation.feature.keyword.state.rememberNodeState
import kotlin.math.roundToInt

/**
 * [KeywordNode]와 [KeywordEdge] 통합 버전
 *
 * @param modifier [Modifier]
 * @param initialOffsetX 초기 키워드 오프셋 X
 * @param initialOffsetY 초기 키워드 오프셋 Y
 * @param label 키워드 이름
 * @param nodeReset 키워드 노드 리셋 여부
 * @param onNodeClick 키워드 노드 클릭 시
 * @param onNodeLongClick 키워드 노드 길게 클릭 시
 * @param onNodeChanged 키워드 노드 위치 변경 시
 */
@Composable
fun KeywordNodeEdge(
    modifier: Modifier = Modifier,
    initialOffsetX: Float,
    initialOffsetY: Float,
    label: String,
    nodeReset: Boolean,
    freeGesture: Boolean,
    onNodeClick: () -> Unit,
    onNodeLongClick: () -> Unit,
    onNodeChanged: (NodeOffsetXType, NodeOffsetYType) -> Unit,
) {
    val nodeState = rememberNodeState(initialOffsetX, initialOffsetY)
    var nodeDragging by rememberSaveable { mutableStateOf(false) }

    var containerWidthPx by rememberSaveable { mutableFloatStateOf(0f) }
    var containerHeightPx by rememberSaveable { mutableFloatStateOf(0f) }

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

    LaunchedEffect(nodeDragging) {
        // 드래그 하지 않은 상태에서 기존 위치에서 변화가 일어났다면 콜백 수행
        if (!nodeDragging && (nodeState.offsetX != initialOffsetX || nodeState.offsetY != initialOffsetY)) {
            onNodeChanged(nodeState.offsetX, nodeState.offsetY)
        }
    }

    LaunchedEffect(nodeReset) {
        if (nodeReset) {
            nodeState.resetPosition()
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { intSize ->
                containerWidthPx = intSize.width.toFloat()
                containerHeightPx = intSize.height.toFloat()
            },
    ) {
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
                }
                .graphicsLayer {
                    translationX = animatedNodeOffsetX
                    translationY = animatedNodeOffsetY
                }
                .then(
                    if (freeGesture) {
                        Modifier.pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { nodeDragging = true },
                                onDragEnd = { nodeDragging = false },
                                onDragCancel = { nodeDragging = false },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val sensitivity = 1.0f
                                    val draggedNodeOffsetX =
                                        nodeState.offsetX + (dragAmount.x * sensitivity).roundToInt()
                                    val draggedNodeOffsetY =
                                        nodeState.offsetY + (dragAmount.y * sensitivity).roundToInt()
                                    val newOffsetX =
                                        draggedNodeOffsetX.coerceIn(0f, containerWidthPx - nodeState.widthPx)
                                    val newOffsetY =
                                        draggedNodeOffsetY.coerceIn(0f, containerHeightPx - nodeState.heightPx)
                                    nodeState.updatePosition(
                                        newOffsetX = newOffsetX,
                                        newOffsetY = newOffsetY,
                                    )
                                },
                            )
                        }
                    } else {
                        Modifier
                    },
                ),
            label = label,
            onClick = onNodeClick,
            onLongClick = onNodeLongClick,
        )
    }
}

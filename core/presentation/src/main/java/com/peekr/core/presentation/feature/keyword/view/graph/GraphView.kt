package com.peekr.core.presentation.feature.keyword.view.graph

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * 모든 키워드 노드들의 부모 뷰로서, 그래프 뷰 제어를 담당한다.
 *
 * @param modifier [Modifier]
 * @param getDragOffset 드래그 오프셋 제공 람다
 * @param getZoom 줌 제공 람다
 * @param onTransform 오프셋 혹은 줌 값 변경 시 콜백
 * @param freeGesture 자유로운 제스처 모드 활성화 여부
 * @param contents 모든 노드(사용자 노드, 키워드 노드 등)들이 위치한다.
 */
@Composable
fun GraphBoard(
    modifier: Modifier = Modifier,
    getDragOffset: () -> Offset,
    getZoom: () -> Float,
    onTransform: (dragOffset: Offset, zoom: Float) -> Unit,
    freeGesture: Boolean = false,
    contents: @Composable BoxScope.(drag: () -> Offset, zoom: () -> Float) -> Unit,
) {
    val freeGestureModifier = if (freeGesture) {
        Modifier.pointerInput(Unit) {
            detectTransformGestures(
                onGesture = { centroid, pan, zoom, _ ->
                    val oldScale = getZoom()
                    val newScale = (oldScale * zoom).coerceIn(ZOOM_MIN, ZOOM_MAX)

                    val oldOffset = getDragOffset()
                    val newOffset = (oldOffset + centroid / oldScale) -
                        (centroid / newScale + pan / oldScale)

                    onTransform(newOffset, newScale)
                },
            )
        }
    } else {
        Modifier
    }

    Box(modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(freeGestureModifier)
                .graphicsLayer {
                    val dragOffset = getDragOffset()
                    val zoom = getZoom()

                    translationX = -dragOffset.x * zoom
                    translationY = -dragOffset.y * zoom
                    scaleX = zoom
                    scaleY = zoom
                    transformOrigin = TransformOrigin(0f, 0f)
                },
        ) {
            contents(getDragOffset, getZoom)
        }
    }
}

private const val ZOOM_MIN = 0.017f
private const val ZOOM_MAX = 1.5f

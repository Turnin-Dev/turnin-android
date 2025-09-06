package com.peekr.presentation.keyword.graph

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.peekr.presentation.keyword.common.model.UiKeyword
import com.peekr.presentation.keyword.common.state.rememberNodeState
import com.peekr.presentation.keyword.common.view.KeywordNodeEdge
import com.peekr.presentation.keyword.common.view.UserNode

/**
 * 키워드 그래프 뷰
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 사용자 프로필 이미지 url (사용자 노드에서 사용)
 * @param keywords 키워드 리스트
 */
@Composable
fun KeywordGraphView(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    keywords: List<UiKeyword>,
) {
    // 키워드 그래프 뷰
    GraphBoard(modifier = modifier) {
        // 사용자 노드
        UserNode(
            modifier = Modifier
                .align(Alignment.Center)
                .size(UserNodeSizeDp)
                .zIndex(2f),
            profileImageUrl = profileImageUrl,
            onClick = { },
        )

        // 키워드 노드 & 엣지
        keywords.forEach { keyword ->
            val nodeState = rememberNodeState(keyword.offsetX, keyword.offsetY)
            KeywordNodeEdge(
                modifier = Modifier.zIndex(1f),
                nodeState = nodeState,
                label = keyword.label,
                onNodeClick = { },
            )
        }
    }
}

/**
 * 모든 키워드 노드들의 부모 뷰로서, 그래프 뷰 제어를 담당한다.
 *
 * @param modifier [Modifier]
 * @param contents 모든 노드(사용자 노드, 키워드 노드 등)들이 위치한다.
 */
@Composable
private fun GraphBoard(
    modifier: Modifier = Modifier,
    contents: @Composable BoxScope.() -> Unit,
) {
    var dragOffsetX by rememberSaveable { mutableFloatStateOf(0f) }
    var dragOffsetY by rememberSaveable { mutableFloatStateOf(0f) }
    var pinchZoom by rememberSaveable { mutableFloatStateOf(1f) }

    Box(modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures(
                        onGesture = { centroid, pan, zoom, _ ->
                            val oldScale = pinchZoom
                            val newScale = pinchZoom * zoom

                            val oldOffset = Offset(dragOffsetX, dragOffsetY)
                            val newOffset = (oldOffset + centroid / oldScale) -
                                (centroid / newScale + pan / oldScale)

                            dragOffsetX = newOffset.x
                            dragOffsetY = newOffset.y
                            pinchZoom = newScale
                        },
                    )
                }.graphicsLayer(
                    translationX = -dragOffsetX * pinchZoom,
                    translationY = -dragOffsetY * pinchZoom,
                    scaleX = pinchZoom,
                    scaleY = pinchZoom,
                    transformOrigin = TransformOrigin(0f, 0f),
                ),
        ) {
            contents()
        }
    }
}

private val UserNodeSizeDp = 45.dp

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun KeywordGraphViewPreview() {
    KeywordGraphView(
        modifier = Modifier.fillMaxSize(),
        profileImageUrl = null,
        keywords = UiKeyword.samples,
    )
}

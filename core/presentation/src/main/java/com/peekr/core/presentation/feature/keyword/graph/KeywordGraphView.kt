package com.peekr.core.presentation.feature.keyword.graph

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import com.peekr.core.presentation.feature.keyword.KeywordNameType
import com.peekr.core.presentation.feature.keyword.NodeOffsetXType
import com.peekr.core.presentation.feature.keyword.NodeOffsetYType
import com.peekr.core.presentation.feature.keyword.UserIdType
import com.peekr.core.presentation.feature.keyword.UserKeywordIdType
import com.peekr.core.presentation.feature.keyword.view.KeywordNodeEdge
import com.peekr.core.presentation.feature.keyword.view.UserNode
import com.peekr.core.presentation.ui.model.UiUserKeyword

/**
 * 키워드 그래프 뷰
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 사용자 프로필 이미지 url (사용자 노드에서 사용)
 * @param nodeReset 키워드 노드 리셋 여부
 * @param keywords 키워드 리스트
 * @param onNodeClick 키워드 노드 클릭 시 콜백
 * @param onNodeLongClick 키워드 노드 길게 클릭 시 콜백
 * @param onNodeChanged 키워드 노드 위치 변화 시 콜백
 */
@Composable
fun KeywordGraphView(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    nodeReset: Boolean,
    keywords: List<UiUserKeyword>,
    onNodeClick: (UserKeywordIdType, UserIdType, KeywordNameType) -> Unit,
    onNodeLongClick: (UserKeywordIdType, KeywordNameType) -> Unit,
    onNodeChanged: (UserKeywordIdType, NodeOffsetXType, NodeOffsetYType) -> Unit,
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
            key(keyword.id) {
                KeywordNodeEdge(
                    modifier = Modifier.zIndex(1f),
                    initialOffsetX = keyword.offsetX.toFloat(),
                    initialOffsetY = keyword.offsetY.toFloat(),
                    label = keyword.keywordName,
                    nodeReset = nodeReset,
                    onNodeClick = {
                        onNodeClick(
                            keyword.id,
                            keyword.userId,
                            keyword.keywordName,
                        )
                    },
                    onNodeLongClick = {
                        onNodeLongClick(
                            keyword.id,
                            keyword.keywordName,
                        )
                    },
                    onNodeChanged = { offsetX, offsetY ->
                        onNodeChanged(keyword.id, offsetX, offsetY)
                    },
                )
            }
        }
    }
}

/**
 * 모든 키워드 노드들의 부모 뷰로서, 그래프 뷰 제어를 담당한다.
 *
 * @param modifier [Modifier]
 * @param freeGesture 자유로운 제스처 모드 활성화 여부 (첫 번째 버전에선 막아놓은 상태)
 * @param contents 모든 노드(사용자 노드, 키워드 노드 등)들이 위치한다.
 */
@Composable
private fun GraphBoard(
    modifier: Modifier = Modifier,
    freeGesture: Boolean = false,
    contents: @Composable BoxScope.() -> Unit,
) {
    var dragOffsetX by rememberSaveable { mutableFloatStateOf(0f) }
    var dragOffsetY by rememberSaveable { mutableFloatStateOf(0f) }
    var pinchZoom by rememberSaveable { mutableFloatStateOf(1f) }
    val freeGestureModifier = if (freeGesture) {
        Modifier.pointerInput(Unit) {
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
                    translationX = -dragOffsetX * pinchZoom
                    translationY = -dragOffsetY * pinchZoom
                    scaleX = pinchZoom
                    scaleY = pinchZoom
                    transformOrigin = TransformOrigin(0f, 0f)
                },
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
        nodeReset = false,
        keywords = UiUserKeyword.samples,
        onNodeClick = { _, _, _ -> },
        onNodeLongClick = { _, _ -> },
        onNodeChanged = { _, _, _ -> },
    )
}

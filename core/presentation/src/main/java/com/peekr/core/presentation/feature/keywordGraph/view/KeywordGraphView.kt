package com.peekr.core.presentation.feature.keywordGraph.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.peekr.core.presentation.feature.keywordGraph.KeywordNameType
import com.peekr.core.presentation.feature.keywordGraph.NodeOffsetXType
import com.peekr.core.presentation.feature.keywordGraph.NodeOffsetYType
import com.peekr.core.presentation.feature.keywordGraph.UserIdType
import com.peekr.core.presentation.feature.keywordGraph.UserKeywordIdType
import com.peekr.core.presentation.feature.keywordGraph.view.graph.GraphBoard
import com.peekr.core.presentation.ui.model.UiUserKeyword

/**
 * 키워드 그래프 뷰
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 사용자 프로필 이미지 url (사용자 노드에서 사용)
 * @param keywords 키워드 리스트
 * @param nodeReset 키워드 노드 리셋 여부
 * @param onUserNodeClick 사용자 노드 클릭 시 콜백
 * @param onNodeClick 키워드 노드 클릭 시 콜백
 * @param onNodeLongClick 키워드 노드 길게 클릭 시 콜백
 * @param onNodeChanged 키워드 노드 위치 변화 시 콜백
 */
@Composable
fun KeywordGraphView(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    keywords: List<UiUserKeyword>,
    freeGesture: Boolean,
    nodeReset: Boolean = false,
    onUserNodeClick: (() -> Unit)? = null,
    onNodeClick: ((UserKeywordIdType, UserIdType, KeywordNameType) -> Unit)? = null,
    onNodeLongClick: ((UserKeywordIdType, KeywordNameType) -> Unit)? = null,
    onNodeChanged: ((UserKeywordIdType, NodeOffsetXType, NodeOffsetYType) -> Unit)? = null,
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableFloatStateOf(1f) }

    // 키워드 그래프 뷰
    GraphBoard(
        modifier = modifier,
        getDragOffset = { dragOffset },
        getZoom = { zoom },
        onTransform = { newDragOffset, newZoom ->
            dragOffset = newDragOffset
            zoom = newZoom
        },
    ) { _, _ ->
        // 사용자 노드
        UserNode(
            modifier = Modifier
                .align(Alignment.Center)
                .size(UserNodeSizeDp)
                .zIndex(2f),
            profileImageUrl = profileImageUrl,
            onClick = { onUserNodeClick?.invoke() },
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
                    freeGesture = freeGesture,
                    onNodeClick = {
                        onNodeClick?.invoke(
                            keyword.id,
                            keyword.userId,
                            keyword.keywordName,
                        )
                    },
                    onNodeLongClick = {
                        onNodeLongClick?.invoke(
                            keyword.id,
                            keyword.keywordName,
                        )
                    },
                    onNodeChanged = { offsetX, offsetY ->
                        onNodeChanged?.invoke(
                            keyword.id,
                            offsetX,
                            offsetY,
                        )
                    },
                )
            }
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
        freeGesture = true,
    )
}

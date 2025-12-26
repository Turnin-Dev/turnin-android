package com.peekr.core.presentation.feature.keywordGraph.view.graph

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.presentation.feature.keywordGraph.model.UiKeywordNode
import com.peekr.core.presentation.feature.keywordGraph.model.UiUserCluster
import com.peekr.core.presentation.feature.keywordGraph.model.UiUserNode
import com.peekr.core.presentation.feature.keywordGraph.util.ClusterLayout
import com.peekr.core.presentation.feature.keywordGraph.view.UserNode
import com.peekr.core.presentation.feature.keywordGraph.view.UserNodeCanvas
import com.peekr.core.presentation.ui.model.UiUserKeyword
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 키워드 노드 레이아웃 캐싱 모델
 */
private data class KeywordNodeLayout(
    val keywordNode: UiKeywordNode,
    val offset: Offset,
    val size: Size,
    val textLayout: TextLayoutResult,
)

/**
 * 그래프 수치 리소스
 */
private object GraphDimens {
    /**
     * 클러스터 간 간격
     */
    val Spacing = 430.dp

    /**
     * 클러스터 크기
     *
     * 단일 클러스터에 대한 크기이다.
     */
    val ClusterSize = 340.dp

    /**
     * 클러스터 반지름
     *
     * 이 값이 커지면 중심 노드(사용자 노드)와 키워드 노드 간 거리가 멀어진다.
     */
    val ClusterRadius = 140.dp

    /**
     * 사용자 노드 크기
     */
    val UserNodeSize = 64.dp

    // 키워드 노드 디자인 수치 값들
    val KeywordHPadding = 16.dp
    val KeywordVPadding = 10.dp
    val KeywordCornerRadius = 100.dp

    /**
     * 줌 LOD(Level Of Detail) 값
     *
     * 이 값 이상 줌이 되는 경우 상세 정보를 표시한다.
     */
    const val ZOOM_LOD = 0.4f

    /**
     * 레이어 당 배치할 클러스터 개수 (중앙 노드를 기준으로 멀어질 수록 깊이 1당 배치될 노드 개수)
     */
    const val CLUSTERS_PER_LAYER = 5
}

/**
 * 그래프 계산 로직
 */
private object GraphMath {
    /**
     * 클러스터 내 노드의 위치 값을 계산한다.
     *
     * 정확히 5등분 한 위치 값이 반환된다.
     */
    fun calculateNodeOffset(
        index: Int,
        clusterRadiusPx: Float,
        centerX: Float,
        centerY: Float,
    ): Offset {
        val angleRadian = ((index * 72f) - 90f) * (Math.PI / 180f).toFloat()
        val tx = (cos(angleRadian) * clusterRadiusPx) + centerX
        val ty = (sin(angleRadian) * clusterRadiusPx) + centerY
        return Offset(tx, ty)
    }
}

/**
 * 키워드 네트워크 그래프
 *
 * @param modifier [Modifier]
 * @param myCluster 나의 클러스터
 * @param otherClusters 사용자들의 클러스터
 */
@OptIn(FlowPreview::class)
@Composable
fun KeywordNetworkGraph(
    modifier: Modifier = Modifier,
    myCluster: UiUserCluster,
    otherClusters: List<UiUserCluster>,
) {
    // ------------------------------ 기본 설정 상태 값들 ------------------------------
    val density = LocalDensity.current
    val spacingPx = with(density) { GraphDimens.Spacing.toPx() }
    val clusterRadiusPx = with(density) { GraphDimens.ClusterSize.toPx() }

    // ------------------------------ 상태 관리 ------------------------------
    val lazyListState = rememberLazyListState()
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }
    var selectedUserId by remember { mutableStateOf<Long?>(null) }
    // 그래프 보드 상태
    val animDragOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val animZoom = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    // ------------------------------ 데이터 전처리 ------------------------------
    // 하단 사용자 리스트용 모든 클러스터 리스트
    val allClusters = remember(myCluster, otherClusters) {
        listOf(myCluster) + otherClusters
    }
    // 모든 클러스터 좌표 계산
    val clusterPositions = remember(myCluster, otherClusters) {
        val positions = mutableMapOf<Long, Offset>()
        positions[myCluster.userNode.userId] = Offset.Zero
        otherClusters.forEachIndexed { index, cluster ->
            positions[cluster.userNode.userId] =
                ClusterLayout.Radar.getOffset(index + 1, spacingPx, GraphDimens.CLUSTERS_PER_LAYER)
        }
        positions
    }

    // ------------------------------ 뷰 ------------------------------
    Box(modifier.fillMaxSize()) {
        // 상위 그래프 보드
        GraphBoard(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { viewportSize = it },
            getDragOffset = { animDragOffset.value },
            getZoom = { animZoom.value },
            onTransform = { dragOffset, zoom ->
                coroutineScope.launch {
                    animDragOffset.snapTo(dragOffset)
                    animZoom.snapTo(zoom)
                }
            },
            freeGesture = true,
        ) { dragL, zoomL ->
            // LOD, Viewport 를 위한 값들
            val showDetail by remember {
                derivedStateOf {
                    zoomL() > GraphDimens.ZOOM_LOD
                }
            }

            // Culling을 위해 현재 화면에 보이는 사각형 영역 계산
            val viewportRect by remember(viewportSize) {
                derivedStateOf {
                    val drag = dragL()
                    val zoom = zoomL()
                    Rect(
                        left = drag.x,
                        top = drag.y,
                        right = drag.x + (viewportSize.width / zoom),
                        bottom = drag.y + (viewportSize.height / zoom),
                    ) // .inflate(100f) // (보류) 여유 패딩
                }
            }

            // 중심과 가장 가까운 사용자를 하단에 있는 사용자의 리스트에서 선택된 사용자 ID와 동기화
            val centralUserId by remember(viewportSize) {
                derivedStateOf {
                    // 모든 클러스터 중 중심점과 가장 가까운 노드 찾기
                    val currentTarget = animDragOffset.value
                    clusterPositions.minByOrNull { (_, pos) ->
                        (pos - currentTarget).getDistance()
                    }?.key
                }
            }
            LaunchedEffect(showDetail, centralUserId) {
                if (showDetail &&
                    centralUserId != null &&
                    centralUserId != selectedUserId
                ) {
                    delay(100)
                    selectedUserId = centralUserId
                    val index = allClusters.indexOfFirst { cluster ->
                        cluster.userNode.userId == centralUserId
                    }
                    lazyListState.animateScrollToItem(index)
                }
            }

            // ------------------------------ 로깅 ------------------------------
            val visibleClusters = remember(viewportRect) {
                val clusters = mutableListOf<UiUserCluster>()
                (otherClusters + myCluster).map { cluster ->
                    val pos = clusterPositions[cluster.userNode.userId] ?: Offset.Zero
                    val center = Offset(viewportSize.width / 2f, viewportSize.height / 2f)
                    val absolutePos = pos + center
                    if (viewportRect.overlaps(Rect(absolutePos, clusterRadiusPx))) {
                        clusters.add(cluster)
                    }
                }

                clusters
            }

            LaunchedEffect(visibleClusters) {
                Log.d("Graph_Debug", "현재 렌더링 중인 클러스터 개수: ${visibleClusters.count()} / ${otherClusters.size + 1}")
                Log.d("Graph_Debug", "현재 렌더링 중인 클러스터: $visibleClusters / ${otherClusters.size + 1}")
            }
            // ------------------------------ 로깅 끝 ------------------------------

            // 렌더링 레이어
            Box(modifier = Modifier.fillMaxSize()) {
                // 배경에 궤도 선 그리기
                BackgroundOrbits(
                    viewportSize = viewportSize,
                    spacingPx = spacingPx,
                    zoom = zoomL(),
                    maxDepth = (otherClusters.size / GraphDimens.CLUSTERS_PER_LAYER) + 1,
                )

                // 클러스터 렌더링 루프 및 컬링
                val viewportCenter = Offset(viewportSize.width / 2f, viewportSize.height / 2f)
                (otherClusters + myCluster).forEach { cluster ->
                    key(cluster.userNode.userId) {
                        val pos = clusterPositions[cluster.userNode.userId] ?: Offset.Zero
                        val absolutePos = pos + viewportCenter

                        // 클러스터 컬링 체크
                        val isClusterVisible = remember(cluster.userNode.userId, viewportRect) {
                            viewportRect.overlaps(Rect(absolutePos, clusterRadiusPx))
                        }

                        // 화면 안에 있을 때만 클러스터 컴포저블 생성
                        if (isClusterVisible) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .graphicsLayer {
                                        translationX = pos.x
                                        translationY = pos.y
                                        compositingStrategy = CompositingStrategy.ModulateAlpha
                                    },
                            ) {
                                ClusterView(
                                    showDetail = showDetail,
                                    profileImageUrl = cluster.userNode.profileImageUrl,
                                    keywords = cluster.keywordNodes,
                                    getZoom = { zoomL() },
                                    onKeywordNodeClick = { keywordNode ->
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        // 사용자 리스트 (하단에 위치)
        UserCardList(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            state = lazyListState,
            users = allClusters.map { it.userNode },
            selectedUserId = selectedUserId,
            onUserClick = { userNode ->
                selectedUserId = userNode.userId
                val targetPos = clusterPositions[userNode.userId] ?: Offset.Zero

                coroutineScope.launch {
                    launch {
                        animDragOffset.animateTo(
                            targetValue = targetPos,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        )
                    }
                    launch {
                        animZoom.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                        )
                    }
                }
            },
        )
    }
}

/**
 * 클러스터 뷰
 *
 * 클러스터에는 사용자 노드와 키워드 노드들이 배치된다.
 *
 * @param showDetail LOD(Level Of Detail)를 처리하기 위한 파라미터
 * @param profileImageUrl 프로필 사진 url
 * @param keywords 키워드 리스트
 * @param getZoom 줌 값을 람다를 통해 전달한다
 * @param onKeywordNodeClick 키워드 노드 클릭 시 콜백
 */
@Composable
private fun ClusterView(
    showDetail: Boolean,
    profileImageUrl: String?,
    keywords: List<UiKeywordNode>,
    getZoom: () -> Float,
    onKeywordNodeClick: (UiKeywordNode) -> Unit,
) {
    val density = LocalDensity.current
    val textMeasure = rememberTextMeasurer()

    val clusterRadiusPx = with(density) { GraphDimens.ClusterRadius.toPx() }
    val clusterSizePx = with(density) { GraphDimens.ClusterSize.toPx() }
    val centerX = clusterSizePx / 2
    val centerY = clusterSizePx / 2

    // 키워드 노드 디자인 수치
    val hPaddingPx = with(density) { GraphDimens.KeywordHPadding.toPx() }
    val vPaddingPx = with(density) { GraphDimens.KeywordVPadding.toPx() }
    val cornerRadiusPx = with(density) { GraphDimens.KeywordCornerRadius.toPx() }
    val textStyle = PeekrTheme.typography.caption1
    val userNodeColor = PeekrTheme.colorScheme.primary

    // 키워드 노드 레이아웃 캐싱 (무거운 작업인 TextMeasure 최적화)
    val keywordNodeLayouts = remember(keywords) {
        keywords.mapIndexed { index, keyword ->
            val nodeOffset = GraphMath.calculateNodeOffset(index, clusterRadiusPx, centerX, centerY)
            val textLayout = textMeasure.measure(keyword.keywordName, textStyle)
            val width = textLayout.size.width + (hPaddingPx * 2)
            val height = textLayout.size.height + (vPaddingPx * 2)
            KeywordNodeLayout(
                keywordNode = keyword,
                offset = nodeOffset,
                size = Size(width, height),
                textLayout = textLayout,
            )
        }
    }

    if (showDetail) {
        Box(Modifier.size(GraphDimens.ClusterSize), contentAlignment = Alignment.Center) {
            val lineColor = PeekrTheme.colorScheme.componentEdge.copy(0.6f)
            val nodeColor = PeekrTheme.colorScheme.backgroundNormal
            val nodeShadowColor = if (isSystemInDarkTheme()) Color.White else Color.Black
            val textColor = PeekrTheme.colorScheme.textNormal

            // 연결 선, 그림자, 노드, 텍스트 그리기
            Canvas(Modifier.fillMaxSize()) {
                drawClusterDetails(
                    keywordNodeLayouts = keywordNodeLayouts,
                    centerX = centerX,
                    centerY = centerY,
                    nodeCornerRadiusPx = cornerRadiusPx,
                    nodeColor = nodeColor,
                    nodeShadowColor = nodeShadowColor,
                    lineColor = lineColor,
                    textColor = textColor,
                )
            }

            // 키워드 노드 터치 영역
            keywordNodeLayouts.forEach { layout ->
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = layout.offset.x - centerX
                            translationY = layout.offset.y - centerY
                        }
                        .size(
                            width = with(density) { layout.size.width.toDp() },
                            height = with(density) { layout.size.height.toDp() },
                        )
                        .clip(RoundedCornerShape(GraphDimens.KeywordCornerRadius))
                        .clickableSingle {
                            onKeywordNodeClick(layout.keywordNode)
                        },
                )
            }

            // 사용자 노드
            UserNode(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(GraphDimens.UserNodeSize)
                    .zIndex(2f)
                    .graphicsLayer {
                        val currentZoom = (1f / getZoom()).coerceIn(0.7f, 1.3f)

                        scaleX = currentZoom
                        scaleY = currentZoom
                    },
                profileImageUrl = profileImageUrl,
                filterQuality = FilterQuality.Medium,
                onClick = {},
            )
        }
    } else {
        UserNodeCanvas(
            modifier = Modifier
                .size(GraphDimens.UserNodeSize)
                .zIndex(2f)
                .graphicsLayer {
                    val currentZoom = (1f / getZoom()).coerceIn(0.7f, 1.3f)

                    scaleX = currentZoom
                    scaleY = currentZoom
                },
            color = userNodeColor,
        )
    }
}

/**
 * 배경에 그릴 궤도 선
 *
 * @param viewportSize 뷰포트 사이즈
 * @param spacingPx 궤도 간 간격
 * @param zoom 줌 수치
 * @param maxDepth 최대 깊이 (궤도 개수)
 */
@Composable
private fun BackgroundOrbits(
    viewportSize: IntSize,
    spacingPx: Float,
    zoom: Float,
    maxDepth: Int,
) {
    val orbitColor = remember { Color.Gray.copy(alpha = 0.2f) }
    val strokeWidth = remember(zoom) { (1f / zoom).coerceAtLeast(0.5f) }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(viewportSize.width / 2f, viewportSize.height / 2f)
        val style = Stroke(width = strokeWidth)

        // 최대 깊이 계산 (예: 전체 노드가 20개고 층당 5개면 4층까지)
        for (i in 1..maxDepth) {
            drawCircle(
                color = orbitColor,
                radius = spacingPx * i,
                center = center,
                style = style,
            )
        }
    }
}

/**
 * 분리된 클러스터 내부 Canvas 영역
 *
 * @param keywordNodeLayouts [KeywordNodeLayout] 리스트
 * @param centerX 중심 X 값
 * @param centerY 중심 Y 값
 * @param nodeCornerRadiusPx 노드 CornerRadius
 * @param nodeColor 노드 색상
 * @param nodeShadowColor 노드 그림자 색상
 * @param lineColor 연결 선 색상
 * @param textColor 노드 텍스트 색상
 */
private fun DrawScope.drawClusterDetails(
    keywordNodeLayouts: List<KeywordNodeLayout>,
    centerX: Float,
    centerY: Float,
    nodeCornerRadiusPx: Float,
    nodeColor: Color,
    nodeShadowColor: Color,
    lineColor: Color,
    textColor: Color,
) {
    keywordNodeLayouts.forEachIndexed { index, keywordNodeLayout ->
        val nodeOffset = keywordNodeLayout.offset
        val nodeSize = keywordNodeLayout.size
        val textLayout = keywordNodeLayout.textLayout

        // 연결 선 레이어
        drawLine(
            color = lineColor,
            start = Offset(centerX, centerY),
            end = Offset(nodeOffset.x, nodeOffset.y),
            strokeWidth = 2f,
        )

        // 그림자 레이어
        val shadowSize = 1f
        drawRoundRect(
            color = nodeShadowColor.copy(0.5f),
            topLeft = Offset(
                x = (nodeOffset.x - nodeSize.width / 2) - shadowSize,
                y = (nodeOffset.y - nodeSize.height / 2) - shadowSize,
            ),
            size = Size(nodeSize.width + shadowSize * 2, nodeSize.height + shadowSize * 2),
            cornerRadius = CornerRadius(nodeCornerRadiusPx),
        )

        // 노드 배경 레이어
        drawRoundRect(
            color = nodeColor,
            topLeft = Offset(nodeOffset.x - nodeSize.width / 2, nodeOffset.y - nodeSize.height / 2),
            size = Size(nodeSize.width, nodeSize.height),
            cornerRadius = CornerRadius(nodeCornerRadiusPx),
        )

        // 텍스트 레이어
        drawText(
            textLayoutResult = textLayout,
            topLeft = Offset(
                x = nodeOffset.x - textLayout.size.width / 2,
                y = nodeOffset.y - textLayout.size.height / 2,
            ),
            color = textColor,
        )
    }
}

// ------------------------------ Previews ------------------------------
@Preview
@Composable
private fun KeywordNetworkGraphPreview() {
    PeekrAppTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.White),
        ) {
            KeywordNetworkGraph(
                modifier = Modifier.fillMaxSize(),
                myCluster = UiUserCluster(
                    userNode = UiUserNode(0, "Me", null),
                    keywordNodes = UiUserKeyword.samples.map {
                        UiKeywordNode(it.id.value, it.keywordId.value, it.keywordName)
                    },
                ),
                otherClusters = List(500) {
                    UiUserCluster(
                        userNode = UiUserNode(
                            userId = (it + 1).toLong(),
                            userName = "username$it",
                            profileImageUrl = "https://fujifilm-korea.co.kr/image/playwith/wl/dt/soliywlj/html/278243925tmlm.jpg",
                        ),
                        keywordNodes = UiUserKeyword.samples.map {
                            UiKeywordNode(it.id.value, it.keywordId.value, it.keywordName)
                        },
                    )
                },
            )
        }
    }
}

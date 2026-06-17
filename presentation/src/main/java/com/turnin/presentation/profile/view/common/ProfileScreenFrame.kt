package com.turnin.presentation.profile.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.ui.component.indicator.TurninIndicator

/**
 * 프로필 화면 프레임
 *
 * 키워드 리스트 영역([keywords])은 [LazyListScope] 범위이고 필수 패딩을 직접 적용해줘야 한다.
 *
 * @param modifier [Modifier]
 * @param isRefreshing 새로고침 여부
 * @param onRefresh 새로고침 콜백
 * @param topBar 탑바 영역
 * @param profile 프로필 영역
 * @param keywordsTitle 키워드 타이틀 텍스트
 * @param keywords 키워드 리스트
 */
@Composable
fun ProfileScreenFrame(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit,
    topBar: @Composable ColumnScope.() -> Unit,
    profile: @Composable ColumnScope.() -> Unit,
    keywordsTitle: @Composable BoxScope.() -> Unit,
    keywords: LazyListScope.() -> Unit,
) {
    val pullToRefreshState = rememberPullToRefreshState()
    val lazyListState = rememberLazyListState()

    var isManualRefresh by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isRefreshing, isManualRefresh) {
        if (!isRefreshing) isManualRefresh = false
    }

    Column(modifier) {
        // TopBar
        topBar()

        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            state = pullToRefreshState,
            isRefreshing = isManualRefresh,
            onRefresh = {
                isManualRefresh = true
                onRefresh()
            },
            indicator = { TurninIndicator(isRefreshing, pullToRefreshState) },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState,
                contentPadding = PaddingValues(bottom = 30.dp),
                overscrollEffect = null,
            ) {
                // Profile
                item {
                    Column(
                        Modifier
                            .padding(
                                horizontal = ScreenTokens.HorizontalPadding,
                                vertical = 10.dp,
                            ),
                    ) {
                        profile()
                    }
                }

                // Keywords Title
                stickyHeader {
                    Column {
                        // Divider
                        DividerSection(Modifier.fillMaxWidth())
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(TurninTheme.colorScheme.backgroundNormal)
                                .padding(
                                    horizontal = ScreenTokens.HorizontalPadding,
                                    vertical = 10.dp,
                                ),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            keywordsTitle()
                        }
                    }
                }

                // Keywords
                keywords()
            }
        }
    }
}

@Composable
private fun DividerSection(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 0.35.dp,
        color = TurninTheme.colorScheme.lineDivider,
    )
}

@Preview(heightDp = 400)
@Composable
private fun ProfileScreenFramePreview() {
    TurninAppTheme {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = false,
            onRefresh = {},
            topBar = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color.Red),
                ) { Text("TopBar") }
            },
            profile = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(166.dp)
                        .background(Color.Blue),
                ) { Text("Profile") }
            },
            keywordsTitle = {
                Text(
                    text = "키워드 (0/5)",
                    style = TurninTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                )
            },
            keywords = {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .background(Color.Green),
                    ) { Text("KeywordGraph") }
                }
            },
        )
    }
}

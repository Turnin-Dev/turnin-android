package com.turnin.core.designsystem.component.tabBar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.theme.PeekrTheme
import kotlinx.coroutines.launch

/**
 * 모든 TabBar 의 기본이 되는 Core TabBar
 *
 * - [TabRow], [Tab], [HorizontalPager] 를 통합해서 제작한 TabBar 이다.
 * - **사용 시에는 [tabs]의 개수와 [pageContent]의 개수가 일치해야 한다.**
 *
 * @param tabs 탭 타이틀 리스트
 * @param pageContent [PagerScope] 범위의 페이지 컨텐츠
 * @param containerColor 컨테이너 색상
 * @param contentColor 컨텐츠 색상
 * @param modifier [Modifier]
 * @param userScrollEnabled 사용자 스크롤 여부
 */
@Composable
internal fun CoreTabBar(
    tabs: List<String>,
    pageContent: @Composable (PagerScope.(Int) -> Unit),
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { tabs.size }

    Column(modifier = modifier.fillMaxSize()) {
        SecondaryTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = containerColor,
            contentColor = contentColor,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(pagerState.currentPage),
                    height = 2.dp,
                    color = PeekrTheme.colorScheme.textNormal,
                )
            },
            divider = {
                HorizontalDivider(thickness = 0.5.dp, color = PeekrTheme.colorScheme.textNormal)
            },
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    modifier = Modifier,
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = tab,
                        style = PeekrTheme.typography.headline5,
                        fontWeight = FontWeight.SemiBold,
                        color = PeekrTheme.colorScheme.textNormal,
                    )
                }
            }
        }

        Surface(
            color = containerColor,
            contentColor = contentColor,
        ) {
            HorizontalPager(
                modifier = modifier,
                state = pagerState,
                userScrollEnabled = userScrollEnabled,
            ) { page ->
                pageContent(page)
            }
        }
    }
}

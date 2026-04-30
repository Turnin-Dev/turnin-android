package com.turnin.presentation.discover.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.chip.TurninChip
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.discover.model.UiDiscoverKeyword

/**
 * 키워드 영역
 *
 * @param modifier [Modifier]
 * @param keywords 키워드 목록
 * @param onClick 키워드 클릭 시 콜백
 * @param contentPadding [PaddingValues]
 */
@Composable
internal fun KeywordsFlowView(
    modifier: Modifier = Modifier,
    keywords: List<UiDiscoverKeyword>,
    onClick: (UiDiscoverKeyword) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    var chipHeight by remember { mutableStateOf(30.dp) }
    val density = LocalDensity.current

    LazyHorizontalStaggeredGrid(
        modifier = modifier.height(
            if (keywords.size <= 1) chipHeight else chipHeight * 2 + Gap,
        ),
        rows = StaggeredGridCells.Fixed(if (keywords.size <= 1) 1 else 2),
        contentPadding = contentPadding,
        horizontalItemSpacing = Gap,
        verticalArrangement = Arrangement.spacedBy(Gap),
    ) {
        items(
            items = keywords,
            key = { it.userKeywordId },
        ) { keyword ->
            TurninChip(
                modifier = if (keyword == keywords.first()) {
                    Modifier.onSizeChanged { size ->
                        chipHeight = with(density) { size.height.toDp() }
                    }
                } else {
                    Modifier
                },
                text = keyword.keywordName,
                color = TurninTheme.colorScheme.componentKeywordBG,
                onClick = { onClick(keyword) },
            )
        }
    }
}

private val Gap = 8.dp

@PreviewLightDarkWithBackground
@Composable
private fun KeywordsFlowLayoutPreview() {
    TurninAppTheme {
        KeywordsFlowView(
            modifier = Modifier.width(200.dp),
            keywords = listOf(
                UiDiscoverKeyword(1L, 1L, "아주 긴 키워드 테스트"),
                UiDiscoverKeyword(2L, 2L, "Confidence"),
                UiDiscoverKeyword(3L, 3L, "Mechanical Keyboards"),
                UiDiscoverKeyword(4L, 4L, "Software Engineering"),
                UiDiscoverKeyword(5L, 5L, "키워드 1"),
            ),
            onClick = {},
        )
    }
}

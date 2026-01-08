package com.peekr.presentation.profile.view.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordId.Companion.invoke
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserId.Companion.invoke
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.model.UserKeywordId.Companion.invoke
import com.peekr.core.presentation.ui.model.UiUserKeyword
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.profile.view.common.keywordItems

fun LazyListScope.keywordItems(
    keywords: List<UiUserKeyword>,
    onClick: (UiUserKeyword) -> Unit,
) {
    items(
        count = keywords.size,
        key = { keywords[it].id.value },
    ) { index ->
        val keyword = keywords[index]

        KeywordCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenTokens.HorizontalPadding),
            keyword = keyword.keywordName,
            description = keyword.description,
            onClick = { onClick(keyword) },
        )
        Spacer(Modifier.height(10.dp))
    }
}

fun LazyListScope.keywordItemsSkeleton() {
    items(count = 2) {
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .padding(horizontal = ScreenTokens.HorizontalPadding),
            shape = RoundedCornerShape(PeekrTheme.shape.small),
        )
        Spacer(Modifier.height(10.dp))
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun KeywordItemsPreview() {
    PeekrAppTheme {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            keywordItems(
                keywords = LargeKeywordList,
                onClick = {},
            )
        }
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun KeywordItemsSkeletonPreview() {
    PeekrAppTheme {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            keywordItemsSkeleton()
        }
    }
}

private val LargeKeywordList = List(20) {
    UiUserKeyword(
        id = UserKeywordId((it + 1).toLong()),
        userId = UserId(1L),
        keywordId = KeywordId((it + 1).toLong()),
        keywordName = "Label ${it + 1}",
        description = "Description ${it + 1}",
        createdAt = 0L,
        updatedAt = 0L,
    )
}

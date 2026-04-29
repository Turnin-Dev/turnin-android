package com.turnin.presentation.profile.view.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.skeleton.SkeletonBox
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.domain.profile.model.ProfileRule
import com.turnin.presentation.R

/**
 * 프로필 화면에서 사용하는 키워드 타이틀 뷰 (키워드 개수 표시 UI)
 *
 * @param modifier [Modifier]
 * @param count 키워드 개수
 */
@Composable
fun KeywordsTitleView(
    modifier: Modifier = Modifier,
    count: Int,
) {
    Text(
        modifier = modifier,
        text = stringResource(R.string.my_profile_screen_keywords_title) +
            " ($count/${ProfileRule.MAX_KEYWORD_COUNT})",
        style = PeekrTheme.typography.body1,
        fontWeight = FontWeight.Bold,
        color = PeekrTheme.colorScheme.textNormal,
    )
}

@Composable
fun KeywordsTitleSkeleton() {
    SkeletonBox(Modifier.size(84.2.dp, 22.4.dp))
}

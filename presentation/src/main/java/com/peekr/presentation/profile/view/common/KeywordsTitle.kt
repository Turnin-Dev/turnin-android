package com.peekr.presentation.profile.view.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.skeleton.SkeletonBox
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.domain.profile.model.ProfileRule
import com.peekr.presentation.R

@Composable
fun KeywordsTitle(
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

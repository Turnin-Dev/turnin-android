package com.peekr.presentation.profile.view.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.icon.Arrow1Right
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R

@Composable
fun KeywordCard(
    modifier: Modifier = Modifier,
    keyword: String,
    description: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(PeekrTheme.shape.small))
            .background(PeekrTheme.colorScheme.componentKeywordBG)
            .clickableSingle { onClick() }
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp, alignment = Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            // 키워드
            Text(
                text = keyword,
                style = PeekrTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = PeekrTheme.colorScheme.textNormal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // 내용
            Text(
                text = description,
                style = PeekrTheme.typography.body4,
                fontWeight = FontWeight.Normal,
                color = PeekrTheme.colorScheme.textNormal,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // 자세히 보기 아이콘
        PeekrIcon(
            icon = PeekrIcons.Default.Normal.Arrow1Right,
            iconSize = PeekrIconSize.ExtraTiny,
            contentDescription = stringResource(R.string.my_profile_screen_keyword_card),
            tint = PeekrTheme.colorScheme.textNormal,
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun KeywordCardPreview() {
    PeekrAppTheme {
        KeywordCard(
            modifier = Modifier.fillMaxWidth(),
            keyword = "Keyword",
            description = "description, description, description, description",
            onClick = {},
        )
    }
}

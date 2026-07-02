package com.turnin.presentation.profile.view.common

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
import com.turnin.core.designsystem.component.icon.TurninIcon
import com.turnin.core.designsystem.component.icon.TurninIconSize
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.click.clickableSingle
import com.turnin.core.designsystem.util.icon.Arrow1Right
import com.turnin.core.designsystem.util.icon.TurninIcons
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.R

/**
 * 키워드 카드 뷰
 *
 * @param modifier [Modifier]
 * @param keyword 키워드 명
 * @param description 키워드 내용
 * @param onClick 키워드 클릭 시 콜백
 */
@Composable
fun KeywordCardView(
    modifier: Modifier = Modifier,
    keyword: String,
    description: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(TurninTheme.shape.small))
            .background(TurninTheme.colorScheme.componentKeywordBG)
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
                style = TurninTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                color = TurninTheme.colorScheme.textNormal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            // 내용
            Text(
                text = description,
                style = TurninTheme.typography.body4,
                fontWeight = FontWeight.Normal,
                color = TurninTheme.colorScheme.textNormal,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // 자세히 보기 아이콘
        TurninIcon(
            icon = TurninIcons.Default.Normal.Arrow1Right,
            iconSize = TurninIconSize.ExtraTiny,
            contentDescription = stringResource(R.string.my_profile_screen_keyword_card),
            tint = TurninTheme.colorScheme.textNormal,
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun KeywordCardPreview() {
    TurninAppTheme {
        KeywordCardView(
            modifier = Modifier.fillMaxWidth(),
            keyword = "Keyword",
            description = "description, description, description, description",
            onClick = {},
        )
    }
}

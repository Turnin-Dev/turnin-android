package com.peekr.core.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Exclamation
import com.peekr.core.designsystem.util.icon.PeekrIconType
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.presentation.R
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground

/**
 * 빈 화면 안내 뷰
 *
 * @param modifier [Modifier]
 */
@Composable
fun EmptyGuidance(
    modifier: Modifier = Modifier,
    icon: PeekrIconType? = null,
    title: String? = null,
    description: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (title != null && icon == null && description == null) {
            // 단일 타이틀
            Text(
                text = title,
                style = PeekrTheme.typography.headline3,
                fontWeight = FontWeight.Medium,
                color = PeekrTheme.colorScheme.textAssist2,
                textAlign = TextAlign.Center,
            )
        } else {
            // 상단 아이콘
            icon?.let {
                Icon(
                    modifier = Modifier
                        .size(56.dp)
                        .background(PeekrTheme.colorScheme.primary.copy(0.1f), CircleShape)
                        .padding(4.dp),
                    imageVector = it.imageVector,
                    contentDescription = stringResource(R.string.empty_guidance_icon_content_desc),
                    tint = PeekrTheme.colorScheme.primary,
                )
            }

            // 타이틀
            title?.let {
                Text(
                    text = it,
                    style = PeekrTheme.typography.headline3,
                    fontWeight = FontWeight.SemiBold,
                    color = PeekrTheme.colorScheme.textNormal,
                    textAlign = TextAlign.Center,
                )
            }

            // 설명
            description?.let {
                Text(
                    text = it,
                    style = PeekrTheme.typography.body3,
                    fontWeight = FontWeight.Normal,
                    color = PeekrTheme.colorScheme.textAssist2,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ------------------------------ Previews ------------------------------

@PreviewLightDarkWithBackground
@Composable
private fun EmptyGuidance_Full_Preview() {
    PeekrAppTheme {
        EmptyGuidance(
            icon = PeekrIcons.Filled.Normal.Exclamation,
            title = "아직 키워드가 없어요",
            description = "나를 구성하고 있는 키워드를 추가하고,\n" +
                "유사한 키워드로 소통하는 다른 사람들을\n" +
                "탐색해봐요",
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun EmptyGuidance_Without_Icon_Preview() {
    PeekrAppTheme {
        EmptyGuidance(
            title = "아직 키워드가 없어요",
            description = "나를 구성하고 있는 키워드를 추가하고,\n" +
                "유사한 키워드로 소통하는 다른 사람들을\n" +
                "탐색해봐요",
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun EmptyGuidance_Only_Title_Preview() {
    PeekrAppTheme {
        EmptyGuidance(
            title = "아직 키워드가 없어요",
        )
    }
}

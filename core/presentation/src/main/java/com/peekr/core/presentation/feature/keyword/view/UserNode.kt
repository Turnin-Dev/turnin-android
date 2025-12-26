package com.peekr.core.presentation.feature.keyword.view

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.presentation.R

/**
 * 사용자 노드 컴포넌트
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 사용자 프로필 사진 url
 * @param filterQuality 사진 화질
 * @param onClick 사용자 노드 클릭 시
 */
@Composable
fun UserNode(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    filterQuality: FilterQuality = FilterQuality.Medium,
    onClick: () -> Unit,
) {
    PeekrAvatar(
        modifier = modifier
            .graphicsLayer {
                shadowElevation = SHADOW_ELEVATION
                shape = CircleShape
                clip = true
            },
        model = profileImageUrl,
        contentDescription = stringResource(R.string.keyword_common_user_node),
        filterQuality = filterQuality,
        onClick = onClick,
    )
}

/**
 * 사용자 노드 단일 색상 버전
 *
 * @param modifier [Modifier]
 * @param color 노드 색상
 */
@Composable
fun UserNodeCanvas(
    modifier: Modifier = Modifier,
    color: Color,
) {
    Canvas(
        modifier = modifier
            .graphicsLayer {
                shadowElevation = SHADOW_ELEVATION
                shape = CircleShape
                clip = true
            },
    ) {
        drawCircle(color = color)
    }
}

private const val SHADOW_ELEVATION = 10f

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UserNodePreview() {
    PeekrAppTheme {
        Box(Modifier.size(80.dp), Alignment.Center) {
            UserNode(
                modifier = Modifier.size(45.dp),
                profileImageUrl = null,
                onClick = {},
            )
        }
    }
}

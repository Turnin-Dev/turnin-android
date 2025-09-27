package com.peekr.core.presentation.keyword.view

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.util.PeekrShadowType
import com.peekr.core.designsystem.util.peekrShadow
import com.peekr.core.presentation.R

/**
 * 사용자 노드 컴포넌트
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 사용자 프로필 사진 url
 * @param onClick 사용자 노드 클릭 시
 */
@Composable
fun UserNode(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .peekrShadow(PeekrShadowType.Normal, shape = CircleShape),
    ) {
        PeekrAvatar(
            modifier = Modifier.size(AvatarSize),
            model = profileImageUrl,
            contentDescription = stringResource(R.string.keyword_common_user_node),
            onClick = onClick,
        )
    }
}

private val AvatarSize = 45.dp

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun UserNodePreview() {
    PeekrAppTheme {
        Box(Modifier.size(80.dp), Alignment.Center) {
            UserNode(
                modifier = Modifier.size(AvatarSize),
                profileImageUrl = null,
                onClick = {},
            )
        }
    }
}

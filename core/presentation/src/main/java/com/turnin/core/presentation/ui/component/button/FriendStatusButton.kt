package com.turnin.core.presentation.ui.component.button

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.turnin.core.designsystem.component.button.TurninButtonStyle
import com.turnin.core.designsystem.component.button.TurninOutlinedButton
import com.turnin.core.designsystem.component.button.TurninSolidButton
import com.turnin.core.designsystem.util.icon.Arrow2Right
import com.turnin.core.designsystem.util.icon.Cancel
import com.turnin.core.designsystem.util.icon.Check
import com.turnin.core.designsystem.util.icon.Plus
import com.turnin.core.designsystem.util.icon.TurninIcons
import com.turnin.core.domain.friend.model.FriendStatus
import com.turnin.core.presentation.R

/**
 * 친구 상태 버튼
 *
 * 친구 상태([friendStatus])에 따라 버튼을 표시한다.
 *
 * [onClick]시에는 [friendStatus]의 `toggle`을 수행하여 바뀐 친구 상태를 재 전달하는 것을 권장한다.
 *
 * @param modifier [Modifier]
 * @param friendStatus 친구 상태 [FriendStatus]
 * @param buttonStyle 버튼 스타일 [TurninButtonStyle]
 * @param onClick 버튼 클릭 시 콜백
 */
@Composable
fun FriendStatusButton(
    modifier: Modifier = Modifier,
    friendStatus: FriendStatus,
    buttonStyle: TurninButtonStyle = TurninButtonStyle.Tiny,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (friendStatus) {
            FriendStatus.NOTHING -> {
                TurninSolidButton(
                    text = stringResource(R.string.friend_status_btn_nothing),
                    style = buttonStyle,
                    icon = TurninIcons.Default.Bold.Plus,
                    onClick = onClick,
                )
            }

            FriendStatus.FRIENDS -> {
                TurninOutlinedButton(
                    text = stringResource(R.string.friend_status_btn_friends),
                    style = buttonStyle,
                    icon = TurninIcons.Default.Bold.Check,
                    onClick = onClick,
                )
            }

            FriendStatus.REQUESTED -> {
                TurninOutlinedButton(
                    text = stringResource(R.string.friend_status_btn_requested),
                    style = buttonStyle,
                    icon = TurninIcons.Default.Bold.Cancel,
                    onClick = onClick,
                )
            }

            FriendStatus.RECEIVED -> {
                TurninSolidButton(
                    text = stringResource(R.string.friend_status_btn_received),
                    style = buttonStyle,
                    icon = TurninIcons.Default.Bold.Arrow2Right,
                    onClick = onClick,
                )
            }
        }
    }
}

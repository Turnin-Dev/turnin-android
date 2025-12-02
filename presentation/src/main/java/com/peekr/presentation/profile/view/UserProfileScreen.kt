package com.peekr.presentation.profile.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.icon.Report
import com.peekr.presentation.R

@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        ProfileScreenFrame(
            modifier = Modifier.fillMaxSize(),
            topBar = {
//                TopBar(
//                    modifier = Modifier
//                        .fillMaxWidth(),
//                    title = profile
//                )
            },
            profile = {
            },
            keywordGraph = {
            },
        )
    }
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param title 탑바 타이틀
 * @param onReportClick 신고 클릭 시
 * @param onBackPressed 뒤로 가기 클릭 시
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    onReportClick: () -> Unit,
    onBackPressed: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        title = title,
        optionSlot = {
            PeekrIconButton(
                icon = PeekrIcons.Filled.Bold.Report,
                iconSize = TopBarOptionIconSize,
                contentDescription = stringResource(R.string.user_profile_top_bar_report),
                onClick = onReportClick,
            )
        },
        onBackPressed = onBackPressed,
    )
}

private val TopBarOptionIconSize = PeekrIconSize.Small

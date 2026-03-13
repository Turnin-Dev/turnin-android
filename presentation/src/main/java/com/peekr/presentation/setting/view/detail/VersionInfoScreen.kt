package com.peekr.presentation.setting.view.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.icon.PeekrIcon
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.logo.PeekrLogo
import com.peekr.core.designsystem.component.logo.PeekrLogoType
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.icon.Arrow1Right
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.presentation.R

/**
 * 버전 정보 화면
 *
 * @param modifier [Modifier]
 * @param versionName 버전 이름
 * @param onServiceTermClick 서비스 이용약관 클릭 시 콜백
 * @param onPrivacyPolicyClick 개인정보 처리방침 클릭 시 콜백
 * @param onBackPressed 뒤로 가기 클릭 시 콜백
 */
@Composable
fun VersionInfoScreen(
    modifier: Modifier = Modifier,
    versionName: String,
    onServiceTermClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onBackPressed: () -> Unit,
) {
    Column(modifier) {
        // 탑바
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
            onBackPressed = onBackPressed,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = TopPaddingDp),
            verticalArrangement = Arrangement.spacedBy(VersionItemGapDp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 로고 & 버전 정보
            LogoAndVersion(
                modifier = Modifier.fillMaxWidth(),
                versionName = versionName,
            )
            // 서비스 이용약관 & 개인정보 처리방침
            PolicyLinkGroup(
                modifier = Modifier.fillMaxWidth(),
                onServiceTermClick = onServiceTermClick,
                onPrivacyPolicyClick = onPrivacyPolicyClick,
            )
        }
    }
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param onBackPressed 뒤로 가기 클릭 시 콜백
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    onBackPressed: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        title = stringResource(R.string.setting_detail_version_info_top_bar_title),
        onBackPressed = onBackPressed,
    )
}

/**
 * 로고 & 버전 정보 뷰
 *
 * @param modifier [Modifier]
 * @param versionName 버전 이름
 */
@Composable
private fun LogoAndVersion(
    modifier: Modifier = Modifier,
    versionName: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LogoGapDp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PeekrLogo(
            logoType = PeekrLogoType.Icon,
            logoWidth = LOGO_WIDTH,
        )
        Text(
            text = versionName,
            style = PeekrTheme.typography.display1,
            fontWeight = FontWeight.SemiBold,
            color = PeekrTheme.colorScheme.textNormal,
        )
    }
}

/**
 * 정책 관련 링크 모음 뷰
 *
 * 서비스 이용약관, 개인정보 처리 방침 등이 포함된다.
 *
 * @param modifier [Modifier]
 * @param onServiceTermClick 서비스 이용약관 클릭 시 콜백
 * @param onPrivacyPolicyClick 개인정보 처리방침 클릭 시 콜백
 */
@Composable
private fun PolicyLinkGroup(
    modifier: Modifier = Modifier,
    onServiceTermClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
) {
    Column(modifier) {
        PolicyLink(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.setting_detail_version_info_policy_service_term),
            onClick = onServiceTermClick,
        )
        PolicyDivider(Modifier.fillMaxWidth())
        PolicyLink(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.setting_detail_version_info_policy_privacy_policy),
            onClick = onPrivacyPolicyClick,
        )
        PolicyDivider(Modifier.fillMaxWidth())
    }
}

/**
 * 정책 링크 뷰
 *
 * @param modifier [Modifier]
 * @param text 링크에 표시될 텍스트
 * @param onClick 링크 클릭 시 콜백
 */
@Composable
private fun PolicyLink(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickableSingle(onClick = onClick)
            .padding(LinkPaddingDp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            style = PeekrTheme.typography.body3Normal,
            fontWeight = FontWeight.Normal,
            color = PeekrTheme.colorScheme.textNormal,
        )
        PeekrIcon(
            icon = PeekrIcons.Default.Normal.Arrow1Right,
            iconSize = PeekrIconSize.Small,
            contentDescription = null,
            tint = PeekrTheme.colorScheme.lineNormal,
        )
    }
}

/**
 * 정책 구분자
 *
 * @param modifier [Modifier]
 */
@Composable
private fun PolicyDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = PeekrTheme.colorScheme.lineDivider,
        thickness = 0.5.dp,
    )
}

// VersionInfoScreen
private val TopPaddingDp = 100.dp
private val VersionItemGapDp = 118.dp

// LogoAndVersion
private val LogoGapDp = 20.dp
private const val LOGO_WIDTH = 85

// PolicyLink
private val LinkPaddingDp = 20.dp

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun LogoAndVersionPreview() {
    PeekrAppTheme {
        LogoAndVersion(versionName = "v1.0.0")
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun PolicyLinkPreview() {
    PeekrAppTheme {
        PolicyLink(
            modifier = Modifier.fillMaxWidth(),
            text = "서비스 이용약관",
            onClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun PolicyLinkGroupPreview() {
    PeekrAppTheme {
        PolicyLinkGroup(
            modifier = Modifier.fillMaxWidth(),
            onServiceTermClick = {},
            onPrivacyPolicyClick = {},
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun VersionInfoScreenPreview() {
    PeekrAppTheme {
        VersionInfoScreen(
            modifier = Modifier.fillMaxSize(),
            versionName = "v1.0.0",
            onServiceTermClick = {},
            onPrivacyPolicyClick = {},
            onBackPressed = {},
        )
    }
}

package com.turnin.core.designsystem.component.logo

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.R

/**
 * Turnin Logo 유형
 *
 * @param lightRes 로고 리소스 (라이트 모드)
 * @param darkRes 로고 리소스 (다크 모드)
 * @param ratio 1:N 비율 (N == [ratio])
 */
enum class TurninLogoType(
    @field:DrawableRes val lightRes: Int,
    @field:DrawableRes val darkRes: Int,
    val ratio: Double,
) {
    Icon(lightRes = R.drawable.logo_icon, darkRes = R.drawable.logo_icon, ratio = 1.24),
    Text(lightRes = R.drawable.logo_text_light, darkRes = R.drawable.logo_text_dark, ratio = 0.239),
    App(lightRes = R.drawable.logo_app, darkRes = R.drawable.logo_app, ratio = 1.0),
}

/**
 * Turnin Logo
 *
 * [logoWidth]는 가로 길이를 기준으로 비율을 곱해서 세로 길이를 자동 측정하기 때문에
 * 결국 [logoWidth]는 로고의 사이즈를 결정하게 된다.
 *
 * 또한, [logoWidth]는 **DP** 단위 기준이지만 숫자만 입력하면 된다.
 *
 * @param logoType [TurninLogoType]
 * @param logoWidth 로고 가로 길이
 * @param modifier [Modifier]
 */
@Composable
fun TurninLogo(
    logoType: TurninLogoType,
    logoWidth: Int,
    modifier: Modifier = Modifier,
) {
    val logoRes = if (isSystemInDarkTheme()) logoType.darkRes else logoType.lightRes

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            modifier = Modifier
                .width(logoWidth.dp)
                .height((logoWidth * logoType.ratio).dp),
            imageVector = ImageVector.vectorResource(logoRes),
            contentDescription = stringResource(R.string.logo_content_desc),
        )
    }
}

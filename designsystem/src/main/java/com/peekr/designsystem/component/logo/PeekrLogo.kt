package com.peekr.designsystem.component.logo

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import com.peekr.designsystem.R

/**
 * Peekr Logo 유형
 *
 * @param res 로고 리소스
 * @param ratio 1:N 비율 (N == [ratio])
 */
enum class PeekrLogoType(
    @DrawableRes val res: Int,
    val ratio: Double,
) {
    Default(res = R.drawable.logo_default, ratio = 1.4),
    Text(res = R.drawable.logo_text, ratio = 0.3),
    Icon(res = R.drawable.logo_icon, ratio = 1.0),
}

/**
 * Peekr Logo
 *
 * [logoWidth]는 가로 길이를 기준으로 비율을 곱해서 세로 길이를 자동 측정하기 때문에
 * 결국 [logoWidth]는 로고의 사이즈를 결정하게 된다.
 *
 * 또한, [logoWidth]는 **DP** 단위 기준이지만 숫자만 입력하면 된다.
 *
 * @param logoType [PeekrLogoType]
 * @param logoWidth 로고 가로 길이
 * @param modifier [Modifier]
 */
@Composable
fun PeekrLogo(
    logoType: PeekrLogoType,
    logoWidth: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Image(
            modifier = Modifier
                .width(logoWidth.dp)
                .height((logoWidth * logoType.ratio).dp),
            imageVector = ImageVector.vectorResource(logoType.res),
            contentDescription = stringResource(R.string.logo_content_desc),
        )
    }
}

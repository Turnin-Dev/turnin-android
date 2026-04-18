package com.peekr.presentation.login.view

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.loading.PeekrLoadingScreen
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.click.clickableSingle
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.presentation.ui.component.logo.PeekrLogoWithText
import com.peekr.core.presentation.ui.model.UiSocialLoginProvider
import com.peekr.presentation.R
import com.peekr.presentation.login.state.LoginState

/**
 * 로그인 화면
 *
 * @param modifier [Modifier]
 * @param login 로그인 시 트리거 되고 어떤 소셜로그인으로 로그인 했는지 인자로 넘긴다.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginState: LoginState,
    login: (UiSocialLoginProvider) -> Unit,
) {
    Box(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ScreenTokens.HorizontalPadding, vertical = 60.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.size(0.dp))
            PeekrLogoWithText()
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                GoogleLoginButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LoginButtonTokens.Height),
                    onClick = { login(UiSocialLoginProvider.GOOGLE) },
                )
                KakaoLoginButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(LoginButtonTokens.Height),
                    onClick = { login(UiSocialLoginProvider.KAKAO) },
                )
            }
        }

        if (loginState.loading) {
            PeekrLoadingScreen()
        }
    }
}

@Composable
private fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CommonLoginButton(
        modifier = modifier,
        icon = R.drawable.google,
        text = R.string.login_screen_btn_google,
        color = Color.White,
        borderColor = Color(0xFF747775),
        onClick = onClick,
    )
}

@Composable
private fun KakaoLoginButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    CommonLoginButton(
        modifier = modifier,
        icon = R.drawable.kakao,
        text = R.string.login_screen_btn_kakao,
        color = LoginButtonTokens.KakaoColor,
        onClick = onClick,
    )
}

@Composable
private fun CommonLoginButton(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    color: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Transparent,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(LoginButtonTokens.Shape)
            .border(1.dp, borderColor, LoginButtonTokens.Shape)
            .background(color, LoginButtonTokens.Shape)
            .clickableSingle(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LoginButtonTokens.InnerPaddingValues),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentSize(Alignment.Center),
                text = stringResource(text),
                style = PeekrTheme.typography.body2.copy(fontWeight = FontWeight.Medium),
            )
        }
    }
}

/** 로그인 버튼에 사용하는 토큰 값들 */
private object LoginButtonTokens {
    /** 로그인 버튼 내부 Horizontal 패딩 */
    val InnerPaddingValues = PaddingValues(horizontal = 24.dp, vertical = 10.dp)

    /** 로그인 버튼 모양 */
    val Shape = RoundedCornerShape(20.dp)

    /** 로그인 버튼 높이 */
    val Height = 48.dp

    /** 카카오 색상 */
    val KakaoColor = Color(0xFFFEE500)
}

@Preview
@Composable
private fun LoginScreenPreview() {
    PeekrAppTheme {
        LoginScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            loginState = LoginState(),
            login = {},
        )
    }
}

@Preview
@Composable
private fun LoginButtonPreview() {
    PeekrAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
            GoogleLoginButton(
                modifier = Modifier
                    .width(300.dp)
                    .height(LoginButtonTokens.Height),
                onClick = {},
            )
            KakaoLoginButton(
                modifier = Modifier
                    .width(300.dp)
                    .height(LoginButtonTokens.Height),
                onClick = {},
            )
        }
    }
}

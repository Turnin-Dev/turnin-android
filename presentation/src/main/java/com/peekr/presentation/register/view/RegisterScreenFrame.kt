package com.peekr.presentation.register.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.component.button.PeekrButtonStyle
import com.peekr.designsystem.component.button.PeekrSolidButton
import com.peekr.designsystem.component.topbar.PeekrTopBar
import com.peekr.designsystem.theme.PeekrAppTheme
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.presentation.R
import com.peekr.presentation.shared.util.ScreenTokens
import com.peekr.presentation.shared.util.bottomAutoPadding

/**
 * 회원가입 화면 공통 프레임
 *
 * @param modifier [Modifier]
 * @param title 회원가입 화면 타이틀
 * @param placeholder 회원가입 텍스트필드의 자리표시자
 * @param text 회원가입 텍스트필드의 텍스트
 * @param onTextChanged 회원가입 텍스트필드의 텍스트 콜백
 * @param errorMessage 회원가입 텍스트필드의 에러 메시지
 * @param onBackPressed 뒤로가기 클릭 시 수행할 작업
 * @param onNextWithValue 입력한 정보와 함께 다음 버튼 클릭 시 수행할 작업
 */
@Composable
fun RegisterScreenFrame(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes subTitle: Int? = null,
    @StringRes placeholder: Int,
    text: String,
    onTextChanged: (String) -> Unit,
    errorMessage: String?,
    enabledNext: Boolean,
    onBackPressed: (() -> Unit)? = null,
    onNextWithValue: (String) -> Unit,
) {
    val bottomPadding = bottomAutoPadding()

    // 회원가입 화면
    Column(modifier = modifier.background(PeekrTheme.colorScheme.backgroundNormal)) {
        // 탑바
        PeekrTopBar(
            modifier = Modifier.fillMaxWidth(),
            onBackPressed = { onBackPressed?.invoke() },
        )

        Spacer(Modifier.height(TopToContentSpacer))

        // 메인 컨텐츠 & 하단 버튼
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = ScreenTokens.HorizontalPadding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // 메인 컨텐츠
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(ContentVerticalSpacing),
            ) {
                // 타이틀
                Column {
                    Text(
                        text = stringResource(title),
                        style = PeekrTheme.typography.title1,
                        fontWeight = FontWeight.Bold,
                        color = PeekrTheme.colorScheme.textNormal,
                    )
                    subTitle?.let {
                        Text(
                            text = stringResource(subTitle),
                            style = PeekrTheme.typography.label2,
                            color = PeekrTheme.colorScheme.textAssist2,
                        )
                    }
                }

                // 회원가입 텍스트필드
                RegisterTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    onTextChanged = onTextChanged,
                    placeholder = stringResource(placeholder),
                    errorMessage = errorMessage,
                )
            }

            // 하단 버튼
            PeekrSolidButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = bottomPadding),
                text = stringResource(R.string.register_screen_btn_next),
                style = PeekrButtonStyle.Large,
                onClick = { onNextWithValue(text) },
                enabled = enabledNext,
            )
        }
    }
}

/** 탑바랑 메인 컨텐츠 사이 간격 */
private val TopToContentSpacer = 64.dp

/** 메인 컨텐츠 수직 간격 */
private val ContentVerticalSpacing = 74.dp

@Preview(showBackground = true)
@Composable
private fun RegisterScreenFramePreview() {
    val (text, onTextChanged) = remember { mutableStateOf("") }

    PeekrAppTheme {
        RegisterScreenFrame(
            modifier = Modifier.fillMaxSize(),
            title = R.string.register_screen_name_title,
            subTitle = R.string.register_screen_name_sub_title,
            placeholder = R.string.register_screen_name_placeholder,
            text = text,
            onTextChanged = onTextChanged,
            errorMessage = null,
            enabledNext = true,
            onBackPressed = {},
            onNextWithValue = { },
        )
    }
}

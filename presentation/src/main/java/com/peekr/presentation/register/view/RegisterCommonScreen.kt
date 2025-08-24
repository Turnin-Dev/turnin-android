package com.peekr.presentation.register.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.peekr.designsystem.component.avatar.PeekrAvatar
import com.peekr.designsystem.component.button.PeekrButtonStyle
import com.peekr.designsystem.component.button.PeekrSolidButton
import com.peekr.designsystem.component.topbar.PeekrTopBar
import com.peekr.designsystem.theme.PeekrAppTheme
import com.peekr.designsystem.theme.PeekrTheme
import com.peekr.presentation.R
import com.peekr.presentation.shared.util.ScreenTokens
import com.peekr.presentation.shared.util.bottomAutoPadding

/**
 * 회원가입 공통 화면
 *
 * @param modifier [Modifier]
 * @param title 회원가입 화면 타이틀
 * @param subTitle 회원가입 화면 보조 타이틀
 * @param placeholder 회원가입 텍스트필드의 자리표시자
 * @param text 회원가입 텍스트필드의 텍스트
 * @param onTextChanged 회원가입 텍스트필드의 텍스트 콜백
 * @param errorMessage 회원가입 텍스트필드의 에러 메시지
 * @param enabledNext 다음 버튼 활성화
 * @param onBackPressed 뒤로가기 클릭 시 수행할 작업
 * @param onNextWithValue 입력한 정보와 함께 다음 버튼 클릭 시 수행할 작업
 */
@Composable
fun RegisterCommonScreen(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes placeholder: Int,
    text: String,
    errorMessage: String?,
    enabledNext: Boolean,
    @StringRes subTitle: Int? = null,
    profileImage: ImageBitmap? = null,
    @StringRes buttonTitle: Int = R.string.register_screen_btn_next,
    onTextChanged: (String) -> Unit,
    onNextWithValue: (String) -> Unit,
    onBackPressed: (() -> Unit)? = null,
    onProfileImageClick: (() -> Unit)? = null,
) {
    val bottomPadding = bottomAutoPadding()

    RegisterScreenFrame(
        modifier = modifier,
        topBar = {
            PeekrTopBar(
                modifier = Modifier.fillMaxWidth(),
                onBackPressed = { onBackPressed?.invoke() },
            )
        },
        contents = {
            Contents(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = TopToContentSpacer),
                title = title,
                placeholder = placeholder,
                text = text,
                errorMessage = errorMessage,
                subTitle = subTitle,
                profileImage = profileImage,
                onTextChanged = onTextChanged,
                onProfileImageClick = onProfileImageClick,
                bottomButton = {
                    PeekrSolidButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = bottomPadding, top = ScreenTokens.BottomButtonPadding),
                        text = stringResource(buttonTitle),
                        style = PeekrButtonStyle.Large,
                        onClick = { onNextWithValue(text) },
                        enabled = enabledNext,
                    )
                },
            )
        },
    )
}

/**
 * 회원가입 공통 화면 - 프레임
 *
 * @param topBar 탑바
 * @param contents 메인 컨텐츠
 */
@Composable
private fun RegisterScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    contents: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        topBar()
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = ScreenTokens.HorizontalPadding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            contents()
        }
    }
}

/** 회원가입 공통 화면 - 메인 컨텐츠 */
@Composable
private fun Contents(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes placeholder: Int,
    text: String,
    errorMessage: String?,
    @StringRes subTitle: Int? = null,
    profileImage: ImageBitmap? = null,
    onTextChanged: (String) -> Unit,
    onProfileImageClick: (() -> Unit)? = null,
    bottomButton: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // 메인 컨텐츠
        Column(verticalArrangement = Arrangement.spacedBy(ContentVerticalSpacing)) {
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

            Column(verticalArrangement = Arrangement.spacedBy(ProfileImageTextFieldSpacing)) {
                // 프로필 사진
                onProfileImageClick?.let {
                    PeekrAvatar(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(ProfileImageSize),
                        model = profileImage,
                        contentDescription = null,
                        onClick = it,
                    )
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
        }

        bottomButton()
    }
}

/** 탑바랑 메인 컨텐츠 사이 간격 */
private val TopToContentSpacer = 64.dp

/** 메인 컨텐츠 수직 간격 */
private val ContentVerticalSpacing = 74.dp

/** 프로필 사진 사이즈 */
private val ProfileImageSize = 135.dp

/** 프로필 사진과 텍스트 필드 사이 간격 */
private val ProfileImageTextFieldSpacing = 37.dp

@Preview(showBackground = true)
@Composable
private fun RegisterScreenFramePreview() {
    val (text, onTextChanged) = remember { mutableStateOf("") }

    PeekrAppTheme {
        RegisterCommonScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
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

package com.turnin.presentation.register.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.avatar.TurninAvatar
import com.turnin.core.designsystem.component.button.TurninButtonStyle
import com.turnin.core.designsystem.component.button.TurninSolidButton
import com.turnin.core.designsystem.component.topbar.TurninTopBar
import com.turnin.core.designsystem.theme.TurninAppTheme
import com.turnin.core.designsystem.theme.TurninTheme
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.presentation.R

/**
 * 회원가입 공통 화면
 *
 * @param modifier [Modifier]
 * @param title 회원가입 화면 타이틀
 * @param placeholder 회원가입 텍스트필드의 자리표시자
 * @param text 회원가입 텍스트필드의 텍스트
 * @param errorMessage 회원가입 텍스트필드의 에러 메시지
 * @param loading 로딩 여부
 * @param enabledNext 다음 버튼 활성화
 * @param singleLine 텍스트필드의 싱글 라인 여부
 * @param subTitle 회원가입 화면 보조 타이틀
 * @param profileImage 프로필 이미지
 * @param buttonTitle 버튼 타이틀
 * @param onTextChanged 회원가입 텍스트필드의 텍스트 콜백
 * @param onNextWithValue 입력한 정보와 함께 다음 버튼 클릭 시 수행할 작업
 * @param onBackPressed 뒤로가기 클릭 시 수행할 작업
 * @param onProfileImageClick 프로필 이미지 클릭 시 수행할 작업
 */
@Composable
fun RegisterCommonScreen(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes placeholder: Int,
    text: String,
    errorMessage: String?,
    loading: Boolean,
    enabledNext: Boolean,
    singleLine: Boolean = true,
    @StringRes subTitle: Int? = null,
    profileImage: ImageBitmap? = null,
    @StringRes buttonTitle: Int = R.string.register_screen_btn_next,
    onTextChanged: (String) -> Unit,
    onNextWithValue: (String) -> Unit,
    onBackPressed: (() -> Unit)? = null,
    onProfileImageClick: (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current

    RegisterScreenFrame(
        modifier = modifier,
        topBar = {
            TurninTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                onBackPressed = onBackPressed,
            )
        },
        contents = {
            Contents(
                title = title,
                placeholder = placeholder,
                text = text,
                errorMessage = errorMessage,
                singleLine = singleLine,
                subTitle = subTitle,
                profileImage = profileImage,
                onTextChanged = onTextChanged,
                onProfileImageClick = onProfileImageClick,
            )
        },
        bottomButton = {
            TurninSolidButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(
                        bottom = ScreenTokens.BottomButtonPadding,
                        top = ScreenTokens.BottomButtonPadding,
                    ),
                text = stringResource(buttonTitle),
                style = TurninButtonStyle.Large,
                onClick = {
                    focusManager.clearFocus()
                    onNextWithValue(text)
                },
                enabled = enabledNext,
                loading = loading,
            )
        },
    )
}

/**
 * 회원가입 공통 화면 - 프레임
 *
 * @param topBar 탑바
 * @param contents 메인 컨텐츠
 * @param bottomButton 하단 버튼
 */
@Composable
private fun RegisterScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    contents: @Composable ColumnScope.() -> Unit,
    bottomButton: @Composable BoxScope.() -> Unit,
) {
    Column(modifier = modifier) {
        topBar()

        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = ScreenTokens.HorizontalPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        top = TopToContentSpacer,
                        bottom = TopToContentSpacer + ScreenTokens.BottomButtonPadding * 2,
                    ),
            ) {
                contents()
            }

            bottomButton()
        }
    }
}

/**
 * 회원가입 공통 화면 - 메인 컨텐츠
 *
 * @param modifier [Modifier]
 * @param title 화면 타이틀
 * @param placeholder 텍스트필드 자리표시자
 * @param text 텍스트필드 텍스트
 * @param errorMessage 텍스트필드 에러 메시지
 * @param singleLine 텍스트필드 싱글 라인 여부
 * @param subTitle 화면 보조 타이틀
 * @param profileImage 프로필 이미지
 * @param onTextChanged 텍스트필드의 텍스트 콜백
 * @param onProfileImageClick 프로필 이미지 클릭 시 수행할 작업
 */
@Composable
private fun Contents(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    @StringRes placeholder: Int,
    text: String,
    errorMessage: String?,
    singleLine: Boolean,
    @StringRes subTitle: Int? = null,
    profileImage: ImageBitmap? = null,
    onTextChanged: (String) -> Unit,
    onProfileImageClick: (() -> Unit)? = null,
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
                    style = TurninTheme.typography.title1,
                    fontWeight = FontWeight.Bold,
                    color = TurninTheme.colorScheme.textNormal,
                )
                subTitle?.let {
                    Text(
                        text = stringResource(subTitle),
                        style = TurninTheme.typography.label2,
                        color = TurninTheme.colorScheme.textAssist2,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(ProfileImageTextFieldSpacing)) {
                // 프로필 사진
                onProfileImageClick?.let {
                    TurninAvatar(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(ProfileImageSize),
                        model = profileImage,
                        contentDescription = stringResource(R.string.register_screen_profile_desc_avatar),
                        onClick = onProfileImageClick,
                    )
                }

                // 회원가입 텍스트필드
                RegisterTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    onTextChanged = onTextChanged,
                    placeholder = stringResource(placeholder),
                    errorMessage = errorMessage,
                    singleLine = singleLine,
                )
            }
        }
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

// ------------------------------ Preview ------------------------------

@Preview(showBackground = true)
@Composable
private fun RegisterScreenFramePreview() {
    val (text, onTextChanged) = remember { mutableStateOf("") }

    TurninAppTheme {
        RegisterCommonScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(TurninTheme.colorScheme.backgroundNormal),
            title = R.string.register_screen_name_title,
            subTitle = R.string.register_screen_name_sub_title,
            placeholder = R.string.register_screen_name_placeholder,
            text = text,
            onTextChanged = onTextChanged,
            errorMessage = null,
            singleLine = true,
            loading = false,
            enabledNext = true,
            onBackPressed = {},
            onNextWithValue = { },
        )
    }
}

package com.peekr.presentation.setting.view.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldLabelPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.peekr.core.designsystem.component.avatar.PeekrAvatar
import com.peekr.core.designsystem.component.button.PeekrIconButton
import com.peekr.core.designsystem.component.icon.PeekrIconSize
import com.peekr.core.designsystem.component.topbar.PeekrTopBar
import com.peekr.core.designsystem.theme.PeekrAppTheme
import com.peekr.core.designsystem.theme.PeekrTheme
import com.peekr.core.designsystem.util.icon.Check
import com.peekr.core.designsystem.util.icon.Edit
import com.peekr.core.designsystem.util.icon.PeekrIcons
import com.peekr.core.designsystem.util.token.ScreenTokens
import com.peekr.core.domain.common.validation.ValidationErrorType
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.peekr.domain.setting.error.SettingErrorType
import com.peekr.presentation.R
import com.peekr.presentation.setting.error.asUiText
import com.peekr.presentation.setting.model.UiAccountInfo
import com.peekr.presentation.setting.state.SettingContract

/**
 * 계정 정보 화면 프레임
 *
 * @param modifier [Modifier]
 * @param topBar 탑바
 * @param profileImage 프로필 사진
 * @param displayIdTextField 사용자 표시 ID 텍스트 필드
 * @param nameTextField 이름 텍스트 필드
 * @param introduceTextField 소개글 텍스트 필드
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccountInfoScreenFrame(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    profileImage: @Composable () -> Unit,
    displayIdTextField: @Composable () -> Unit,
    nameTextField: @Composable () -> Unit,
    introduceTextField: @Composable () -> Unit,
) {
    Column(modifier) {
        topBar()
        Column(
            Modifier
                .weight(1f)
                .imePadding()
                .verticalScroll(rememberScrollState()),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = ProfileImageVerticalPadding),
                contentAlignment = Alignment.Center,
            ) {
                profileImage()
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPadding),
            ) {
                displayIdTextField()
                nameTextField()
                introduceTextField()
            }
        }
    }
}

/**
 * 계정 정보 화면
 *
 * @param modifier [Modifier]
 * @param accountInfo 계정 정보
 * @param isAccountInfoEdited 계정 정보 수정 여부
 * @param displayIdState 사용자 표시 ID 상태
 * @param isDisplayIdValid 사용자 표시 ID 유효성 검사 결과
 * @param nameState 사용자 명 상태
 * @param isNameValid 사용자 명 유효성 검사 결과
 * @param introduceState 소개글 상태
 * @param isIntroduceValid 사용자 표시 ID 유효성 검사 결과
 * @param onBackPressed 뒤로 가기 시 콜백
 */
@Composable
fun AccountInfoScreen(
    modifier: Modifier = Modifier,
    accountInfo: UiAccountInfo?,
    isAccountInfoEdited: Boolean,
    displayIdState: TextFieldState,
    isDisplayIdValid: ValidationResult<DisplayId, SettingErrorType>,
    nameState: TextFieldState,
    isNameValid: ValidationResult<Name, ValidationErrorType>,
    introduceState: TextFieldState,
    isIntroduceValid: ValidationResult<Introduce, ValidationErrorType>,
    onUiEvent: (SettingContract.UiEvent) -> Unit,
    onBackPressed: () -> Unit,
) {
    AccountInfoScreenFrame(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                isAccountInfoEdited = isAccountInfoEdited,
                onSave = { onUiEvent(SettingContract.UiEvent.OnSaveAccountInfo) },
                onBackPressed = onBackPressed,
            )
        },
        profileImage = {
            ProfileImage(
                profileImageUrl = accountInfo?.profileImageUrl,
                name = accountInfo?.name,
            )
        },
        displayIdTextField = {
            DisplayIdTextField(
                modifier = Modifier.fillMaxWidth(),
                displayIdState = displayIdState,
                isDisplayIdValid = isDisplayIdValid,
            )
        },
        nameTextField = {
            NameTextField(
                modifier = Modifier.fillMaxWidth(),
                nameState = nameState,
                isNameValid = isNameValid,
            )
        },
        introduceTextField = {
            IntroduceTextField(
                modifier = Modifier.fillMaxWidth(),
                introduceState = introduceState,
                isIntroduceValid = isIntroduceValid,
            )
        },
    )
}

/**
 * 탑바
 *
 * @param modifier [Modifier]
 * @param isAccountInfoEdited 계정 정보 수정 여부
 * @param onSave 저장 클릭 시 콜백
 * @param onBackPressed 뒤로 가기 클릭 시 콜백
 */
@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    isAccountInfoEdited: Boolean,
    onSave: () -> Unit,
    onBackPressed: () -> Unit,
) {
    PeekrTopBar(
        modifier = modifier,
        title = stringResource(R.string.setting_detail_account_info_top_bar_title),
        onBackPressed = onBackPressed,
        optionSlot = {
            if (isAccountInfoEdited) {
                PeekrIconButton(
                    icon = PeekrIcons.Default.Bold.Check,
                    iconSize = PeekrIconSize.Small,
                    contentDescription = stringResource(R.string.setting_detail_account_info_top_bar_save),
                    onClick = onSave,
                    tint = PeekrTheme.colorScheme.primary,
                )
            }
        },
    )
}

/**
 * 프로필 사진
 *
 * @param modifier [Modifier]
 * @param profileImageUrl 프로필 사진 URL
 * @param name 사용자 명 (이미지 설명용)
 */
@Composable
private fun ProfileImage(
    modifier: Modifier = Modifier,
    profileImageUrl: String?,
    name: String?,
) {
    Box(modifier) {
        PeekrAvatar(
            modifier = Modifier.size(84.dp),
            model = profileImageUrl,
            contentDescription = name,
        )
        Icon(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(CircleShape)
                .size(28.dp)
                .background(PeekrTheme.colorScheme.textStrong, CircleShape)
                .padding(8.dp),
            imageVector = PeekrIcons.Outlined.Normal.Edit.imageVector,
            contentDescription = stringResource(R.string.setting_detail_account_info_profile_image_edit),
            tint = PeekrTheme.colorScheme.backgroundNormal,
        )
    }
}

/**
 * 사용자 표시 ID 텍스트 필드
 *
 * @param modifier [Modifier]
 * @param displayIdState 사용자 표시 ID 텍스트 필드 상태
 * @param isDisplayIdValid 사용자 표시 ID 유효성 검사 결과
 */
@Composable
private fun DisplayIdTextField(
    modifier: Modifier = Modifier,
    displayIdState: TextFieldState,
    isDisplayIdValid: ValidationResult<DisplayId, SettingErrorType>,
) {
    OutlinedTextField(
        modifier = modifier,
        state = displayIdState,
        isError = isDisplayIdValid is ValidationResult.Invalid,
        supportingText = {
            if (isDisplayIdValid is ValidationResult.Invalid) {
                Text(isDisplayIdValid.error.asUiText().asString())
            }
        },
        trailingIcon = {
            if (isDisplayIdValid is ValidationResult.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(15.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.5.dp,
                    color = PeekrTheme.colorScheme.primary,
                )
            }
        },
        label = { Text(stringResource(R.string.setting_detail_account_info_text_field_id_label)) },
        lineLimits = TextFieldLineLimits.SingleLine,
        labelPosition = TextFieldLabelPosition.Above(),
        colors = getTextFieldColors(),
    )
}

/**
 * 사용자 명 텍스트 필드
 *
 * @param modifier [Modifier]
 * @param nameState 사용자 명 텍스트 필드 상태
 * @param isNameValid 사용자 명 유효성 검사 결과
 */
@Composable
private fun NameTextField(
    modifier: Modifier = Modifier,
    nameState: TextFieldState,
    isNameValid: ValidationResult<Name, ValidationErrorType>,
) {
    OutlinedTextField(
        modifier = modifier,
        state = nameState,
        isError = isNameValid is ValidationResult.Invalid,
        supportingText = {
            if (isNameValid is ValidationResult.Invalid) {
                Text(isNameValid.error.asUiText().asString())
            }
        },
        label = { Text(stringResource(R.string.setting_detail_account_info_text_field_name_label)) },
        lineLimits = TextFieldLineLimits.SingleLine,
        labelPosition = TextFieldLabelPosition.Above(),
        colors = getTextFieldColors(),
    )
}

/**
 * 소개글 텍스트 필드
 *
 * @param modifier [Modifier]
 * @param introduceState 소개글 텍스트 필드 상태
 * @param isIntroduceValid 소개글 유효성 검사 결과
 */
@Composable
private fun IntroduceTextField(
    modifier: Modifier = Modifier,
    introduceState: TextFieldState,
    isIntroduceValid: ValidationResult<Introduce, ValidationErrorType>,
) {
    OutlinedTextField(
        modifier = modifier,
        state = introduceState,
        isError = isIntroduceValid is ValidationResult.Invalid,
        supportingText = {
            if (isIntroduceValid is ValidationResult.Invalid) {
                Text(isIntroduceValid.error.asUiText().asString())
            }
        },
        label = {
            Text(stringResource(R.string.setting_detail_account_info_text_field_introduce_label))
        },
        lineLimits = TextFieldLineLimits.MultiLine(),
        labelPosition = TextFieldLabelPosition.Above(),
        colors = getTextFieldColors(),
    )
}

private val ProfileImageVerticalPadding = 50.dp

@Composable
private fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
    unfocusedBorderColor = PeekrTheme.colorScheme.textNormal,
    unfocusedTextColor = PeekrTheme.colorScheme.textNormal,
    focusedBorderColor = PeekrTheme.colorScheme.textStrong,
    focusedTextColor = PeekrTheme.colorScheme.textNormal,
    focusedLabelColor = PeekrTheme.colorScheme.textNormal,
    cursorColor = PeekrTheme.colorScheme.textNormal,
    errorTextColor = PeekrTheme.colorScheme.statusNegative,
    errorCursorColor = PeekrTheme.colorScheme.statusNegative,
    errorBorderColor = PeekrTheme.colorScheme.statusNegative,
    errorSupportingTextColor = PeekrTheme.colorScheme.statusNegative,
    errorLabelColor = PeekrTheme.colorScheme.statusNegative,
)

// ------------------------------ Previews ------------------------------
@PreviewLightDarkWithBackground
@Composable
private fun ProfileImagePreview() {
    PeekrAppTheme {
        ProfileImage(
            profileImageUrl = null,
            name = "name",
        )
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun DisplayIdTextFieldPreview() {
    PeekrAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(50.dp)) {
            NameTextField(
                modifier = Modifier.fillMaxWidth(),
                nameState = TextFieldState(),
                isNameValid = ValidationResult.Valid(Name("name")),
            )
            NameTextField(
                modifier = Modifier.fillMaxWidth(),
                nameState = TextFieldState("Name"),
                isNameValid = ValidationResult.Valid(Name("name")),
            )
            NameTextField(
                modifier = Modifier.fillMaxWidth(),
                nameState = TextFieldState("Name"),
                isNameValid = ValidationResult.Invalid(ValidationErrorType.Unexpected),
            )
        }
    }
}

@PreviewLightDarkWithBackground
@Composable
private fun SettingScreenPreview() {
    PeekrAppTheme {
        AccountInfoScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PeekrTheme.colorScheme.backgroundNormal),
            accountInfo = UiAccountInfo.sample,
            isAccountInfoEdited = true,
            displayIdState = TextFieldState(UiAccountInfo.sample.displayId),
            isDisplayIdValid = ValidationResult.Valid(DisplayId("displayId")),
            nameState = TextFieldState(UiAccountInfo.sample.name),
            isNameValid = ValidationResult.Invalid(ValidationErrorType.Unexpected),
            introduceState = TextFieldState(UiAccountInfo.sample.introduce),
            isIntroduceValid = ValidationResult.Valid(Introduce("introduce")),
            onUiEvent = {},
            onBackPressed = {},
        )
    }
}

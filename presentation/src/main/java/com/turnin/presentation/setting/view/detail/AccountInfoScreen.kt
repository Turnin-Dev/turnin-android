package com.turnin.presentation.setting.view.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.turnin.core.designsystem.component.avatar.PeekrAvatar
import com.turnin.core.designsystem.component.button.PeekrIconButton
import com.turnin.core.designsystem.component.icon.PeekrIconSize
import com.turnin.core.designsystem.component.topbar.PeekrTopBar
import com.turnin.core.designsystem.theme.PeekrAppTheme
import com.turnin.core.designsystem.theme.PeekrTheme
import com.turnin.core.designsystem.util.click.clickableSingleWithoutRipple
import com.turnin.core.designsystem.util.icon.Check
import com.turnin.core.designsystem.util.icon.Edit
import com.turnin.core.designsystem.util.icon.PeekrIcons
import com.turnin.core.designsystem.util.token.ScreenTokens
import com.turnin.core.presentation.ui.util.PreviewLightDarkWithBackground
import com.turnin.presentation.R
import com.turnin.presentation.setting.model.UiEditableAccountInfo
import com.turnin.presentation.setting.state.AccountInfoContract
import com.turnin.presentation.setting.state.AccountInfoDisplayIdState
import com.turnin.presentation.setting.state.AccountInfoIntroduceState
import com.turnin.presentation.setting.state.AccountInfoNameState

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
 * @param displayIdFieldState 사용자 표시 ID 텍스트 필드 상태
 * @param nameFieldState 사용자 명 텍스트 필드 상태
 * @param introduceFieldState 소개글 텍스트 필드 상태
 * @param onUiEvent UI 이벤트 발행
 * @param onProfileImageClick 프로필 사진 클릭 시 콜백
 */
@Composable
fun AccountInfoScreen(
    modifier: Modifier = Modifier,
    accountInfo: UiEditableAccountInfo?,
    localProfileImage: ByteArray?,
    isAccountInfoEdited: Boolean,
    displayIdFieldState: AccountInfoDisplayIdState,
    nameFieldState: AccountInfoNameState,
    introduceFieldState: AccountInfoIntroduceState,
    onUiEvent: (AccountInfoContract.UiEvent) -> Unit,
    onProfileImageClick: () -> Unit,
) {
    AccountInfoScreenFrame(
        modifier = modifier,
        topBar = {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenTokens.HorizontalPaddingWithTouchTarget),
                isAccountInfoEdited = isAccountInfoEdited,
                onSave = { onUiEvent(AccountInfoContract.UiEvent.OnSaveAccountInfo) },
                onBackPressed = { onUiEvent(AccountInfoContract.UiEvent.SafeBackPressed) },
            )
        },
        profileImage = {
            ProfileImage(
                profileImageUrl = localProfileImage ?: accountInfo?.profileImageUrl,
                name = accountInfo?.name,
                onClick = onProfileImageClick,
            )
        },
        displayIdTextField = {
            DisplayIdTextField(
                modifier = Modifier.fillMaxWidth(),
                displayIdState = displayIdFieldState,
                onDisplayIdChanged = {
                    onUiEvent(AccountInfoContract.UiEvent.OnDisplayIdChanged(it))
                },
            )
        },
        nameTextField = {
            NameTextField(
                modifier = Modifier.fillMaxWidth(),
                nameState = nameFieldState,
                onNameChanged = {
                    onUiEvent(AccountInfoContract.UiEvent.OnNameChanged(it))
                },
            )
        },
        introduceTextField = {
            IntroduceTextField(
                modifier = Modifier.fillMaxWidth(),
                introduceState = introduceFieldState,
                onIntroduceChanged = {
                    onUiEvent(AccountInfoContract.UiEvent.OnIntroduceChanged(it))
                },
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
                    iconSize = PeekrIconSize.Normal,
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
 * @param profileImageUrl 프로필 사진
 * @param name 사용자 명 (이미지 설명용)
 * @param onClick 프로필 사진 클릭 시 콜백
 */
@Composable
private fun ProfileImage(
    modifier: Modifier = Modifier,
    profileImageUrl: Any?,
    name: String?,
    onClick: () -> Unit,
) {
    Box(modifier.clickableSingleWithoutRipple(onClick = onClick)) {
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
 * @param onDisplayIdChanged 사용자 표시 ID 변경 시 콜백
 */
@Composable
private fun DisplayIdTextField(
    modifier: Modifier = Modifier,
    displayIdState: AccountInfoDisplayIdState,
    onDisplayIdChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = displayIdState.displayId,
        onValueChange = onDisplayIdChanged,
        isError = displayIdState.displayIdError != null,
        supportingText = {
            if (displayIdState.displayIdError != null) {
                Text(displayIdState.displayIdError.asString())
            }
        },
        trailingIcon = {
            if (displayIdState.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(15.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 2.5.dp,
                    color = PeekrTheme.colorScheme.primary,
                )
            }
        },
        label = { Text(stringResource(R.string.setting_detail_account_info_text_field_id_label)) },
        singleLine = true,
        colors = getTextFieldColors(),
    )
}

/**
 * 사용자 명 텍스트 필드
 *
 * @param modifier [Modifier]
 * @param nameState 사용자 명 텍스트 필드 상태
 * @param onNameChanged 사용자 명 변경 시 콜백
 */
@Composable
private fun NameTextField(
    modifier: Modifier = Modifier,
    nameState: AccountInfoNameState,
    onNameChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = nameState.name,
        onValueChange = onNameChanged,
        isError = nameState.nameError != null,
        supportingText = {
            if (nameState.nameError != null) {
                Text(nameState.nameError.asString())
            }
        },
        label = { Text(stringResource(R.string.setting_detail_account_info_text_field_name_label)) },
        singleLine = true,
        colors = getTextFieldColors(),
    )
}

/**
 * 소개글 텍스트 필드
 *
 * @param modifier [Modifier]
 * @param introduceState 소개글 텍스트 필드 상태
 * @param onIntroduceChanged 소개글 변경 시 콜백
 */
@Composable
private fun IntroduceTextField(
    modifier: Modifier = Modifier,
    introduceState: AccountInfoIntroduceState,
    onIntroduceChanged: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = introduceState.introduce,
        onValueChange = onIntroduceChanged,
        isError = introduceState.introduceError != null,
        supportingText = {
            if (introduceState.introduceError != null) {
                Text(introduceState.introduceError.asString())
            }
        },
        label = {
            Text(stringResource(R.string.setting_detail_account_info_text_field_introduce_label))
        },
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
            onClick = {},
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
                nameState = AccountInfoNameState(),
                onNameChanged = {},
            )
            NameTextField(
                modifier = Modifier.fillMaxWidth(),
                nameState = AccountInfoNameState(),
                onNameChanged = {},
            )
            NameTextField(
                modifier = Modifier.fillMaxWidth(),
                nameState = AccountInfoNameState(),
                onNameChanged = {},
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
            accountInfo = UiEditableAccountInfo.sample,
            localProfileImage = null,
            isAccountInfoEdited = true,
            displayIdFieldState = AccountInfoDisplayIdState(),
            nameFieldState = AccountInfoNameState(),
            introduceFieldState = AccountInfoIntroduceState(),
            onUiEvent = {},
            onProfileImageClick = {},
        )
    }
}

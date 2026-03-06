package com.peekr.presentation.setting.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.setting.error.SettingErrorType
import com.peekr.domain.setting.model.SettingProfileImagePatch
import com.peekr.domain.setting.usecase.AccountInfoUseCases
import com.peekr.presentation.R
import com.peekr.presentation.setting.error.asUiText
import com.peekr.presentation.setting.model.UiEditableAccountInfo
import com.peekr.presentation.setting.state.AccountInfoContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class AccountInfoViewModel @Inject constructor(
    private val usecases: AccountInfoUseCases,
    private val snackbarController: SnackbarController,
    private val savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<AccountInfoContract.UiState, AccountInfoContract.UiEvent, AccountInfoContract.UiEffect>() {
    override fun createInitialState(): AccountInfoContract.UiState =
        AccountInfoContract.UiState(
//            accountInfo = UiEditableAccountInfo(
//                displayId = savedStateHandle.get<String>("displayId") ?: "",
//                name = savedStateHandle.get<String>("name") ?: "",
//                introduce = savedStateHandle.get<String>("introduce") ?: "",
//                profileImageUrl = savedStateHandle.get<String>("profileImageUrl"),
//            ),
        )

    private val initialDisplayId: String? = savedStateHandle.get<String>("displayId")
    private val initialName: String? = savedStateHandle.get<String>("name")
    private val initialIntroduce: String? = savedStateHandle.get<String>("introduce")
    private val initialProfileImageUrl: String? = savedStateHandle.get<String>("profileImageUrl")

    val displayIdState = TextFieldState()
    val nameState = TextFieldState()
    val introduceState = TextFieldState()

    var isDisplayIdState by mutableStateOf<ValidationResult<DisplayId, SettingErrorType>>(
        if (initialDisplayId != null) {
            ValidationResult.Valid(DisplayId(initialDisplayId))
        } else {
            ValidationResult.Loading
        },
    )
        private set
    val isNameValid by derivedStateOf {
        usecases.validateName(nameState.text.toString())
    }
    val isIntroduceValid by derivedStateOf {
        usecases.validateIntroduce(introduceState.text.toString())
    }

    private val _localProfileImage = MutableStateFlow<ByteArray?>(null)

    /** 오로지 UI 표시용으로만 사용된다. */
    val localProfileImage = _localProfileImage.asStateFlow()

    init {
        // 넘어온 인자 값 체크
        if (initialDisplayId == null || initialName == null || initialIntroduce == null) {
//            sendEffect { AccountInfoContract.UiEffect.CloseScreen }
            // TODO: 일시적으로 정보를 불러올 수 없다는 에러 문구 표시
        } else {
            updateState {
                copy(
                    accountInfo = UiEditableAccountInfo(
                        displayId = initialDisplayId,
                        name = initialName,
                        introduce = initialIntroduce,
                        profileImageUrl = initialProfileImageUrl,
                    ),
                )
            }

            // TextFieldState 초기값 설정
            displayIdState.setTextAndPlaceCursorAtEnd(initialDisplayId)
            nameState.setTextAndPlaceCursorAtEnd(initialName)
            introduceState.setTextAndPlaceCursorAtEnd(initialIntroduce)

            observeDisplayIdValidation()
            observeTextFieldChanges()
        }
    }

    override suspend fun handleEvent(event: AccountInfoContract.UiEvent) {
        when (event) {
            AccountInfoContract.UiEvent.OnSaveAccountInfo -> saveAccountInfo()
            is AccountInfoContract.UiEvent.OnProfileImageUpdated -> updateProfileImage(event.imageBytes)
            AccountInfoContract.UiEvent.OnProfileImageDeleted -> deleteProfileImage()
        }
    }

    // 사용자 표시 ID 유효성 검사
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeDisplayIdValidation() {
        snapshotFlow { displayIdState.text.toString() }
            .distinctUntilChanged()
            .debounce(300)
            .flatMapLatest { text ->
                when {
                    text.isBlank() -> flowOf(ValidationResult.Loading)
                    text == initialDisplayId -> flowOf(ValidationResult.Valid(DisplayId(text)))
                    else -> usecases.validateDisplayId(text)
                }
            }
            .onEach { isDisplayIdState = it }
            .launchIn(viewModelScope)
    }

    // 텍스트 필드 값을 관찰하여 상태를 업데이트하고 정보가 수정됐는지 판단한다.
    private fun observeTextFieldChanges() {
        combine(
            snapshotFlow { displayIdState.text.toString() },
            snapshotFlow { nameState.text.toString() },
            snapshotFlow { introduceState.text.toString() },
        ) { displayId, name, introduce ->
            Triple(displayId, name, introduce)
        }
            .onEach { (displayId, name, introduce) ->
                updateState {
                    copy(
                        accountInfo = accountInfo?.copy(
                            displayId = displayId,
                            name = name,
                            introduce = introduce,
                        ),
                        isAccountInfoEdited = checkAccountInfoEdited(displayId, name, introduce),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // 계정 정보 저장
    private fun saveAccountInfo() {
        val accountInfo = currentUiState.accountInfo ?: return

        // 1. isAccountInfoEdited 가 true 인지 확인
        // 2. 모든 텍스트 필드 유효성 검사 상태가 Valid 인지 확인
        // 3. 위 과정이 모두 완료되면 최종적으로 계정 정보 저장 수행
        if (currentUiState.isAccountInfoEdited &&
            isDisplayIdState is ValidationResult.Valid &&
            isNameValid is ValidationResult.Valid &&
            isIntroduceValid is ValidationResult.Valid
        ) {
            usecases.updateAccountInfo(
                displayId = accountInfo.displayId,
                name = accountInfo.name,
                introduce = accountInfo.introduce,
                oldProfileImageUrl = initialProfileImageUrl,
                profileImagePatch = currentUiState.profileImagePatch,
            ).onEach { result ->
                when (result) {
                    Result.Loading -> updateState { copy(fullScreenLoading = true) }
                    is Result.Error -> showSnackbar(result.error.asUiText())
                    is Result.Success -> {
                        showSnackbar(UiText.StringResource(R.string.setting_success_update_account_info))
                        updateState {
                            copy(
                                profileImagePatch = SettingProfileImagePatch.Unchanged,
                                isAccountInfoEdited = false,
                                error = null,
                            )
                        }
                    }
                }
            }
                .onCompletion { updateState { copy(fullScreenLoading = false) } }
                .launchIn(viewModelScope)
        }
    }

    // 프로필 사진 업데이트
    // 업데이트 시에는 ByteArray만 가지고 있다가
    // 계정 정보 저장 시 파일명과 함께 ImageFileDetail을 만들어 저장 폼에 포함시킨다.
    private fun updateProfileImage(imageBytes: ByteArray) {
        _localProfileImage.update { imageBytes }
        updateState {
            copy(
                profileImagePatch = SettingProfileImagePatch.Update(imageBytes),
                isAccountInfoEdited = true,
            )
        }
    }

    // 프로필 사진 삭제
    private fun deleteProfileImage() {
        _localProfileImage.update { null }
        updateState {
            copy(
                accountInfo = accountInfo?.copy(profileImageUrl = null),
                profileImagePatch = SettingProfileImagePatch.Remove,
                isAccountInfoEdited = checkAccountInfoEdited(profileImageUrl = null),
            )
        }
    }

    // 계정 정보 변경 여부 확인
    private fun checkAccountInfoEdited(
        displayId: String = currentUiState.accountInfo?.displayId ?: "",
        name: String = currentUiState.accountInfo?.name ?: "",
        introduce: String = currentUiState.accountInfo?.introduce ?: "",
        profileImageUrl: String? = currentUiState.accountInfo?.profileImageUrl,
    ): Boolean =
        initialDisplayId != displayId ||
            initialName != name ||
            initialIntroduce != introduce ||
            initialProfileImageUrl != profileImageUrl

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}

package com.peekr.presentation.setting.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.common.snackbar.SnackbarController
import com.peekr.core.presentation.common.snackbar.SnackbarEvent
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.core.presentation.common.viewmodel.setTextFieldValidation
import com.peekr.core.presentation.ui.util.UiText
import com.peekr.domain.setting.model.SettingProfileImagePatch
import com.peekr.domain.setting.usecase.AccountInfoUseCases
import com.peekr.presentation.R
import com.peekr.presentation.setting.error.asUiText
import com.peekr.presentation.setting.model.UiEditableAccountInfo
import com.peekr.presentation.setting.state.AccountInfoContract
import com.peekr.presentation.setting.state.AccountInfoDisplayIdState
import com.peekr.presentation.setting.state.AccountInfoIntroduceState
import com.peekr.presentation.setting.state.AccountInfoNameState
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AccountInfoViewModel @Inject constructor(
    private val usecases: AccountInfoUseCases,
    private val snackbarController: SnackbarController,
    savedStateHandle: SavedStateHandle,
) : MVIBaseViewModel<AccountInfoContract.UiState, AccountInfoContract.UiEvent, AccountInfoContract.UiEffect>() {
    override fun createInitialState(): AccountInfoContract.UiState =
        AccountInfoContract.UiState()

    private var initialDisplayId: String? = savedStateHandle.get<String>("displayId")
    private var initialName: String? = savedStateHandle.get<String>("name")
    private var initialIntroduce: String? = savedStateHandle.get<String>("introduce")
    private var initialProfileImageUrl: String? = savedStateHandle.get<String>("profileImageUrl")

    // StateFlow 기반의 텍스트 필드 상태
    private val _displayIdFieldState = MutableStateFlow(AccountInfoDisplayIdState())
    val displayIdFieldState = _displayIdFieldState.asStateFlow()

    private val _nameFieldState = MutableStateFlow(AccountInfoNameState())
    val nameFieldState = _nameFieldState.asStateFlow()

    private val _introduceFieldState = MutableStateFlow(AccountInfoIntroduceState())
    val introduceFieldState = _introduceFieldState.asStateFlow()

    private val _localProfileImage = MutableStateFlow<ByteArray?>(null)

    /** 오로지 UI 표시용으로만 사용된다. */
    val localProfileImage = _localProfileImage.asStateFlow()

    init {
        val localInitialDisplayId = initialDisplayId
        val localInitialName = initialName
        val localInitialIntroduce = initialIntroduce

        // 넘어온 인자 값 체크
        if (localInitialDisplayId == null || localInitialName == null || localInitialIntroduce == null) {
            viewModelScope.launch {
                showSnackbar(UiText.StringResource(R.string.setting_error_my_profile_not_found))
            }
        } else {
            // 텍스트 필드 초기값 설정
            _displayIdFieldState.update {
                it.copy(displayId = localInitialDisplayId, isDisplayIdValid = true)
            }
            _nameFieldState.update {
                it.copy(name = localInitialName, isNameValid = true)
            }
            _introduceFieldState.update {
                it.copy(introduce = localInitialIntroduce, isIntroduceValid = true)
            }

            // 계정 정보 초기 값 설정
            updateState {
                copy(
                    accountInfo = UiEditableAccountInfo(
                        displayId = localInitialDisplayId,
                        name = localInitialName,
                        introduce = localInitialIntroduce,
                        profileImageUrl = initialProfileImageUrl,
                    ),
                )
            }

            observeDisplayIdValidation()
            observeNameValidation()
            observeIntroduceValidation()
            observeTextFieldChanges()
        }
    }

    override suspend fun handleEvent(event: AccountInfoContract.UiEvent) {
        when (event) {
            AccountInfoContract.UiEvent.OnSaveAccountInfo -> saveAccountInfo()
            is AccountInfoContract.UiEvent.OnProfileImageUpdated -> updateProfileImage(event.imageBytes)
            AccountInfoContract.UiEvent.OnProfileImageDeleted -> deleteProfileImage()
            AccountInfoContract.UiEvent.SafeBackPressed -> safeBackPressed()
            is AccountInfoContract.UiEvent.OnDisplayIdChanged ->
                _displayIdFieldState.update { it.copy(displayId = event.displayId) }

            is AccountInfoContract.UiEvent.OnNameChanged ->
                _nameFieldState.update { it.copy(name = event.name) }

            is AccountInfoContract.UiEvent.OnIntroduceChanged ->
                _introduceFieldState.update { it.copy(introduce = event.introduce) }
        }
    }

    // 사용자 표시 ID 유효성 검사
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeDisplayIdValidation() {
        _displayIdFieldState
            .map { it.displayId }
            .distinctUntilChanged()
            .onEach {
                // 입력 즉시 로딩 상태 부여
                _displayIdFieldState.update {
                    it.copy(isDisplayIdValid = false, displayIdError = null, loading = true)
                }
            }
            .debounce(DEBOUNCE_300)
            .flatMapLatest { text ->
                when {
                    text.isBlank() -> flowOf(ValidationResult.Loading)
                    text == initialDisplayId -> flowOf(ValidationResult.Valid(DisplayId(text)))
                    else -> usecases.validateDisplayId(text)
                }
            }
            .onEach { result ->
                when (result) {
                    ValidationResult.Loading -> _displayIdFieldState.update {
                        it.copy(isDisplayIdValid = false, displayIdError = null, loading = true)
                    }

                    is ValidationResult.Valid -> _displayIdFieldState.update {
                        it.copy(isDisplayIdValid = true, displayIdError = null, loading = false)
                    }

                    is ValidationResult.Invalid -> _displayIdFieldState.update {
                        it.copy(
                            isDisplayIdValid = false,
                            displayIdError = result.error.asUiText(),
                            loading = false,
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    // 사용자 명 유효성 검사
    private fun observeNameValidation() {
        _nameFieldState.setTextFieldValidation(
            scope = viewModelScope,
            value = { it.name },
            validator = { usecases.validateName(it) },
            onLoading = { _nameFieldState.update { it.copy(loading = true) } },
            onValid = {
                _nameFieldState.update {
                    it.copy(isNameValid = true, nameError = null, loading = false)
                }
            },
            onInvalid = { error ->
                _nameFieldState.update {
                    it.copy(isNameValid = false, nameError = error.asUiText(), loading = false)
                }
            },
        )
    }

    // 소개글 유효성 검사
    private fun observeIntroduceValidation() {
        _introduceFieldState.setTextFieldValidation(
            scope = viewModelScope,
            value = { it.introduce },
            validator = { usecases.validateIntroduce(it) },
            onLoading = { _introduceFieldState.update { it.copy(loading = true) } },
            onValid = {
                _introduceFieldState.update {
                    it.copy(isIntroduceValid = true, introduceError = null, loading = false)
                }
            },
            onInvalid = { error ->
                _introduceFieldState.update {
                    it.copy(isIntroduceValid = false, introduceError = error.asUiText(), loading = false)
                }
            },
        )
    }

    // 텍스트 필드 변경 감지 → UiState의 accountInfo, isAccountInfoEdited 업데이트
    private fun observeTextFieldChanges() {
        combine(
            _displayIdFieldState.map { it.displayId },
            _nameFieldState.map { it.name },
            _introduceFieldState.map { it.introduce },
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
                        isAccountInfoEdited = checkAccountInfoEdited(
                            displayId = displayId,
                            name = name,
                            introduce = introduce,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // 계정 정보 저장
    private fun saveAccountInfo() {
        val accountInfo = currentUiState.accountInfo ?: return

        if (currentUiState.isAccountInfoEdited &&
            _displayIdFieldState.value.isDisplayIdValid &&
            _nameFieldState.value.isNameValid &&
            _introduceFieldState.value.isIntroduceValid
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

                        // 초기 값 갱신
                        initialDisplayId = _displayIdFieldState.value.displayId
                        initialName = _nameFieldState.value.name
                        initialIntroduce = _introduceFieldState.value.introduce
                        initialProfileImageUrl = currentUiState.accountInfo?.profileImageUrl

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
    private fun updateProfileImage(imageBytes: ByteArray?) {
        if (imageBytes == null) {
            viewModelScope.launch {
                showSnackbar(UiText.StringResource(R.string.setting_error_updated_image_not_found))
            }
            return
        }

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

    private fun safeBackPressed() {
        if (currentUiState.isAccountInfoEdited) {
            sendEffect { AccountInfoContract.UiEffect.OpenSafeCancelModal }
        } else {
            sendEffect { AccountInfoContract.UiEffect.CloseScreen }
        }
    }

    // 계정 정보 변경 여부 확인
    private fun checkAccountInfoEdited(
        displayId: String = _displayIdFieldState.value.displayId,
        name: String = _nameFieldState.value.name,
        introduce: String = _introduceFieldState.value.introduce,
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

private const val DEBOUNCE_300 = 300L

package com.peekr.presentation.setting.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import com.peekr.domain.setting.usecase.SettingUseCases
import com.peekr.presentation.R
import com.peekr.presentation.setting.error.asUiText
import com.peekr.presentation.setting.model.UiAccountInfo
import com.peekr.presentation.setting.model.toUiModel
import com.peekr.presentation.setting.state.AccountInfoState
import com.peekr.presentation.setting.state.SettingContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val usecases: SettingUseCases,
    private val snackbarController: SnackbarController,
) : MVIBaseViewModel<SettingContract.UiState, SettingContract.UiEvent, SettingContract.UiEffect>() {
    override fun createInitialState(): SettingContract.UiState =
        SettingContract.UiState()

    // ------------------------------ 계정 정보 상태 값 ------------------------------
    private var initialAccountInfo: UiAccountInfo? = null

    val displayIdState = TextFieldState()
    val nameState = TextFieldState()
    val introduceState = TextFieldState()

    var isDisplayIdState by mutableStateOf<ValidationResult<DisplayId, SettingErrorType>>(ValidationResult.Loading)
        private set
    val isNameValid by derivedStateOf {
        usecases.validateName(nameState.text.toString())
    }
    val isIntroduceValid by derivedStateOf {
        usecases.validateIntroduce(introduceState.text.toString())
    }

    private val _localProfileImage = MutableStateFlow<ByteArray?>(null)
    val localProfileImage = _localProfileImage.asStateFlow()

    val accountInfoState = uiState
        .map { it.accountInfoState }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = uiState.value.accountInfoState,
        )

    // ------------------------------ XX 상태 값 ------------------------------

    // ------------------------------ 뷰모델 코드 ------------------------------
    init {
        loadAccountInfo()
        observeDisplayIdValidation()
        observeIsAccountInfoEdited()
    }

    override suspend fun handleEvent(event: SettingContract.UiEvent) {
        when (event) {
            SettingContract.UiEvent.OnSaveAccountInfo -> saveAccountInfo()
            is SettingContract.UiEvent.OnProfileImageUpdated -> updateProfileImage(event.imageBytes)
            SettingContract.UiEvent.OnAccountInfoStateCleared -> clearAccountInfoState()
            SettingContract.UiEvent.OnProfileImageDeleted -> deleteProfileImage()
        }
    }

    // 계정 정보 초기 로드
    private fun loadAccountInfo() {
        viewModelScope.launch {
            usecases.getAccountInfo().collect { result ->
                if (result is Result.Success) {
                    val accountInfo = result.data.toUiModel()

                    // 1. 초기 계정 정보 저장
                    initialAccountInfo = accountInfo

                    // 2. 각 텍스트 필드에도 초기 값 설정
                    displayIdState.setTextAndPlaceCursorAtEnd(result.data.displayId.value)
                    nameState.setTextAndPlaceCursorAtEnd(result.data.name.value)
                    introduceState.setTextAndPlaceCursorAtEnd(result.data.introduce.value)

                    // 3. 수정용 계정 정보 상태 업데이트
                    updateState {
                        this.copy(accountInfoState = AccountInfoState(accountInfo = accountInfo))
                    }
                }
            }
        }
    }

    private fun observeIsAccountInfoEdited() {
        combine(
            snapshotFlow { displayIdState.text.toString() },
            snapshotFlow { nameState.text.toString() },
            snapshotFlow { introduceState.text.toString() },
        ) { displayId, name, introduce ->
            Triple(displayId, name, introduce)
        }
            .onEach { (displayId, name, introduce) ->
                val updatedAccountInfo = currentUiState.accountInfoState.accountInfo?.copy(
                    displayId = displayId,
                    name = name,
                    introduce = introduce,
                )

                updateState {
                    this.copy(
                        accountInfoState = accountInfoState.copy(
                            accountInfo = updatedAccountInfo,
                            isAccountInfoEdited = updatedAccountInfo != initialAccountInfo,
                        ),
                    )
                }
            }
            .launchIn(viewModelScope)
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
                    text == initialAccountInfo?.displayId -> flowOf(ValidationResult.Valid(DisplayId(text)))
                    else -> usecases.validateDisplayId(text)
                }
            }
            .onEach { isDisplayIdState = it }
            .onStart { emit(ValidationResult.Loading) }
            .launchIn(viewModelScope)
    }

    // 계정 정보 저장
    private fun saveAccountInfo() {
        // 1. 초기 로드된 계정 정보가 없으면 즉시 종료
        val updatedAccountInfo = currentUiState.accountInfoState.accountInfo ?: return

        // 2. isAccountInfoEdited 가 true 인지 확인
        // 3. 모든 텍스트 필드 유효성 검사 상태가 Valid 인지 확인
        // 4. 위 과정이 모두 완료되면 최종적으로 계정 정보 저장 수행
        if (currentUiState.accountInfoState.isAccountInfoEdited &&
            isDisplayIdState is ValidationResult.Valid &&
            isNameValid is ValidationResult.Valid &&
            isIntroduceValid is ValidationResult.Valid
        ) {
            usecases.updateAccountInfo(
                displayId = updatedAccountInfo.displayId,
                name = updatedAccountInfo.name,
                introduce = updatedAccountInfo.introduce,
                oldProfileImageUrl = initialAccountInfo?.profileImageUrl,
                profileImagePatch = currentUiState.accountInfoState.profileImagePatch,
            ).onEach { result ->
                when (result) {
                    Result.Loading -> updateState { copy(fullScreenLoading = true) }
                    is Result.Error -> showSnackbar(result.error.asUiText())
                    is Result.Success -> showSnackbar(UiText.StringResource(R.string.setting_success_update_account_info))
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
                accountInfoState = accountInfoState.copy(
                    profileImagePatch = SettingProfileImagePatch.Update(imageBytes),
                    isAccountInfoEdited = true,
                ),
            )
        }
    }

    private fun deleteProfileImage() {
        _localProfileImage.update { null }
        updateState {
            copy(
                accountInfoState = accountInfoState.copy(
                    accountInfo = accountInfoState.accountInfo?.copy(profileImageUrl = null),
                    profileImagePatch = SettingProfileImagePatch.Remove,
                    isAccountInfoEdited = true,
                ),
            )
        }
    }

    // 계정 정보 화면에 필요한 모든 상태 값들 초기화
    private fun clearAccountInfoState() {
        // 텍스트 필드 초기화
        displayIdState.setTextAndPlaceCursorAtEnd(initialAccountInfo?.displayId ?: "")
        nameState.setTextAndPlaceCursorAtEnd(initialAccountInfo?.name ?: "")
        introduceState.setTextAndPlaceCursorAtEnd(initialAccountInfo?.introduce ?: "")

        // 유효성 검사 상태 초기화
        isDisplayIdState = if (initialAccountInfo?.displayId != null) {
            ValidationResult.Valid(DisplayId(initialAccountInfo!!.displayId))
        } else {
            ValidationResult.Loading
        }

        // 로컬 프로필 이미지 초기화
        _localProfileImage.update { null }

        // UiState 초기화
        updateState {
            copy(accountInfoState = AccountInfoState(accountInfo = initialAccountInfo))
        }
    }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}

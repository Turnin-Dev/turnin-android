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
import com.peekr.presentation.setting.model.toUiModel
import com.peekr.presentation.setting.state.AccountInfoState
import com.peekr.presentation.setting.state.SettingContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
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
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val usecases: SettingUseCases,
    private val snackbarController: SnackbarController,
) : MVIBaseViewModel<SettingContract.UiState, SettingContract.UiEvent, SettingContract.UiEffect>() {
    override fun createInitialState(): SettingContract.UiState =
        SettingContract.UiState()

    // ------------------------------ 계정 정보 텍스트 필드 상태 ------------------------------
    private var initialDisplayId: String = ""
    private var initialName: String = ""
    private var initialIntroduce: String = ""

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

    // ------------------------------ 각 화면 별 상태 ------------------------------
    val accountInfoState = uiState
        .map { it.accountInfoState }
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = uiState.value.accountInfoState,
        )

    init {
        loadAccountInfo()
        observeDisplayIdValidation()
        observeIsAccountInfoEdited()
    }

    override suspend fun handleEvent(event: SettingContract.UiEvent) {
        when (event) {
            SettingContract.UiEvent.OnSaveAccountInfo -> saveAccountInfo()
            is SettingContract.UiEvent.OnProfileImageUpdated -> updateProfileImage(event.imageBytes)
        }
    }

    // 계정 정보 초기 로드
    private fun loadAccountInfo() {
        viewModelScope.launch {
            usecases.getAccountInfo().collect { result ->
                if (result is Result.Success) {
                    initialDisplayId = result.data.displayId.value
                    initialName = result.data.name.value
                    initialIntroduce = result.data.introduce.value
                    displayIdState.setTextAndPlaceCursorAtEnd(result.data.displayId.value)
                    nameState.setTextAndPlaceCursorAtEnd(result.data.name.value)
                    introduceState.setTextAndPlaceCursorAtEnd(result.data.introduce.value)
                    updateState {
                        this.copy(
                            accountInfoState = AccountInfoState(
                                accountInfo = result.data.toUiModel(),
                            ),
                        )
                    }
                }
            }
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
            .onStart { emit(ValidationResult.Loading) }
            .launchIn(viewModelScope)
    }

    private fun observeIsAccountInfoEdited() {
        combine(
            snapshotFlow { displayIdState.text.toString() },
            snapshotFlow { nameState.text.toString() },
            snapshotFlow { introduceState.text.toString() },
        ) { displayId, name, introduce ->
            initialDisplayId != displayId ||
                initialName != name ||
                initialIntroduce != introduce
        }
            .onEach { isEdited ->
                updateState {
                    this.copy(
                        accountInfoState = accountInfoState.copy(isAccountInfoEdited = isEdited),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    // 계정 정보 저장
    private fun saveAccountInfo() {
        // 2. isAccountInfoEdited 가 true 인지 확인
        // 3. 모든 텍스트 필드 유효성 검사 상태가 Valid 인지 확인
        // 4. 위 과정이 모두 완료되면 최종적으로 계정 정보 저장 수행
        if (currentUiState.accountInfoState.isAccountInfoEdited &&
            isDisplayIdState is ValidationResult.Valid &&
            isNameValid is ValidationResult.Valid &&
            isIntroduceValid is ValidationResult.Valid
        ) {
            usecases.updateAccountInfo(
                displayId = displayIdState.text.toString(),
                name = nameState.text.toString(),
                introduce = introduceState.text.toString(),
                oldProfileImageUrl = currentUiState.accountInfoState.accountInfo?.profileImageUrl,
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
        updateState {
            copy(
                accountInfoState = accountInfoState.copy(
                    profileImagePatch = SettingProfileImagePatch.Update(imageBytes),
                ),
            )
        }
    }

    private suspend fun showSnackbar(message: UiText) {
        snackbarController.sendEvent(SnackbarEvent(message = message))
    }
}

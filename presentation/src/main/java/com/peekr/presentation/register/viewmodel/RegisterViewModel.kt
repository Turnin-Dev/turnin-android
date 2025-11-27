package com.peekr.presentation.register.viewmodel

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.validation.ValidationResult
import com.peekr.core.presentation.common.error.asUiText
import com.peekr.core.presentation.feature.image.toByteArray
import com.peekr.core.presentation.ui.model.UiSocialLoginProvider
import com.peekr.domain.register.error.RegisterErrorType
import com.peekr.domain.register.model.ExistsResult
import com.peekr.domain.register.usecase.CheckDisplayIdExistsUseCase
import com.peekr.domain.register.usecase.RegisterIntegrationUseCase
import com.peekr.domain.register.usecase.ValidateDisplayIdUseCase
import com.peekr.domain.register.usecase.ValidateIntroduceUseCase
import com.peekr.domain.register.usecase.ValidateNameUseCase
import com.peekr.presentation.login.error.asUiText
import com.peekr.presentation.login.mapper.toDomainModel
import com.peekr.presentation.register.error.asUiText
import com.peekr.presentation.register.model.UiImageFileDetail
import com.peekr.presentation.register.model.toDomainModel
import com.peekr.presentation.register.state.RegisterDisplayIdState
import com.peekr.presentation.register.state.RegisterEventState
import com.peekr.presentation.register.state.RegisterNameState
import com.peekr.presentation.register.state.RegisterProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateDisplayIdUseCase: ValidateDisplayIdUseCase,
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateIntroduceUseCase: ValidateIntroduceUseCase,
    private val checkDisplayIdExistsUseCase: CheckDisplayIdExistsUseCase,
    private val registerIntegrationUseCase: RegisterIntegrationUseCase,
) : ViewModel() {
    private val _displayIdState = MutableStateFlow(RegisterDisplayIdState())
    val displayIdState = _displayIdState.asStateFlow()

    private val _nameState = MutableStateFlow(RegisterNameState())
    val nameState = _nameState.asStateFlow()

    private val _profileState = MutableStateFlow(RegisterProfileState())
    val profileState = _profileState.asStateFlow()

    private val _registerEventState = MutableStateFlow(RegisterEventState())
    val registerEventState = _registerEventState.asStateFlow()

    init {
        validateDisplayIdState()
        validateNameState()
        validateIntroduceState()
    }

    // ------------------------------ 단순한 값 상태 업데이트 ------------------------------
    fun onDisplayIdChanged(displayId: String) {
        _displayIdState.update { it.copy(displayId = displayId) }
    }

    fun onNameChanged(name: String) {
        _nameState.update { it.copy(name = name) }
    }

    fun onIntroduceChanged(introduce: String) {
        _profileState.update { it.copy(introduce = introduce) }
    }

    fun selectProfileImage(image: ImageBitmap?) {
        _profileState.update { it.copy(image = image) }
    }

    fun selectOriginalImage(image: ImageBitmap?) {
        _profileState.update { it.copy(originalImage = image) }
        image?.let {
            _registerEventState.update { it.copy(navigateToCropImageScreen = true) }
        }
    }

    // ------------------------------ UI 비즈니스 로직 ------------------------------

    /**
     * 회원가입을 진행한다.
     *
     * @param provider presentation 계층용 소셜로그인 플랫폼
     * @param providerId 소셜로그인 플랫폼 ID
     * @param image [ImageBitmap]타입의 프로필 이미지
     */
    fun register(
        provider: UiSocialLoginProvider,
        providerId: String,
        image: ImageBitmap?,
    ) {
        val imageFileDetail = image
            ?.let {
                UiImageFileDetail.create(image.toByteArray(), nameState.value.name)
            }?.toDomainModel()
        registerIntegrationUseCase(
            provider = provider.toDomainModel(),
            providerId = providerId,
            displayId = displayIdState.value.displayId,
            name = nameState.value.name,
            imageFileDetail = imageFileDetail,
            introduce = profileState.value.introduce,
        ).onEach { result ->
            when (result) {
                Result.Loading -> {
                    _profileState.update { it.copy(loading = true) }
                }

                is Result.Error<RegisterErrorType> -> {
                    _profileState.update {
                        it.copy(introduceError = result.error.asUiText(), loading = false)
                    }
                }

                is Result.Success -> {
                    _profileState.update { it.copy(loading = false) }
                    _registerEventState.update { it.copy(navigateToNextScreen = true) }
                }
            }
        }.launchIn(viewModelScope)
    }

    /**
     * 사용자 표시 ID 중복 검사를 한다.
     *
     * - 만약 중복된다면 [RegisterDisplayIdState]를 통해 error를 표시한다.
     * - 중복되지 않는다면 사용 가능하므로 [registerEventState]를 통해 다음 화면으로
     * 넘어갈 수 있게 이벤트 상태를 보낸다.
     */
    fun checkDisplayIdExists(displayId: String) {
        val normalized = displayId.trim()
        if (normalized.isNotEmpty()) {
            checkDisplayIdExistsUseCase(normalized)
                .onEach { result ->
                    when (result) {
                        Result.Loading -> {
                            _displayIdState.update { it.copy(loading = true) }
                        }

                        is Result.Error<RegisterErrorType> -> {
                            _displayIdState.update {
                                it.copy(displayIdError = result.error.asUiText(), loading = false)
                            }
                        }

                        is Result.Success<ExistsResult> -> {
                            val exists = result.data.exists
                            if (exists) { // 이미 존재하면 중복이므로 사용 X
                                _displayIdState.update {
                                    it.copy(
                                        displayIdError =
                                            RegisterErrorType.DisplayIdNotAvailable.asUiText(),
                                    )
                                }
                            } else {
                                _displayIdState.update { it.copy(displayIdError = null) }
                                _registerEventState.update { it.copy(navigateToNextScreen = true) }
                            }
                            _displayIdState.update { it.copy(loading = false) }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _displayIdState.update {
                it.copy(
                    displayIdError = RegisterErrorType.CantUseEmptyOrBlank.asUiText(),
                    loading = false,
                )
            }
        }
    }

    // ------------------------------ 초기화 로직 ------------------------------

    /** [RegisterDisplayIdState] - displayId 상태 값이 변할 때 마다 유효성 검사를 수행할 수 있게 한다. */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun validateDisplayIdState() {
        displayIdState
            .map { it.displayId }
            .distinctUntilChanged()
            .onEach { if (it.isBlank()) _displayIdState.update { s -> s.copy(canNext = false) } }
            .filter { it.isNotBlank() }
            .flatMapLatest { displayId -> validateDisplayIdUseCase(displayId) }
            .onEach { result ->
                when (result) {
                    ValidationResult.Loading -> _displayIdState.update { it.copy(canNext = false) }
                    is ValidationResult.Valid -> _displayIdState.update { it.copy(displayIdError = null, canNext = true) }
                    is ValidationResult.Invalid -> _displayIdState.update {
                        it.copy(displayIdError = result.error.asUiText(), canNext = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    /** [RegisterNameState] - name 상태 값이 변할 때 마다 유효성 검사를 수행할 수 있게 한다. */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun validateNameState() {
        nameState
            .map { it.name }
            .distinctUntilChanged()
            .onEach { if (it.isBlank()) _nameState.update { s -> s.copy(canNext = false) } }
            .filter { it.isNotBlank() }
            .flatMapLatest { name -> validateNameUseCase(name) }
            .onEach { result ->
                when (result) {
                    ValidationResult.Loading -> _nameState.update { it.copy(canNext = false) }
                    is ValidationResult.Valid -> _nameState.update { it.copy(nameError = null, canNext = true) }
                    is ValidationResult.Invalid -> _nameState.update {
                        it.copy(nameError = result.error.asUiText(), canNext = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    /** [RegisterProfileState] - profile(introduce) 상태 값이 변할 때 마다 유효성 검사를 수행할 수 있게 한다. */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun validateIntroduceState() {
        profileState
            .map { it.introduce }
            .distinctUntilChanged()
            .onEach { if (it.isBlank()) _profileState.update { s -> s.copy(canNext = false) } }
            .filter { it.isNotBlank() }
            .flatMapLatest { introduce -> validateIntroduceUseCase(introduce) }
            .onEach { result ->
                when (result) {
                    ValidationResult.Loading -> _profileState.update { it.copy(canNext = false) }
                    is ValidationResult.Valid -> _profileState.update {
                        it.copy(introduceError = null, canNext = true)
                    }

                    is ValidationResult.Invalid -> _profileState.update {
                        it.copy(introduceError = result.error.asUiText(), canNext = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    // ------------------------------ 초기화 및 자원 정리 로직 ------------------------------
    fun onConsumeEventState() {
        _registerEventState.update {
            it.copy(
                navigateToNextScreen = false,
                navigateToCropImageScreen = false,
            )
        }
    }
}

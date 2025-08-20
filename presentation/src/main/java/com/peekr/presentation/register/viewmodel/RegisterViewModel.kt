package com.peekr.presentation.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.domain.account.model.ExistsResult
import com.peekr.domain.account.usecase.register.CheckDisplayIdExistsUseCase
import com.peekr.domain.account.usecase.register.ValidateDisplayIdUseCase
import com.peekr.domain.shared.util.CommonValidationError
import com.peekr.domain.shared.util.ErrorType
import com.peekr.domain.shared.util.Result
import com.peekr.domain.shared.util.ValidationResult
import com.peekr.presentation.register.state.RegisterEventState
import com.peekr.presentation.register.state.RegisterState
import com.peekr.presentation.shared.util.error.asUiText
import com.peekr.presentation.shared.util.error.errorTypeFirst
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateDisplayIdUseCase: ValidateDisplayIdUseCase,
    private val checkDisplayIdExistsUseCase: CheckDisplayIdExistsUseCase,
) : ViewModel() {
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    private val _registerEventState = MutableStateFlow(RegisterEventState())
    val registerEventState = _registerEventState.asStateFlow()

    init {
        validateDisplayIdState()
    }

    fun onDisplayIdChanged(displayId: String) {
        _registerState.update {
            it.copy(displayId = displayId)
        }
    }

    /**
     * 사용자 표시 ID 중복 검사를 한다.
     *
     * - 만약 중복된다면 [registerState]를 통해 error를 표시한다.
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
                            _registerState.update {
                                it.copy(loading = true)
                            }
                        }

                        is Result.Error<ErrorType> -> {
                            _registerState.update {
                                it.copy(error = result.errorTypeFirst(), loading = false)
                            }
                        }

                        is Result.Success<ExistsResult> -> {
                            val exists = result.data.exists
                            if (exists) { // 이미 존재하면 중복이므로 사용 X
                                _registerState.update {
                                    it.copy(error = RegisterError.DisplayIdNotAvailable.asUiText())
                                }
                            } else {
                                _registerEventState.update { it.copy(navigateToNextScreen = true) }
                            }
                            _registerState.update { it.copy(loading = false) }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            _registerState.update { it.copy(error = RegisterError.CantUseEmptyOrBlack.asUiText()) }
        }
    }

    /** [registerState] - displayId 상태 값이 변할 때 마다 유효성 검사를 수행할 수 있게 한다. */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun validateDisplayIdState() {
        registerState
            .map { it.displayId }
            .distinctUntilChanged()
            .flatMapLatest { displayId ->
                val normalized = displayId.trim()
                if (normalized.isEmpty()) return@flatMapLatest validateDisplayIdUseCase("")
                validateDisplayIdUseCase(normalized)
            }.onEach { result ->
                when (result) {
                    ValidationResult.Loading -> _registerState.update { it.copy(canNext = false) }
                    ValidationResult.Success -> _registerState.update { it.copy(error = null, canNext = true) }
                    is ValidationResult.Error<CommonValidationError> -> _registerState.update {
                        it.copy(error = result.error.asUiText(), canNext = false)
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun onConsumeEventState() {
        _registerEventState.update { it.copy(navigateToNextScreen = false) }
    }

    fun initCanNextState() {
        _registerState.update { it.copy(canNext = true) }
    }
}

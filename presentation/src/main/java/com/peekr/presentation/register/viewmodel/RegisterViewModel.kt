package com.peekr.presentation.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peekr.domain.account.usecase.register.ValidateDisplayIdUseCase
import com.peekr.domain.shared.util.ValidationResult
import com.peekr.presentation.register.state.RegisterState
import com.peekr.presentation.shared.util.error.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val validateDisplayIdUseCase: ValidateDisplayIdUseCase,
) : ViewModel() {
    private val _registerState = MutableStateFlow(RegisterState())
    val registerState = _registerState.asStateFlow()

    init {
        debounceDisplayIdState()
    }

    fun onDisplayIdChanged(displayId: String) {
        _registerState.update {
            it.copy(displayId = displayId)
        }
    }

    @OptIn(FlowPreview::class)
    private fun debounceDisplayIdState() {
        registerState
            .map { it.displayId }
            .distinctUntilChanged()
            .debounce(300)
            .onEach { displayId ->
                if (displayId.isNotEmpty()) {
                    validateDisplayId(displayId)
                }
            }.launchIn(viewModelScope)
    }

    private fun validateDisplayId(displayId: String) {
        validateDisplayIdUseCase(displayId)
            .onEach { result ->
                when (result) {
                    ValidationResult.Loading -> {}
                    is ValidationResult.Error -> {
                        _registerState.update {
                            it.copy(error = result.error.asUiText())
                        }
                    }

                    ValidationResult.Success -> {
                        _registerState.update {
                            it.copy(error = null)
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}

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
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import com.peekr.domain.setting.error.SettingErrorType
import com.peekr.domain.setting.usecase.SettingUseCases
import com.peekr.presentation.setting.state.SettingContract
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val usecases: SettingUseCases,
) : MVIBaseViewModel<SettingContract.UiState, SettingContract.UiEvent, SettingContract.UiEffect>() {
    override fun createInitialState(): SettingContract.UiState =
        SettingContract.UiState()

    private var initialDisplayId: String = ""

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

    init {
        loadAccountInfo()
        observeDisplayIdValidation()
    }

    override suspend fun handleEvent(event: SettingContract.UiEvent) {
        when (event) {
            else -> {}
        }
    }

    private fun loadAccountInfo() {
        viewModelScope.launch {
            usecases.getAccountInfo().collect { result ->
                if (result is Result.Success) {
                    initialDisplayId = result.data.displayId.value
                    displayIdState.setTextAndPlaceCursorAtEnd(result.data.displayId.value)
                    nameState.setTextAndPlaceCursorAtEnd(result.data.name.value)
                    introduceState.setTextAndPlaceCursorAtEnd(result.data.introduce.value)
                }
            }
        }
    }

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
}

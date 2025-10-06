package com.peekr.core.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface BaseUiState

interface BaseUiEvent

interface BaseUiEffect

/**
 * MVI 패턴을 사용하는 Base ViewModel
 */
abstract class MVIBaseViewModel<State : BaseUiState, Event : BaseUiEvent, Effect : BaseUiEffect> : ViewModel() {
    // ------------------------------ Init ------------------------------
    init {
        viewModelScope.launch {
            loadInitialData()
        }
    }

    // ------------------------------ UI State ------------------------------

    /** UI State 초기 값 */
    protected abstract val initialState: State

    // UI State
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)

    /** UI State */
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    /** 단순히 UI State 값을 읽을 때 사용한다. */
    protected val currentUiState: State
        get() = uiState.value

    /** 초기 데이터 로드 시 사용한다. */
    protected open suspend fun loadInitialData() {}

    /** Update UiState */
    protected fun updateState(newUiState: State) {
        _uiState.update { newUiState }
    }

    /** Update UiState */
    protected fun updateState(reducer: State.() -> State) {
        val newState = currentUiState.reducer()
        _uiState.update { newState }
    }

    // ------------------------------ UI Event ------------------------------

    /** Handle UiEvent */
    fun processEvent(event: Event) {
        viewModelScope.launch {
            handleEvent(event)
        }
    }

    /** Handle UiEvent */
    protected abstract suspend fun handleEvent(event: Event)

    // ------------------------------ UI Effect ------------------------------

    /** UI Effect, 일회성 이벤트를 위한 상태 값 (Channel 이용) */
    private val _effect = Channel<Effect>()
    val effect = _effect.receiveAsFlow()

    /** Send UiEffect */
    protected fun sendEffect(effect: () -> Effect) {
        val effectValue = effect()
        viewModelScope.launch { _effect.send(effectValue) }
    }

    override fun onCleared() {
        super.onCleared()
        _effect.close()
    }
}

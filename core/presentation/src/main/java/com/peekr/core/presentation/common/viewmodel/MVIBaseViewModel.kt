package com.peekr.core.presentation.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
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

    /**
     * 초기 데이터 로드 시 사용한다.
     *
     * 이 함수에서는 가벼운 작업을 수행하는 것을 권장한다.
     *
     * 비교적 무거운 작업은 직접 뷰모델 클래스의 init 블록 내부에서 수행하는 것을 권장한다.
     */
    protected open suspend fun loadInitialData() {}

    // ------------------------------ UI State ------------------------------

    /** UI State 초기 값 */
    private val initialState: State by lazy { createInitialState() }

    protected abstract fun createInitialState(): State

    // UI State
    private val _uiState: MutableStateFlow<State> = MutableStateFlow(initialState)

    /** UI State */
    val uiState: StateFlow<State> =
        _uiState
            .onStart { loadInitialData() }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = initialState,
            )

    /** 단순히 UI State 값을 읽을 때 사용한다. */
    protected val currentUiState: State
        get() = _uiState.value

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

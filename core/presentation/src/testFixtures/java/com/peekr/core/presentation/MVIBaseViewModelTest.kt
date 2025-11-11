package com.peekr.core.presentation

import androidx.lifecycle.viewModelScope
import com.peekr.core.presentation.viewmodel.BaseUiEffect
import com.peekr.core.presentation.viewmodel.BaseUiEvent
import com.peekr.core.presentation.viewmodel.BaseUiState
import com.peekr.core.presentation.viewmodel.MVIBaseViewModel
import kotlin.reflect.KClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule

/**
 * [MVIBaseViewModel] 테스트 유틸 클래스
 *
 * 해당 클래스는 [MVIBaseViewModel]를 사용하는 뷰모델의 테스트를 쉽게 하기 위한 유틸리티 클래스이다.
 * 그러므로, [MVIBaseViewModel]와 밀접하게 연결되어 있다.
 *
 * @see MVIBaseViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class MVIBaseViewModelTest<
    State : BaseUiState,
    Event : BaseUiEvent,
    Effect : BaseUiEffect,
    ViewModel : MVIBaseViewModel<State, Event, Effect>,
> {
    // Set test dispatcher
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    /**
     * UI 상태 테스트 함수
     *
     * @param viewModel 테스트 할 뷰모델
     * @param assertLastStateOnly 가장 최신 상태 값만 검증할 것 인지에 대한 여부
     * @param includeInitialState 초기 상태 값 포함 여부
     * @param intents 수행할 인텐트 (= 이벤트)
     * @param assertions 초기 상태 값을 기준으로 검증할 UI 상태 리스트
     */
    protected fun test(
        viewModel: ViewModel,
        assertLastStateOnly: Boolean = false,
        includeInitialState: Boolean = false,
        intents: List<Event>,
        assertions: (State) -> List<State>,
    ): Unit = runTest {
        // Set ViewModal States
        val states = mutableListOf<State>()
        val stateCollectionJob =
            viewModel.viewModelScope.launch {
                // drop(1) 이유:
                // MVIBaseViewModel 에서 자체적으로 초기 상태를 설정하는 과정에서 초기 상태가 담기기 때문
                viewModel.uiState
//                    .drop(1)
                    .toList(states)
            }

        // Send intent
        intents.forEach { intent -> viewModel.processEvent(intent) }

        // States assertion
        val initialState = states.last()
        if (assertLastStateOnly) {
            assertEquals(assertions(initialState).last(), states.last())
        } else {
            assertEquals(assertions(initialState).size, states.size)
            assertions(initialState).zip(states) { assertion, state ->
                assertEquals(assertion, state)
            }
        }

        // Clean up
        stateCollectionJob.cancel()
    }

    /**
     * 일회성 이벤트 테스트 함수
     *
     * @param viewModel 테스트 할 뷰모델
     * @param intents 수행할 인텐트 (= 이벤트)
     * @param assertions 검증할 [Effect]의 리스트
     * @param assertTypeOnly 타입 검증만 실시할 지에 대한 여부
     */
    protected fun testEffect(
        viewModel: ViewModel,
        intents: List<Event>,
        assertions: List<Effect>,
        assertTypeOnly: Boolean = false,
    ): Unit = runTest {
        // Set ViewModal Effects
        val effects = mutableListOf<Effect>()
        val effectCollectionJob =
            viewModel.viewModelScope.launch {
                // UI 상태를 수집하기 시작해야
                // MVIBaseViewModel의 uiState에 걸려있는 .onStart { 초기 데이터 로드 수행 }을 수행하기 때문이다.
                viewModel.uiState.first()
                viewModel.effect.toList(effects)
            }

        // Send intent
        intents.forEach { intent -> viewModel.processEvent(intent) }

        // Effects Assertion
        assertEquals(assertions.size, effects.size)
        assertions.zip(effects) { assertion, effect ->
            if (assertTypeOnly) {
                val assertionClass: KClass<out Effect> = assertion::class
                val effectClass: KClass<out Effect> = effect::class
                assertEquals(assertionClass, effectClass)
            } else {
                assertEquals(assertion, effect)
            }
        }

        // Clean up
        effectCollectionJob.cancel()
    }
}

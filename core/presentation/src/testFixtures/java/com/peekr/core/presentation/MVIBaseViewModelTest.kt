package com.peekr.core.presentation

import androidx.lifecycle.viewModelScope
import com.peekr.core.presentation.common.viewmodel.BaseUiEffect
import com.peekr.core.presentation.common.viewmodel.BaseUiEvent
import com.peekr.core.presentation.common.viewmodel.BaseUiState
import com.peekr.core.presentation.common.viewmodel.MVIBaseViewModel
import kotlin.reflect.KClass
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
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
     * 해당 함수를 사용할 때 [assertAllState] 파라미터를 주의해야 한다.
     *
     * [assertAllState]가 `false`인 경우에는 예상되는 상태 값(1개)만 검증하면 되지만,
     *
     * [assertAllState]가 `true`인 경우에는 초기 상태 값을 포함하여 순서대로 상태 값들을 나열해서 검증해야 한다.
     * (**만약 초기 데이터 로직이 있어서 뷰모델 초기화 시 상태 변화가 일어난다면 해당 상태 값도 포함해줘야 한다.**)
     *
     * ```
     * // ------------------------------ Usage ------------------------------
     * class UserContract {
     *     data class UiState(
     *         val username: String = "",
     *         val loading: Boolean = false,
     *         val error: UiText? = null,
     *     ) : BaseUiState
     *
     *     sealed interface UiEvent : BaseUiEvent {
     *         data object GetUser : UiEvent
     *     }
     *
     *     sealed interface UiEffect : BaseUiEffect {
     *         data object BackPress : UiEffect
     *     }
     * }
     *
     * class UserViewModel : MVIBaseViewModel<
     *     UserContract.UiState,
     *     UserContract.UiEvent,
     *     UserContract.UiEffect,
     *     >() {
     *     ...
     * }
     *
     * class UserViewModelTest : MVIBaseViewModelTest<
     *     UserContract.UiState,
     *     UserContract.UiEvent,
     *     UserContract.UiEffect,
     *     UserViewModel,
     *     >() {
     *     private val userViewModel = UserViewModel()
     *
     *     @Test
     *     fun normal_ui_state_test() {
     *         testState(
     *             viewModel = userViewModel,
     *             intents = listOf(
     *                 UserContract.UiEvent.GetUser,
     *             ),
     *             assertions = listOf(
     *                 UserContract.UiState(username = "name"),
     *             ),
     *         )
     *     }
     *
     *     @Test
     *     fun all_ui_state_test() {
     *         testState(
     *             viewModel = userViewModel,
     *             assertAllState = true, // assertAllState is true
     *             intents = listOf(
     *                 UserContract.UiEvent.GetUser,
     *             ),
     *             assertions = listOf(
     *                 UserContract.UiState(), // Initial State
     *                 UserContract.UiState(username = "name"),
     *             ),
     *         )
     *     }
     * }
     * ```
     *
     * @param viewModel 테스트 할 뷰모델
     * @param assertAllState 검증에 모든 상태 포함 여부
     * @param intents 수행할 인텐트 (= 이벤트)
     * @param assertions 검증할 UI 상태 리스트
     */
    protected fun testState(
        viewModel: ViewModel,
        assertAllState: Boolean = false,
        intents: List<Event>,
        assertions: List<State>,
    ): Unit = runTest {
        // Set ViewModal States
        val states = mutableListOf<State>()
        val stateCollectionJob =
            viewModel.viewModelScope.launch {
                viewModel.uiState.toList(states)
            }

        yield()

        // Send intent
        intents.forEach { intent -> viewModel.processEvent(intent) }

        advanceUntilIdle()

        // States assertion
        if (assertAllState) {
            assertEquals(assertions.size, states.size)
            assertions.zip(states) { assertion, state ->
                assertEquals(assertion, state)
            }
        } else {
            assertEquals(assertions.last(), states.last())
        }

        // Clean up
        stateCollectionJob.cancel()
    }

    /**
     * 일회성 이벤트 테스트 함수
     *
     * 해당 함수를 사용할 때 [assertTypeOnly] 파라미터를 주의해야 한다.
     *
     * [assertTypeOnly]가 `true`인 경우 일회성 이벤트에 포함된 내용과 상관없이 타입 검증만 수행한다.
     * (예를 들어, 에러 메시지 내용까지 검증할 필요 없고 에러 타입만 검증하고자 할 때 사용한다.)
     *
     * [assertTypeOnly]가 `false`인 경우 일회성 이벤트에 포함된 내용까지 함께 검증한다.
     *
     * ```
     * // ------------------------------ Usage ------------------------------
     * class UserContract {
     *     data class UiState(
     *         val username: String = "",
     *         val loading: Boolean = false,
     *         val error: UiText? = null,
     *     ) : BaseUiState
     *
     *     sealed interface UiEvent : BaseUiEvent {
     *         data object GetUser : UiEvent
     *     }
     *
     *     sealed interface UiEffect : BaseUiEffect {
     *         data object Error : UiEffect
     *         data class ErrorWithMessage(val message: String) : UiEffect
     *     }
     * }
     *
     * class UserViewModel : MVIBaseViewModel<
     *     UserContract.UiState,
     *     UserContract.UiEvent,
     *     UserContract.UiEffect,
     *     >() {
     *     ...
     * }
     *
     * class UserViewModelTest : MVIBaseViewModelTest<
     *     UserContract.UiState,
     *     UserContract.UiEvent,
     *     UserContract.UiEffect,
     *     UserViewModel,
     *     >() {
     *     private val userViewModel = UserViewModel()
     *
     *     @Test
     *     fun normal_ui_effect_test() {
     *         testEffect(
     *             viewModel = userViewModel,
     *             intents = listOf(
     *                 UserContract.UiEvent.GetUser,
     *             ),
     *             assertions = listOf(
     *                 UserContract.UiEffect.Error,
     *             ),
     *         )
     *     }
     *
     *     @Test
     *     fun assert_type_only_ui_effect_test() {
     *         testEffect(
     *             viewModel = userViewModel,
     *             assertTypeOnly = true, // assertTypeOnly is true
     *             intents = listOf(
     *                 UserContract.UiEvent.GetUser,
     *             ),
     *             assertions = listOf(
     *                 // The message content is not important, only perform type assertion.
     *                 UserContract.UiEffect.ErrorWithMessage("I don't care."),
     *             ),
     *         )
     *     }
     * }
     *
     * ```
     *
     * @param viewModel 테스트 할 뷰모델
     * @param assertTypeOnly 타입 검증만 실시할 지에 대한 여부
     * @param intents 수행할 인텐트 (= 이벤트)
     * @param assertions 검증할 [Effect]의 리스트
     */
    protected fun testEffect(
        viewModel: ViewModel,
        assertTypeOnly: Boolean = false,
        intents: List<Event>,
        assertions: List<Effect>,
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

        yield()

        // Send intent
        intents.forEach { intent -> viewModel.processEvent(intent) }

        advanceUntilIdle()

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

    /**
     * UI 상태, 일회성 이벤트 동시 테스트 함수
     *
     * @param viewModel 테스트 할 뷰모델
     * @param assertAllState 검증에 모든 상태 포함 여부
     * @param assertEffectTypeOnly 타입 검증만 실시할 지에 대한 여부
     * @param intents 수행할 인텐트 (= 이벤트)
     * @param assertionStates 검증할 UI 상태[State] 리스트
     * @param assertionEffects 검증할 [Effect]의 리스트
     */
    protected fun testAll(
        viewModel: ViewModel,
        assertAllState: Boolean = false,
        assertEffectTypeOnly: Boolean = false,
        intents: List<Event>,
        assertionStates: List<State>,
        assertionEffects: List<Effect>,
    ): Unit = runTest {
        // Set ViewModal States, Effects
        val states = mutableListOf<State>()
        val effects = mutableListOf<Effect>()
        val stateCollectionJob =
            viewModel.viewModelScope.launch {
                viewModel.uiState.toList(states)
            }
        val effectsCollectionJob =
            viewModel.viewModelScope.launch {
                viewModel.effect.toList(effects)
            }

        yield()

        // Send intent
        intents.forEach { intent -> viewModel.processEvent(intent) }

        advanceUntilIdle()

        // States assertion
        if (assertAllState) {
            assertEquals(assertionStates.size, states.size)
            assertionStates.zip(states) { assertion, state ->
                assertEquals(assertion, state)
            }
        } else {
            assertEquals(assertionStates.last(), states.last())
        }

        // Effects Assertion
        assertEquals(assertionEffects.size, effects.size)
        assertionEffects.zip(effects) { assertion, effect ->
            if (assertEffectTypeOnly) {
                val assertionClass: KClass<out Effect> = assertion::class
                val effectClass: KClass<out Effect> = effect::class
                assertEquals(assertionClass, effectClass)
            } else {
                assertEquals(assertion, effect)
            }
        }

        // Clean up
        stateCollectionJob.cancel()
        effectsCollectionJob.cancel()
    }
}

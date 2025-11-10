package com.peekr.core.presentation

import androidx.lifecycle.viewModelScope
import com.peekr.core.presentation.viewmodel.BaseUiEffect
import com.peekr.core.presentation.viewmodel.BaseUiEvent
import com.peekr.core.presentation.viewmodel.BaseUiState
import com.peekr.core.presentation.viewmodel.MVIBaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
abstract class MVIBaseViewModelTest<
    State : BaseUiState,
    Event : BaseUiEvent,
    Effect : BaseUiEffect,
    ViewModel : MVIBaseViewModel<State, Event, Effect>,
> {
    // Set test dispatcher
    val testDispatcher = UnconfinedTestDispatcher()

    // Setup
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    // Test
    protected fun test(
        viewModel: ViewModel,
        intents: List<Event>,
        assertions: List<State>,
        assertInitialState: Boolean = false,
    ): Unit = runTest {
        val states =
            if (assertInitialState) {
                mutableListOf(viewModel.uiState.value)
            } else {
                mutableListOf()
            }
        val stateCollectionJob =
            viewModel.viewModelScope.launch {
                viewModel.uiState.toList(states)
            }

        intents.forEach { intent -> viewModel.processEvent(intent) }

        assertEquals(assertions.size, states.size)
        assertions.zip(states) { assertion, state ->
            assertEquals(assertion, state)
        }

        stateCollectionJob.cancel()
    }

    // Cleanup
    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }
}

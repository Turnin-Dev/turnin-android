package com.turnin.presentation.setting.viewmodel

import com.turnin.core.domain.common.Result
import com.turnin.core.domain.model.DisplayId
import com.turnin.core.domain.model.Introduce
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.SocialLoginProvider
import com.turnin.core.domain.model.UserId
import com.turnin.core.presentation.FakeSnackbarController
import com.turnin.core.presentation.MVIBaseViewModelTest
import com.turnin.core.presentation.common.snackbar.SnackbarEvent
import com.turnin.domain.setting.error.SettingErrorType
import com.turnin.domain.setting.model.AccountInfo
import com.turnin.domain.setting.usecase.SettingUseCases
import com.turnin.presentation.setting.error.asUiText
import com.turnin.presentation.setting.model.toUiModel
import com.turnin.presentation.setting.state.SettingContract
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SettingViewModelTest :
    MVIBaseViewModelTest<
        SettingContract.UiState,
        SettingContract.UiEvent,
        SettingContract.UiEffect,
        SettingViewModel,
    >() {
    private val usecases: SettingUseCases = mockk()
    private val snackbarController = FakeSnackbarController()
    private lateinit var viewModel: SettingViewModel

    @Before
    fun setUp() {
        every {
            usecases.getAccountInfo()
        } returns flowOf(Result.Success(TestAccountInfo))

        viewModel = SettingViewModel(usecases, snackbarController)
    }

    @Test
    fun `초기 계정 정보 로드 성공 시 accountInfoLoading이 false로 업데이트된다`() {
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                SettingContract.UiState(
                    accountInfoLoading = false,
                ),
            ),
        )
    }

    @Test
    fun `계정 정보 로드 중에는 accountInfoLoading이 true로 업데이트된다`() {
        every {
            usecases.getAccountInfo()
        } returns flow {
            emit(Result.Loading)
        }
        viewModel = SettingViewModel(usecases, snackbarController)

        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                SettingContract.UiState(
                    accountInfoLoading = true,
                ),
            ),
        )
    }

    @Test
    fun `계정 정보 로드 실패 시 accountInfoLoading이 false가 되고 스낵바 에러가 표시된다`() = runTest {
        // given
        val expectedError = SettingErrorType.Unexpected(null)
        every {
            usecases.getAccountInfo()
        } returns flowOf(Result.Error(expectedError))
        viewModel = SettingViewModel(usecases, snackbarController)

        val snackbarList = mutableListOf<SnackbarEvent>()
        val snackbarJob = launch {
            snackbarController.events.toList(snackbarList)
        }

        // when, then
        testState(
            viewModel = viewModel,
            intents = listOf(),
            assertions = listOf(
                SettingContract.UiState(
                    accountInfoLoading = false,
                ),
            ),
        )

        // then: 스낵바 이벤트 검증
        assertTrue(snackbarList.isNotEmpty())
        assertEquals(expectedError.asUiText(), snackbarList.last().message)

        // clean up
        snackbarJob.cancel()
    }

    @Test
    fun `계정 정보 로드 성공 후 계정 정보 화면으로 이동 시 NavigateToAccountInfo 이펙트가 발생한다`() {
        testEffect(
            viewModel = viewModel,
            intents = listOf(SettingContract.UiEvent.OnNavigateToAccountInfo),
            assertions = listOf(
                SettingContract.UiEffect.NavigateToAccountInfo(TestAccountInfo.toUiModel()),
            ),
        )
    }

    @Test
    fun `계정 정보 로드 실패 후 계정 정보 화면으로 이동 시 NavigateToAccountInfo 이펙트가 발생하지 않는다`() {
        // given
        every {
            usecases.getAccountInfo()
        } returns flowOf(Result.Error(SettingErrorType.Unexpected(null)))
        viewModel = SettingViewModel(usecases, snackbarController)

        testEffect(
            viewModel = viewModel,
            intents = listOf(SettingContract.UiEvent.OnNavigateToAccountInfo),
            assertions = listOf(), // 이펙트 없음
        )
    }

    companion object {
        private val TestAccountInfo = AccountInfo(
            userId = UserId(1L),
            displayId = DisplayId("did"),
            name = Name("name"),
            profileImageUrl = "image.jpg",
            introduce = Introduce("hello"),
            loginProvider = SocialLoginProvider.GOOGLE,
        )
    }
}

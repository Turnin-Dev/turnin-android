package com.peekr.presentation.keywordDetail.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.core.presentation.util.UiText
import com.peekr.domain.keywordDetail.error.KeywordDetailErrorType
import com.peekr.domain.keywordDetail.usecase.GetDescriptionUseCase
import com.peekr.domain.keywordDetail.usecase.GetUserIdUseCase
import com.peekr.domain.keywordDetail.usecase.UpdateDescriptionUseCase
import com.peekr.presentation.keywordDetail.error.asUiText
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class KeywordDetailViewModelTest : MVIBaseViewModelTest<
    KeywordDetailContract.UiState,
    KeywordDetailContract.UiEvent,
    KeywordDetailContract.UiEffect,
    KeywordDetailViewModel,
>() {
    private val getUserIdUseCase: GetUserIdUseCase = mockk()
    private val getDescriptionUseCase: GetDescriptionUseCase = mockk()
    private val updateDescriptionUseCase: UpdateDescriptionUseCase = mockk()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: KeywordDetailViewModel

    @Before
    fun setUp() {
        savedStateHandle = TestSavedStateHandle
        every { getUserIdUseCase() } returns flow {
            emit(Result.Loading)
            emit(Result.Success(TestUserId))
        }
        every {
            getDescriptionUseCase(TestUserKeywordId.value)
        } returns flow {
            emit(Result.Loading)
            emit(Result.Success(TestDescription))
        }
        every {
            updateDescriptionUseCase(TestUserKeywordId.value, TestDescription.value)
        } returns flowOf(Result.Success(TestPatchDescription))

        viewModel = KeywordDetailViewModel(
            getUserIdUseCase = getUserIdUseCase,
            getDescriptionUseCase = getDescriptionUseCase,
            updateDescriptionUseCase = updateDescriptionUseCase,
            savedStateHandle = savedStateHandle,
        )
    }

    @Test
    fun `초기 데이터 준비 성공 테스트`() {
        testState(
            viewModel = viewModel,
            assertAllState = true,
            intents = emptyList(),
            assertions = listOf(
                KeywordDetailContract.UiState(),
                TestInitialUiState,
            ),
        )
    }

    @Test
    fun `초기 데이터 준비 실패 테스트 - NavArgs 값 중 존재하지 않는 값이 있을 때 에러를 발생시킨다`() {
        savedStateHandle = SavedStateHandle()
        viewModel = KeywordDetailViewModel(
            getUserIdUseCase = getUserIdUseCase,
            getDescriptionUseCase = getDescriptionUseCase,
            updateDescriptionUseCase = updateDescriptionUseCase,
            savedStateHandle = savedStateHandle,
        )

        testEffect(
            viewModel = viewModel,
            assertTypeOnly = true,
            intents = emptyList(),
            assertions = listOf(
                KeywordDetailContract.UiEffect.FullScreenError(UiText.DynamicString("")),
            ),
        )
    }

    @Test
    fun `키워드 설명 수정에 성공하면 로딩, 에러, 수정 모드 상태를 false로 변환한다`() {
        testState(
            viewModel = viewModel,
            intents = listOf(
                KeywordDetailContract.UiEvent.UpdateDescription(TestDescription.value),
            ),
            assertions = listOf(
                TestInitialUiState.copy(
                    loading = false,
                    error = null,
                    editMode = false,
                ),
            ),
        )
    }

    @Test
    fun `키워드 설명 수정에 실패하면 에러가 발생한다`() {
        val expectedError = KeywordDetailErrorType.Unexpected(null)
        every {
            updateDescriptionUseCase(TestUserKeywordId.value, TestDescription.value)
        } returns flowOf(Result.Error(expectedError))

        testState(
            viewModel = viewModel,
            assertAllState = true,
            intents = listOf(
                KeywordDetailContract.UiEvent.UpdateDescription(TestDescription.value),
            ),
            assertions = listOf(
                KeywordDetailContract.UiState(),
                TestInitialUiState,
                TestInitialUiState.copy(
                    error = expectedError.asUiText(),
                ),
            ),
        )
    }

    companion object {
        private const val TEST_KEYWORD = "sample"
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestDescription = KeywordDescription("sample")
        private val TestPatchDescription = PatchDescription(TestDescription)
        private val TestSavedStateHandle = SavedStateHandle(
            mapOf(
                "userKeywordId" to TestUserKeywordId.value,
                "userId" to TestUserId.value,
                "keyword" to TEST_KEYWORD,
            ),
        )
        private val TestInitialUiState = KeywordDetailContract.UiState(
            keyword = TEST_KEYWORD,
            description = TextFieldValue(
                text = TestDescription.value,
                selection = TextRange(TestDescription.value.length),
            ),
            myKeyword = true,
        )
    }
}

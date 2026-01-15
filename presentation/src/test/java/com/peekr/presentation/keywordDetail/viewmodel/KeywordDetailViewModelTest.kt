package com.peekr.presentation.keywordDetail.viewmodel

import com.peekr.core.presentation.MVIBaseViewModelTest
import com.peekr.presentation.keywordDetail.state.KeywordDetailContract

class KeywordDetailViewModelTest : MVIBaseViewModelTest<
    KeywordDetailContract.UiState,
    KeywordDetailContract.UiEvent,
    KeywordDetailContract.UiEffect,
    KeywordDetailViewModel,
>() {
//    private val checkMyKeywordUseCase: CheckMyKeywordUseCase = mockk()
//    private val getDescriptionUseCase: GetDescriptionUseCase = mockk()
//    private val updateDescriptionUseCase: UpdateDescriptionUseCase = mockk()
//    private lateinit var savedStateHandle: SavedStateHandle
//    private lateinit var viewModel: KeywordDetailViewModel
//
//    @Before
//    fun setUp() {
//        savedStateHandle = TestSavedStateHandle
//        every { checkMyKeywordUseCase() } returns flow {
//            emit(Result.Loading)
//            emit(Result.Success(TestUserId))
//        }
//        every {
//            getDescriptionUseCase(TestUserKeywordId.value)
//        } returns flow {
//            emit(Result.Loading)
//            emit(Result.Success(TestDescription))
//        }
//        every {
//            updateDescriptionUseCase(TestUserKeywordId.value, TestDescription.value)
//        } returns flowOf(Result.Success(TestPatchDescription))
//
//        viewModel = KeywordDetailViewModel(
//            checkMyKeywordUseCase = checkMyKeywordUseCase,
//            getDescriptionUseCase = getDescriptionUseCase,
//            updateDescriptionUseCase = updateDescriptionUseCase,
//            savedStateHandle = savedStateHandle,
//        )
//    }
//
//    @Test
//    fun `초기 데이터 준비 성공 테스트`() {
//        testState(
//            viewModel = viewModel,
//            assertAllState = true,
//            intents = emptyList(),
//            assertions = listOf(
//                KeywordDetailContract.UiState(),
//                TestInitialUiState,
//            ),
//        )
//    }
//
//    @Test
//    fun `초기 데이터 준비 실패 테스트 - NavArgs 값 중 존재하지 않는 값이 있을 때 에러를 발생시킨다`() {
//        savedStateHandle = SavedStateHandle()
//        viewModel = KeywordDetailViewModel(
//            checkMyKeywordUseCase = checkMyKeywordUseCase,
//            getDescriptionUseCase = getDescriptionUseCase,
//            updateDescriptionUseCase = updateDescriptionUseCase,
//            savedStateHandle = savedStateHandle,
//        )
//
//        testEffect(
//            viewModel = viewModel,
//            assertTypeOnly = true,
//            intents = emptyList(),
//            assertions = listOf(
//                KeywordDetailContract.UiEffect.FullScreenError(UiText.DynamicString("")),
//            ),
//        )
//    }
//
//    @Test
//    fun `키워드 설명 수정에 성공하면 로딩, 에러, 수정 모드 상태를 false로 변환한다`() {
//        testState(
//            viewModel = viewModel,
//            intents = listOf(
//                KeywordDetailContract.UiEvent.UpdateDescription(TestDescription.value),
//            ),
//            assertions = listOf(
//                TestInitialUiState.copy(
//                    loading = false,
//                    error = null,
//                    editMode = false,
//                ),
//            ),
//        )
//    }
//
//    @Test
//    fun `키워드 설명 수정에 실패하면 에러가 발생한다`() {
//        val expectedError = KeywordDetailErrorType.Unexpected(null)
//        every {
//            updateDescriptionUseCase(TestUserKeywordId.value, TestDescription.value)
//        } returns flowOf(Result.Error(expectedError))
//
//        testState(
//            viewModel = viewModel,
//            assertAllState = true,
//            intents = listOf(
//                KeywordDetailContract.UiEvent.UpdateDescription(TestDescription.value),
//            ),
//            assertions = listOf(
//                KeywordDetailContract.UiState(),
//                TestInitialUiState,
//                TestInitialUiState.copy(
//                    error = expectedError.asUiText(),
//                ),
//            ),
//        )
//    }
//
//    companion object {
//        private const val TEST_KEYWORD = "sample"
//        private val TestUserId = UserId(1L)
//        private val TestUserKeywordId = UserKeywordId(1L)
//        private val TestDescription = KeywordDescription("sample")
//        private val TestPatchDescription = PatchDescription(TestDescription)
//        private val TestSavedStateHandle = SavedStateHandle(
//            mapOf(
//                "userKeywordId" to TestUserKeywordId.value,
//                "userId" to TestUserId.value,
//                "keyword" to TEST_KEYWORD,
//            ),
//        )
//        private val TestInitialUiState = KeywordDetailContract.UiState(
//            keyword = TEST_KEYWORD,
//            description = TextFieldValue(
//                text = TestDescription.value,
//                selection = TextRange(TestDescription.value.length),
//            ),
//            myKeyword = true,
//        )
//    }
}

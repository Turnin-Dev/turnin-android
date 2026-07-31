package com.turnin.presentation.home.viewmodel

import androidx.paging.PagingData
import com.turnin.core.domain.feed.model.Feed
import com.turnin.core.domain.feed.model.FeedType
import com.turnin.core.domain.model.KeywordDescription
import com.turnin.core.domain.model.KeywordId
import com.turnin.core.domain.model.KeywordName
import com.turnin.core.domain.model.Name
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.model.UserKeywordId
import com.turnin.core.presentation.MainDispatcherRule
import com.turnin.domain.home.usecase.GetFeedsUseCase
import com.turnin.presentation.home.model.toUiModel
import com.turnin.presentation.util.MockLog
import com.turnin.presentation.util.collectDataForTest
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val getFeedsUseCase: GetFeedsUseCase = mockk()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        MockLog.mock()
    }

    @After
    fun teardown() {
        clearAllMocks()
        MockLog.cleanUp()
    }

    @Test
    fun `allFeedsPagingData 초기 로드 성공 테스트`() = runTest(dispatcherRule.testDispatcher) {
        // given
        val expectedFeeds = createFeeds(5)
        every { getFeedsUseCase(FeedType.ALL) } returns flowOf(PagingData.from(expectedFeeds))

        viewModel = HomeViewModel(getFeedsUseCase)
        val expectedUiFeeds = expectedFeeds.map { it.toUiModel() }

        // when
        val actualList = viewModel.allFeedsPagingData.first()
            .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)

        // then
        assertEquals(expectedUiFeeds.size, actualList.size)
        assertEquals(expectedUiFeeds, actualList)
    }

    @Test
    fun `allFeedsPagingData 에러 발생 시 빈 페이징 데이터를 반환한다`() = runTest(dispatcherRule.testDispatcher) {
        // given
        every { getFeedsUseCase(FeedType.ALL) } returns flow { throw Exception("Test Error") }

        viewModel = HomeViewModel(getFeedsUseCase)

        // when
        val actualList = viewModel.allFeedsPagingData.first()
            .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)

        // then
        assertTrue(actualList.isEmpty())
    }

    @Test
    fun `initialLoadFriendsPagingData 호출 시 friendsPagingData가 로드된다`() = runTest(dispatcherRule.testDispatcher) {
        // given
        val expectedFeeds = createFeeds(3)
        every { getFeedsUseCase(FeedType.ALL) } returns flowOf(PagingData.empty())
        every { getFeedsUseCase(FeedType.FRIEND) } returns flowOf(PagingData.from(expectedFeeds))

        viewModel = HomeViewModel(getFeedsUseCase)
        val expectedUiFeeds = expectedFeeds.map { it.toUiModel() }

        // when
        viewModel.initialLoadFriendsPagingData()
        advanceUntilIdle()

        val actualList = viewModel.friendsPagingData.first()
            .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)

        // then
        assertEquals(expectedUiFeeds.size, actualList.size)
        assertEquals(expectedUiFeeds, actualList)
    }

    @Test
    fun `friendsPagingData 에러 발생 시 빈 페이징 데이터를 반환한다`() = runTest(dispatcherRule.testDispatcher) {
        // given
        every { getFeedsUseCase(FeedType.ALL) } returns flowOf(PagingData.empty())
        every { getFeedsUseCase(FeedType.FRIEND) } returns flow { throw Exception("Friend Feed Error") }

        viewModel = HomeViewModel(getFeedsUseCase)

        // when
        viewModel.initialLoadFriendsPagingData()
        advanceUntilIdle()

        val actualList = viewModel.friendsPagingData.first()
            .collectDataForTest(dispatcherRule.testDispatcher, dispatcherRule.testDispatcher)

        // then
        assertTrue(actualList.isEmpty())
    }

    private fun createFeeds(count: Int): List<Feed> = List(count) {
        Feed(
            userKeywordId = UserKeywordId.from(it.toLong() + 1),
            userId = UserId.from(it.toLong() + 1),
            userName = Name.from("User$it"),
            profileImageUrl = null,
            keywordId = KeywordId.from(it.toLong() + 1),
            keyword = KeywordName.from("Keyword$it"),
            description = KeywordDescription("Description$it"),
            createdAt = System.currentTimeMillis(),
            sortOrder = it,
        )
    }
}

package com.turnin.core.data.repository

import androidx.paging.testing.asSnapshot
import com.turnin.core.data.MockLog
import com.turnin.core.data.source.local.memory.MemoryCache
import com.turnin.core.data.source.network.datasource.BlockNetworkDataSource
import com.turnin.core.data.source.network.dto.block.response.BlockReasonResponse
import com.turnin.core.data.source.network.dto.block.response.BlockedUserCursorPageResponse
import com.turnin.core.data.source.network.dto.block.response.BlockedUserResponse
import com.turnin.core.data.source.network.dto.block.response.toDomainModel
import com.turnin.core.data.source.network.error.NetworkErrorType
import com.turnin.core.data.source.network.error.toCommonErrorType
import com.turnin.core.data.source.network.util.NetworkResult
import com.turnin.core.domain.block.model.BlockReasonId
import com.turnin.core.domain.block.model.CreateBlock
import com.turnin.core.domain.common.Result
import com.turnin.core.domain.model.BlockId
import com.turnin.core.domain.model.UserId
import com.turnin.core.domain.user.model.CoreUserProfile
import com.turnin.core.domain.userKeyword.model.UserKeywordDetail
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * BlockRepository + 페이징 테스트가 포함
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BlockRepositoryImplTest {
    private val dataSource: BlockNetworkDataSource = mockk()
    private val userMemoryCache: MemoryCache<UserId, CoreUserProfile> = mockk()
    private val keywordMemoryCache: MemoryCache<UserId, List<UserKeywordDetail>> = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository =
        BlockRepositoryImpl(dataSource, userMemoryCache, keywordMemoryCache, dispatcher)

    @Before
    fun setUp() {
        every { userMemoryCache.remove(any()) } returns null
        every { keywordMemoryCache.remove(any()) } returns null
        coEvery {
            dataSource.getBlockReasons()
        } returns NetworkResult.Success(listOf(TestBlockReasonResponse))
        coEvery {
            dataSource.deleteBlock(any())
        } returns NetworkResult.Success(Unit)
        coEvery {
            dataSource.getBlockedUsers(any(), any())
        } returns NetworkResult.Success(TestBlockedUsersResponse)
        coEvery {
            dataSource.createBlock(any())
        } returns NetworkResult.Success(Unit)

        MockLog.mock()
    }

    @After
    fun teardown() {
        MockLog.cleanUp()
    }

    @Test
    fun `차단 목록 조회 - 초기 호출 성공 시 도메인 모델로 변환된 데이터를 반환한다`() = runTest {
        // given: 2페이지 테스트 데이터 설정
        val pageSize = 5
        val expectedCursorPage1 = createCursorPageResponse(nextCursor = 5L, pageSize)
        val expectedCursorPage2 = createCursorPageResponse(nextCursor = null, pageSize)

        coEvery {
            dataSource.getBlockedUsers(any(), any())
        } answers {
            val cursor = firstArg<Long?>()
            when (cursor) {
                null -> NetworkResult.Success(expectedCursorPage1)
                5L -> NetworkResult.Success(expectedCursorPage2)
                else -> NetworkResult.Success(
                    BlockedUserCursorPageResponse(items = emptyList(), nextCursor = null),
                )
            }
        }

        // when: 조회(페이징) 진행
        val blockedUsers = repository.getBlockedUsers().asSnapshot()

        // then
        assertEquals(pageSize * 2, blockedUsers.size)
        assertEquals(expectedCursorPage1.items.first().toDomainModel(), blockedUsers.first())
    }

    @Test
    fun `차단 사유 목록 조회 - 성공 테스트`() = runTest {
        // given
        val expectedReasons = listOf(TestBlockReasonResponse)
        coEvery {
            dataSource.getBlockReasons()
        } returns NetworkResult.Success(expectedReasons)

        // when
        val result = repository.getBlockReasons().last()

        // then
        val success = result as Result.Success
        assertEquals(success.data, expectedReasons.map { it.toDomainModel() })
    }

    @Test
    fun `차단 사유 목록 조회 - 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.getBlockReasons()
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getBlockReasons().last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    @Test
    fun `차단 생성 - 성공 테스트`() = runTest {
        // given
        val createBlock = CreateBlock(
            blockerId = UserId(1L),
            blockedId = UserId(2L),
            reasonId = BlockReasonId(1L),
            customReason = "custom-reason",
        )

        // when
        val result = repository.createBlock(createBlock).last()

        // then
        assertTrue(result is Result.Success)
        verify(exactly = 1) { userMemoryCache.remove(any()) }
        verify(exactly = 1) { keywordMemoryCache.remove(any()) }
    }

    @Test
    fun `차단 생성 - 에러 발생 시 캐시를 무효화하고 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.createBlock(any())
        } returns NetworkResult.Error(expectedError)
        val createBlock = CreateBlock(
            blockerId = UserId(1L),
            blockedId = UserId(2L),
            reasonId = BlockReasonId(1L),
            customReason = "custom-reason",
        )

        // when
        val result = repository.createBlock(createBlock).last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
        verify(exactly = 1) { userMemoryCache.remove(any()) }
        verify(exactly = 1) { keywordMemoryCache.remove(any()) }
    }

    @Test
    fun `차단 삭제 - 성공 테스트`() = runTest {
        // when
        val result = repository.deleteBlock(BlockId(1L), UserId(1L)).last()

        // then
        assertTrue(result is Result.Success)
        verify(exactly = 1) { userMemoryCache.remove(any()) }
        verify(exactly = 1) { keywordMemoryCache.remove(any()) }
    }

    @Test
    fun `차단 삭제 - 에러 발생 시 캐시를 무효화하고 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.deleteBlock(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.deleteBlock(BlockId(1L), UserId(1L)).last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
        verify(exactly = 1) { userMemoryCache.remove(any()) }
        verify(exactly = 1) { keywordMemoryCache.remove(any()) }
    }

    companion object {
        /**
         * 테스트용 커서 페이지 응답 바디 생성기
         *
         * 목록 데이터는 중요하지 않고 다음 커서를 직접 설정해서 테스트를 진행한다.
         */
        private fun createCursorPageResponse(
            nextCursor: Long?,
            pageSize: Int,
        ): BlockedUserCursorPageResponse =
            BlockedUserCursorPageResponse(
                items = List(pageSize) {
                    BlockedUserResponse(
                        id = it.toLong(),
                        userId = it.toLong(),
                        displayId = "displayId$it",
                        name = "name$it",
                        profileImageUrl = null,
                    )
                },
                nextCursor = nextCursor,
            )

        private val TestBlockedUserResponse = BlockedUserResponse(
            id = 1L,
            userId = 2L,
            displayId = "did",
            name = "name",
            profileImageUrl = null,
        )
        private val TestBlockedUsersResponse = BlockedUserCursorPageResponse(
            items = listOf(TestBlockedUserResponse),
            nextCursor = TestBlockedUserResponse.id,
        )
        private val TestBlockReasonResponse = BlockReasonResponse(
            id = 1L,
            code = "code",
            description = "desc",
        )
    }
}

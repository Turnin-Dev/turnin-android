package com.peekr.core.data.repository

import androidx.paging.testing.asSnapshot
import com.peekr.core.data.MockLog
import com.peekr.core.data.source.network.datasource.BlockNetworkDataSource
import com.peekr.core.data.source.network.dto.block.request.BlockRequest
import com.peekr.core.data.source.network.dto.block.response.BlockReasonResponse
import com.peekr.core.data.source.network.dto.block.response.BlockedUserResponse
import com.peekr.core.data.source.network.dto.block.response.BlockedUsersResponse
import com.peekr.core.data.source.network.dto.block.response.toDomainModel
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.block.model.BlockPagingTokens
import com.peekr.core.domain.block.model.BlockReasonId
import com.peekr.core.domain.block.model.CreateBlock
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.BlockId
import com.peekr.core.domain.model.UserId
import io.mockk.coEvery
import io.mockk.mockk
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
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = BlockRepositoryImpl(dataSource, dispatcher)

    @Before
    fun setUp() {
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
        // given
        val pageSize = BlockPagingTokens.PAGE_SIZE
        val expectedFirstPage = createBlockResponseList(1, pageSize).map { it.toDomainModel() }

        // 첫 번째 페이지 설정 (page=1, size=20)
        coEvery {
            dataSource.getBlockedUsers(1, pageSize)
        } returns NetworkResult.Success(
            createBlocksResponse(
                pageNumber = 1L,
                startId = 1L,
                count = pageSize,
                hasNext = true,
            ),
        )

        // 두 번쨰 페이지 설정 (page=2, size=20)
        // Paging Source는 initialLoadSize(30)를 채우기 위해 2페이지를 요청할 것으로 예상
        coEvery {
            dataSource.getBlockedUsers(2, pageSize)
        } returns NetworkResult.Success(
            createBlocksResponse(
                pageNumber = 2L,
                startId = pageSize + 1L,
                count = pageSize,
                hasNext = true,
            ),
        )

        // when
        val pagingData = repository.getBlockedUsers().asSnapshot()

        // then
        assertEquals(pageSize * 2, pagingData.size)
        assertEquals(expectedFirstPage.first().id, pagingData.first().id)
        val expectedLastId = pageSize * 2
        assertEquals(expectedLastId.toLong(), pagingData.last().id.value)
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
    }

    @Test
    fun `차단 생성 - 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
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
    }

    @Test
    fun `차단 삭제 - 성공 테스트`() = runTest {
        // when
        val result = repository.deleteBlock(BlockId(1L)).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `차단 삭제 - 에러 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.deleteBlock(any())
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.deleteBlock(BlockId(1L)).last()

        // then
        val error = result as Result.Error
        assertEquals(expectedError.toCommonErrorType(), error.error)
    }

    companion object {
        private fun createBlocksResponse(
            pageNumber: Long,
            startId: Long,
            count: Int,
            hasNext: Boolean,
        ): BlockedUsersResponse = BlockedUsersResponse(
            pageNumber = pageNumber,
            pageSize = count,
            hasNext = hasNext,
            list = createBlockResponseList(startId, count),
        )

        private fun createBlockResponseList(
            startId: Long,
            count: Int,
        ): List<BlockedUserResponse> =
            (startId until startId + count).map { id ->
                BlockedUserResponse(
                    id = id,
                    userId = id + 1L,
                    displayId = "did$id",
                    name = "name$id",
                    profileImageUrl = null,
                )
            }

        private val TestBlockRequest = BlockRequest(
            blockerId = 1L,
            blockedId = 2L,
            reasonId = 1L,
            customReason = "custom-reason",
        )
        private val TestBlockedUserResponse = BlockedUserResponse(
            id = 1L,
            userId = 2L,
            displayId = "did",
            name = "name",
            profileImageUrl = null,
        )
        private val TestBlockedUsersResponse = BlockedUsersResponse(
            pageNumber = 1L,
            pageSize = 20,
            hasNext = true,
            list = listOf(TestBlockedUserResponse),
        )
        private val TestBlockReasonResponse = BlockReasonResponse(
            id = 1L,
            code = "code",
            description = "desc",
        )
    }
}

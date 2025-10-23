package com.peekr.core.data.keyword.repository

import com.peekr.core.data.keyword.network.KeywordDataSource
import com.peekr.core.data.keyword.network.request.CreateKeywordRequest
import com.peekr.core.data.keyword.network.response.KeywordResponse
import com.peekr.core.data.keyword.network.response.toDomainModel
import com.peekr.core.data.network.error.NetworkErrorType
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.domain.keyword.error.KeywordErrorType
import com.peekr.core.domain.keyword.repository.KeywordRepository
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class KeywordRepositoryImplTest {
    private val dataSource: KeywordDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: KeywordRepository = KeywordRepositoryImpl(dataSource, dispatcher)

    @Test
    fun `키워드 ID로 키워드 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getKeywordById(TestKeywordId)
        } returns NetworkResult.Success(TestKeywordResponse)

        // when
        val result = repository.getKeywordById(TestKeywordId).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TestKeywordResponse.toDomainModel(), (result as Result.Success).data)
    }

    @Test
    fun `키워드 ID로 키워드 조회 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.Forbidden
        coEvery {
            dataSource.getKeywordById(TestKeywordId)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getKeywordById(TestKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            KeywordErrorType.CommonError(expectedError.toCommonErrorType()),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `키워드 ID로 키워드 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.getKeywordById(TestKeywordId)
        } throws exception

        // when
        val result = repository.getKeywordById(TestKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is KeywordErrorType.Unexpected) {
            assertEquals(
                KeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as KeywordErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `키워드 명으로 키워드 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getKeywordByName(TEST_KEYWORD)
        } returns NetworkResult.Success(TestKeywordResponse)

        // when
        val result = repository.getKeywordByName(TEST_KEYWORD).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TestKeywordResponse.toDomainModel(), (result as Result.Success).data)
    }

    @Test
    fun `키워드 명으로 키워드 조회 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.Forbidden
        coEvery {
            dataSource.getKeywordByName(TEST_KEYWORD)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getKeywordByName(TEST_KEYWORD).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            KeywordErrorType.CommonError(expectedError.toCommonErrorType()),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `키워드 명으로 키워드 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery { dataSource.getKeywordByName(TEST_KEYWORD) } throws exception

        // when
        val result = repository.getKeywordByName(TEST_KEYWORD).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is KeywordErrorType.Unexpected) {
            assertEquals(
                KeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as KeywordErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `키워드 생성 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.createKeyword(TestCreateKeywordRequest)
        } returns NetworkResult.Success(TestKeywordResponse)

        // when
        val result = repository.createKeyword(TEST_KEYWORD).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(TestKeywordResponse.toDomainModel(), (result as Result.Success).data)
    }

    @Test
    fun `키워드 생성 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.Forbidden
        coEvery {
            dataSource.createKeyword(TestCreateKeywordRequest)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.createKeyword(TEST_KEYWORD).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            KeywordErrorType.CommonError(expectedError.toCommonErrorType()),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `키워드 생성 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.createKeyword(TestCreateKeywordRequest)
        } throws exception

        // when
        val result = repository.createKeyword(TEST_KEYWORD).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is KeywordErrorType.Unexpected) {
            assertEquals(
                KeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as KeywordErrorType.Unexpected).cause?.message,
            )
        }
    }

    companion object {
        private val TestKeywordId = KeywordId(1L)
        private const val TEST_KEYWORD = "sample"
        private val TestKeywordResponse = KeywordResponse(
            id = TestKeywordId.value,
            keyword = TEST_KEYWORD,
            createdBy = 1L,
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestCreateKeywordRequest = CreateKeywordRequest(TEST_KEYWORD)
    }
}

package com.peekr.data.userKeyword.repository

import com.peekr.core.data.network.util.NetworkErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.network.util.toErrorType
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.util.ErrorType
import com.peekr.core.domain.util.Result
import com.peekr.data.userKeyword.model.request.CreateUserKeywordRequest
import com.peekr.data.userKeyword.model.request.PatchUserKeywordRequest
import com.peekr.data.userKeyword.model.response.UserKeywordResponse
import com.peekr.data.userKeyword.model.response.UserKeywordsResponse
import com.peekr.data.userKeyword.model.response.toDomainModel
import com.peekr.data.userKeyword.network.UserKeywordDataSource
import com.peekr.domain.userKeyword.model.CreateUserKeyword
import com.peekr.domain.userKeyword.model.PatchUserKeyword
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
class UserKeywordRepositoryImplTest {
    private val dataSource: UserKeywordDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = UserKeywordRepositoryImpl(dataSource, dispatcher)

    @Test
    fun `사용자 키워드 리스트 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getUserKeywords(TestUserId)
        } returns NetworkResult.Success(TestUserKeywordsResponse)

        // when
        val result = repository.getUserKeywords(TestUserId).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestUserKeywordsResponse.toDomainModel(),
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 키워드 리스트 조회 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.NotFound
        coEvery {
            dataSource.getUserKeywords(TestUserId)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getUserKeywords(TestUserId).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 리스트 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.getUserKeywords(TestUserId)
        } throws exception

        // when
        val result = repository.getUserKeywords(TestUserId).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is ErrorType.Unexpected) {
            assertEquals(
                ErrorType.Unexpected(exception).cause?.message,
                (result.error as ErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 생성 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.createUserKeyword(TestCreateUserKeywordRequest)
        } returns NetworkResult.Success(TestUserKeywordResponse)

        // when
        val result = repository.createUserKeyword(TestCreateUserKeyword).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestUserKeywordResponse.toDomainModel(),
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 키워드 생성 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.NotFound
        coEvery {
            dataSource.createUserKeyword(TestCreateUserKeywordRequest)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.createUserKeyword(TestCreateUserKeyword).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 생성 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.createUserKeyword(TestCreateUserKeywordRequest)
        } throws exception

        // when
        val result = repository.createUserKeyword(TestCreateUserKeyword).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is ErrorType.Unexpected) {
            assertEquals(
                ErrorType.Unexpected(exception).cause?.message,
                (result.error as ErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 수정 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.patchUserKeyword(
                ownerId = TestUserId,
                userKeywordId = TestUserKeywordId,
                patchUserKeywordRequest = TestPatchUserKeywordRequest,
            )
        } returns NetworkResult.Success(Unit)

        // when
        val result = repository
            .patchUserKeyword(
                userId = TestUserId,
                userKeywordId = TestUserKeywordId,
                patch = TestPatchUserKeyword,
            ).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 키워드 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.NotFound
        coEvery {
            dataSource.patchUserKeyword(
                ownerId = TestUserId,
                userKeywordId = TestUserKeywordId,
                patchUserKeywordRequest = TestPatchUserKeywordRequest,
            )
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository
            .patchUserKeyword(
                userId = TestUserId,
                userKeywordId = TestUserKeywordId,
                patch = TestPatchUserKeyword,
            ).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.patchUserKeyword(
                ownerId = TestUserId,
                userKeywordId = TestUserKeywordId,
                patchUserKeywordRequest = TestPatchUserKeywordRequest,
            )
        } throws exception

        // when
        val result = repository
            .patchUserKeyword(
                userId = TestUserId,
                userKeywordId = TestUserKeywordId,
                patch = TestPatchUserKeyword,
            ).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is ErrorType.Unexpected) {
            assertEquals(
                ErrorType.Unexpected(exception).cause?.message,
                (result.error as ErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 삭제 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.deleteUserKeyword(
                ownerId = TestUserId,
                userKeywordId = TestUserKeywordId,
            )
        } returns NetworkResult.Success(Unit)

        // when
        val result = repository
            .deleteUserKeyword(
                userId = TestUserId,
                userKeywordId = TestUserKeywordId,
            ).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 키워드 삭제 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.NotFound
        coEvery {
            dataSource.deleteUserKeyword(
                ownerId = TestUserId,
                userKeywordId = TestUserKeywordId,
            )
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository
            .deleteUserKeyword(
                userId = TestUserId,
                userKeywordId = TestUserKeywordId,
            ).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 삭제 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.deleteUserKeyword(
                ownerId = TestUserId,
                userKeywordId = TestUserKeywordId,
            )
        } throws exception

        // when
        val result = repository
            .deleteUserKeyword(
                userId = TestUserId,
                userKeywordId = TestUserKeywordId,
            ).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is ErrorType.Unexpected) {
            assertEquals(
                ErrorType.Unexpected(exception).cause?.message,
                (result.error as ErrorType.Unexpected).cause?.message,
            )
        }
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestKeywordId = KeywordId(1L)
        private val TestUserKeywordResponse = UserKeywordResponse(
            id = TestUserKeywordId.value,
            keywordId = TestKeywordId.value,
            userId = TestUserId.value,
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywordsResponse = UserKeywordsResponse(
            keywords = listOf(TestUserKeywordResponse),
        )
        private val TestCreateUserKeywordRequest = CreateUserKeywordRequest(
            userId = TestUserId.value,
            keywordId = TestKeywordId.value,
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
        )
        private val TestCreateUserKeyword = CreateUserKeyword(
            userId = TestUserId,
            keywordId = TestKeywordId,
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
        )
        private val TestPatchUserKeywordRequest = PatchUserKeywordRequest(
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
        )
        private val TestPatchUserKeyword = PatchUserKeyword(
            offsetX = 0.0,
            offsetY = 0.0,
            description = "sample",
        )
    }
}

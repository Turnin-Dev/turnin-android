package com.peekr.core.data.userKeyword.repository

import com.peekr.core.data.repository.UserKeywordRepositoryImpl
import com.peekr.core.data.source.network.datasource.UserKeywordNetworkDataSource
import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchDescriptionRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchOffsetRequest
import com.peekr.core.data.source.network.dto.userKeyword.response.DescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchDescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchOffsetResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordsResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.toDomainModel
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordValue
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.error.UserKeywordErrorType
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchDescription
import com.peekr.core.domain.userKeyword.model.PatchOffset
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
    private val dataSource: UserKeywordNetworkDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = UserKeywordRepositoryImpl(dataSource, dispatcher)

    @Test
    fun `사용자 키워드 리스트 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getUserKeywords()
        } returns NetworkResult.Success(TestUserKeywordsResponse)

        // when
        val result = repository.getUserKeywords().last()

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
            dataSource.getUserKeywords()
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getUserKeywords().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            UserKeywordErrorType.CommonError(expectedError.toCommonErrorType()),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 리스트 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.getUserKeywords()
        } throws exception

        // when
        val result = repository.getUserKeywords().last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is UserKeywordErrorType.Unexpected) {
            assertEquals(
                UserKeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as UserKeywordErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 설명 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getDescription(TestUserKeywordId)
        } returns NetworkResult.Success(TestDescriptionResponse)

        // when
        val result = repository.getDescription(TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestDescriptionResponse.toDomainModel(),
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 키워드 설명 조회 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.NotFound
        coEvery {
            dataSource.getDescription(TestUserKeywordId)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getDescription(TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            UserKeywordErrorType.CommonError(expectedError.toCommonErrorType()),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 설명 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.getDescription(TestUserKeywordId)
        } throws exception

        // when
        val result = repository.getDescription(TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is UserKeywordErrorType.Unexpected) {
            assertEquals(
                UserKeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as UserKeywordErrorType.Unexpected).cause?.message,
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
            UserKeywordErrorType.CommonError(expectedError.toCommonErrorType()),
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
        if (result is Result.Error && result.error is UserKeywordErrorType.Unexpected) {
            assertEquals(
                UserKeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as UserKeywordErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 오프셋 수정 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.patchOffset(
                userKeywordId = TestUserKeywordId,
                patchOffsetRequest = TestPatchOffsetRequest,
            )
        } returns NetworkResult.Success(TestPatchOffsetResponse)

        // when
        val result = repository
            .patchOffset(
                userKeywordId = TestUserKeywordId,
                patchOffset = TestPatchOffset,
            ).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestPatchOffset,
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 키워드 오프셋 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.NotFound
        coEvery {
            dataSource.patchOffset(
                userKeywordId = TestUserKeywordId,
                patchOffsetRequest = TestPatchOffsetRequest,
            )
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository
            .patchOffset(
                userKeywordId = TestUserKeywordId,
                patchOffset = TestPatchOffset,
            ).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            UserKeywordErrorType.CommonError(expectedError.toCommonErrorType()),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 오프셋 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.patchOffset(
                userKeywordId = TestUserKeywordId,
                patchOffsetRequest = TestPatchOffsetRequest,
            )
        } throws exception

        // when
        val result = repository
            .patchOffset(
                userKeywordId = TestUserKeywordId,
                patchOffset = TestPatchOffset,
            ).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is UserKeywordErrorType.Unexpected) {
            assertEquals(
                UserKeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as UserKeywordErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 설명 수정 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescriptionRequest = TestPatchDescriptionRequest,
            )
        } returns NetworkResult.Success(TestPatchDescriptionResponse)

        // when
        val result = repository
            .patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescription = TestPatchDescription,
            ).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestPatchDescription,
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 키워드 설명 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.NotFound
        coEvery {
            dataSource.patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescriptionRequest = TestPatchDescriptionRequest,
            )
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository
            .patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescription = TestPatchDescription,
            ).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            UserKeywordErrorType.CommonError(expectedError.toCommonErrorType()),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 설명 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescriptionRequest = TestPatchDescriptionRequest,
            )
        } throws exception

        // when
        val result = repository
            .patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescription = TestPatchDescription,
            ).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is UserKeywordErrorType.Unexpected) {
            assertEquals(
                UserKeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as UserKeywordErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 삭제 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)
        } returns NetworkResult.Success(Unit)

        // when
        val result = repository.deleteUserKeyword(userKeywordId = TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 키워드 삭제 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.NotFound
        coEvery {
            dataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.deleteUserKeyword(userKeywordId = TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            UserKeywordErrorType.CommonError(expectedError.toCommonErrorType()),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 삭제 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)
        } throws exception

        // when
        val result = repository.deleteUserKeyword(userKeywordId = TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is UserKeywordErrorType.Unexpected) {
            assertEquals(
                UserKeywordErrorType.Unexpected(exception).cause?.message,
                (result.error as UserKeywordErrorType.Unexpected).cause?.message,
            )
        }
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestKeywordId = KeywordId(1L)
        private val TestKeyword = KeywordValue("sampleKeyword")
        private val TestKeywordDescription = KeywordDescription("sample")
        private val TestUserKeywordResponse = UserKeywordResponse(
            id = TestUserKeywordId.value,
            keywordId = TestKeywordId.value,
            keyword = TestKeyword.value,
            userId = TestUserId.value,
            offsetX = 0.0,
            offsetY = 0.0,
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywordsResponse = UserKeywordsResponse(
            keywords = listOf(TestUserKeywordResponse),
        )
        private val TestCreateUserKeywordRequest = CreateUserKeywordRequest(
            userId = TestUserId.value,
            keyword = TestKeyword.value,
            offsetX = 0.0,
            offsetY = 0.0,
            description = TestKeywordDescription.value,
        )
        private val TestCreateUserKeyword = CreateUserKeyword(
            userId = TestUserId,
            keyword = TestKeyword,
            description = TestKeywordDescription,
            offsetX = 0.0,
            offsetY = 0.0,
        )
        private val TestPatchOffset = PatchOffset(1.0, 2.0)
        private val TestPatchOffsetRequest = PatchOffsetRequest(
            offsetX = TestPatchOffset.offsetX.toFloat(),
            offsetY = TestPatchOffset.offsetY.toFloat(),
        )
        private val TestPatchOffsetResponse = PatchOffsetResponse(
            offsetX = TestPatchOffset.offsetX.toFloat(),
            offsetY = TestPatchOffset.offsetY.toFloat(),
        )
        private val TestPatchDescription = PatchDescription(KeywordDescription("hello"))
        private val TestPatchDescriptionRequest = PatchDescriptionRequest(
            description = TestPatchDescription.description.value,
        )
        private val TestPatchDescriptionResponse = PatchDescriptionResponse(
            description = TestPatchDescription.description.value,
        )
        private val TestDescriptionResponse = DescriptionResponse(
            description = TestPatchDescription.description.value,
        )
    }
}

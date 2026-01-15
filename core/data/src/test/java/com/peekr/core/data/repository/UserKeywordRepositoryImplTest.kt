package com.peekr.core.data.repository

import com.peekr.core.data.source.local.database.dao.MyKeywordDetailDao
import com.peekr.core.data.source.local.database.entity.MyKeywordDetailEntity
import com.peekr.core.data.source.local.database.entity.toEntity
import com.peekr.core.data.source.network.datasource.UserKeywordNetworkDataSource
import com.peekr.core.data.source.network.datasource.UserNetworkDataSource
import com.peekr.core.data.source.network.dto.common.UserInfoResponse
import com.peekr.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.peekr.core.data.source.network.dto.common.toDomainModel
import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.PatchDescriptionRequest
import com.peekr.core.data.source.network.dto.userKeyword.response.DescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.PatchDescriptionResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordsResponse
import com.peekr.core.data.source.network.dto.userKeyword.response.toDomainModel
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.model.KeywordDescription
import com.peekr.core.domain.model.KeywordId
import com.peekr.core.domain.model.KeywordName
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.model.UserKeywordId
import com.peekr.core.domain.userKeyword.model.CreateUserKeyword
import com.peekr.core.domain.userKeyword.model.PatchDescription
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserKeywordRepositoryImplTest {
    private val userKeywordNetworkDataSource: UserKeywordNetworkDataSource = mockk()
    private val userNetworkDataSource: UserNetworkDataSource = mockk()
    private val myKeywordDetailDao: MyKeywordDetailDao = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = UserKeywordRepositoryImpl(
        userKeywordNetworkDataSource,
        userNetworkDataSource,
        myKeywordDetailDao,
        dispatcher,
    )

    @Test
    fun `나의 키워드 상세 정보 리스트 조회 - 성공 테스트`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestMyKeywordDetailEntity }
        coEvery {
            myKeywordDetailDao.getAll()
        } returns flowOf(expectedList)

        // when
        val result = repository.getMyKeywords().last()

        // then
        assertEquals(expectedCount, result.size)
        assertEquals(expectedList, result.map { it.toEntity() })
    }

    @Test
    fun `나의 키워드 상세 정보 리스트 새로고침 - 성공 테스트`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestUserKeywordDetailResponse }
        coEvery {
            userNetworkDataSource.getMyKeywords()
        } returns NetworkResult.Success(expectedList)
        coEvery {
            myKeywordDetailDao.upsertAll(any())
        } just Runs

        // when
        val result = repository.getMyKeywordsRefresh().last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 키워드 상세 정보 리스트 조회 - 성공 테스트`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestUserKeywordDetailResponse }
        coEvery {
            userNetworkDataSource.getUserKeywords(any())
        } returns NetworkResult.Success(expectedList)

        // when
        val result = repository.getUserKeywords(TestUserId).last()

        // then
        val success = result as Result.Success
        assertEquals(expectedCount, success.data.size)
        assertEquals(expectedList.map { it.toDomainModel() }, success.data)
    }

    @Test
    fun `사용자 키워드 설명 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.getDescription(TestUserKeywordId)
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
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            userKeywordNetworkDataSource.getDescription(TestUserKeywordId)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getDescription(TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 설명 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            userKeywordNetworkDataSource.getDescription(TestUserKeywordId)
        } throws exception

        // when
        val result = repository.getDescription(TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(exception).cause?.message,
                (result.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 생성 - 성공 테스트`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.createUserKeyword(TestCreateUserKeywordRequest)
        } returns NetworkResult.Success(TestUserKeywordResponse)
        coEvery {
            myKeywordDetailDao.upsert(any())
        } just Runs

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
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            userKeywordNetworkDataSource.createUserKeyword(TestCreateUserKeywordRequest)
        } returns NetworkResult.Error(expectedError)
        coEvery {
            myKeywordDetailDao.upsert(any())
        } just Runs

        // when
        val result = repository.createUserKeyword(TestCreateUserKeyword).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 생성 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            userKeywordNetworkDataSource.createUserKeyword(TestCreateUserKeywordRequest)
        } throws exception
        coEvery {
            myKeywordDetailDao.upsert(any())
        } just Runs

        // when
        val result = repository.createUserKeyword(TestCreateUserKeyword).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(exception).cause?.message,
                (result.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 설명 수정 - 성공 테스트`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescriptionRequest = TestPatchDescriptionRequest,
            )
        } returns NetworkResult.Success(TestPatchDescriptionResponse)
        coEvery {
            myKeywordDetailDao.updateDescription(any(), any())
        } just Runs

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
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            userKeywordNetworkDataSource.patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescriptionRequest = TestPatchDescriptionRequest,
            )
        } returns NetworkResult.Error(expectedError)
        coEvery {
            myKeywordDetailDao.updateDescription(any(), any())
        } just Runs

        // when
        val result = repository
            .patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescription = TestPatchDescription,
            ).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 설명 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            userKeywordNetworkDataSource.patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescriptionRequest = TestPatchDescriptionRequest,
            )
        } throws exception
        coEvery {
            myKeywordDetailDao.updateDescription(any(), any())
        } just Runs

        // when
        val result = repository
            .patchDescription(
                userKeywordId = TestUserKeywordId,
                patchDescription = TestPatchDescription,
            ).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(exception).cause?.message,
                (result.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 키워드 삭제 - 성공 테스트`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)
        } returns NetworkResult.Success(Unit)
        coEvery {
            myKeywordDetailDao.deleteById(any())
        } just Runs

        // when
        val result = repository.deleteUserKeyword(userKeywordId = TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 키워드 삭제 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            userKeywordNetworkDataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)
        } returns NetworkResult.Error(expectedError)
        coEvery {
            myKeywordDetailDao.deleteById(any())
        } just Runs

        // when
        val result = repository.deleteUserKeyword(userKeywordId = TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 삭제 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            userKeywordNetworkDataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)
        } throws exception
        coEvery {
            myKeywordDetailDao.deleteById(any())
        } just Runs

        // when
        val result = repository.deleteUserKeyword(userKeywordId = TestUserKeywordId).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(exception).cause?.message,
                (result.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserKeywordId = UserKeywordId(1L)
        private val TestKeywordId = KeywordId(1L)
        private val TestKeyword = KeywordName("sampleKeyword")
        private val TestKeywordDescription = KeywordDescription("sample")
        private val TestUserKeywordResponse = UserKeywordResponse(
            id = TestUserKeywordId.value,
            keywordId = TestKeywordId.value,
            keyword = TestKeyword.value,
            userId = TestUserId.value,
            description = "description",
            createdAt = 1000,
            updatedAt = 1000,
        )
        private val TestUserKeywordsResponse = UserKeywordsResponse(
            keywords = listOf(TestUserKeywordResponse),
        )
        private val TestCreateUserKeywordRequest = CreateUserKeywordRequest(
            userId = TestUserId.value,
            keyword = TestKeyword.value,
            description = TestKeywordDescription.value,
        )
        private val TestCreateUserKeyword = CreateUserKeyword(
            userId = TestUserId,
            keyword = TestKeyword,
            description = TestKeywordDescription,
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
        private val TestMyKeywordDetailEntity = MyKeywordDetailEntity(
            userKeywordId = 1L,
            keywordId = 1L,
            keywordName = "keyword",
            description = "description",
            createdAt = 1000L,
            updatedAt = 1000L,
        )
        private val TestUserKeywordDetailResponse = UserKeywordDetailResponse(
            userKeywordId = 1L,
            keywordId = 1L,
            keywordName = "keyword",
            description = "description",
            userInfo = UserInfoResponse(
                userId = TestUserId.value,
                userName = "name",
                profileImageUrl = null,
            ),
            createdAt = 1000L,
            updatedAt = 1000L,
        )
    }
}

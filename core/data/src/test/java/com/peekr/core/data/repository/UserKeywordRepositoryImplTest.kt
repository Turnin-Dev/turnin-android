package com.peekr.core.data.repository

import com.peekr.core.data.source.local.database.dao.FeedDao
import com.peekr.core.data.source.local.database.dao.MyKeywordDao
import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.peekr.core.data.source.local.database.entity.FeedEntity
import com.peekr.core.data.source.local.database.entity.MyKeywordEntity
import com.peekr.core.data.source.local.database.entity.MyProfileEntity
import com.peekr.core.data.source.local.database.entity.toUserKeywordDetail
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.local.memory.MemoryCache
import com.peekr.core.data.source.network.datasource.UserKeywordNetworkDataSource
import com.peekr.core.data.source.network.datasource.UserNetworkDataSource
import com.peekr.core.data.source.network.dto.common.UserInfoResponse
import com.peekr.core.data.source.network.dto.common.UserKeywordDetailResponse
import com.peekr.core.data.source.network.dto.common.toDomainModel
import com.peekr.core.data.source.network.dto.userKeyword.request.CreateUserKeywordRequest
import com.peekr.core.data.source.network.dto.userKeyword.request.toDataModel
import com.peekr.core.data.source.network.dto.userKeyword.response.UserKeywordResponse
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
import com.peekr.core.domain.userKeyword.model.PatchUserKeyword
import com.peekr.core.domain.userKeyword.model.UserKeywordDetail
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserKeywordRepositoryImplTest {
    private val userKeywordNetworkDataSource: UserKeywordNetworkDataSource = mockk()
    private val userNetworkDataSource: UserNetworkDataSource = mockk()
    private val myKeywordDao: MyKeywordDao = mockk()
    private val myProfileDao: MyProfileDao = mockk()
    private val feedDao: FeedDao = mockk()
    private val memoryListCache: MemoryCache<UserId, List<UserKeywordDetail>> = mockk()
    private val memoryCache: MemoryCache<UserKeywordId, UserKeywordDetail> = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = UserKeywordRepositoryImpl(
        userKeywordNetworkDataSource = userKeywordNetworkDataSource,
        userNetworkDataSource = userNetworkDataSource,
        myKeywordDao = myKeywordDao,
        myProfileDao = myProfileDao,
        feedDao = feedDao,
        memoryListCache = memoryListCache,
        memoryCache = memoryCache,
        dataStoreManager = dataStoreManager,
        ioDispatcher = dispatcher,
    )

    // ------------------------------ getMyDetailFromLocal() ------------------------------
    @Test
    fun `나의 키워드 상세 정보 로컬 조회 - 성공 테스트(DB 데이터 O)`() = runTest {
        // given
        coEvery { dataStoreManager.getLongData(any()) } returns flowOf(TestUserId.value)
        coEvery { myProfileDao.getByUserId(TestUserId.value) } returns flowOf(TestMyProfileEntity)
        coEvery { myKeywordDao.getById(TestUserKeywordId.value) } returns flowOf(TestMyKeywordEntity)

        // when
        val result = repository.getMyDetailFromLocal(TestUserKeywordId).last()

        // then
        assertEquals(TestUserKeywordDetailResponse.toDomainModel(), result)
    }

    @Test
    fun `나의 키워드 상세 정보 로컬 조회 - 성공 테스트(DB 데이터 X)`() = runTest {
        // given
        coEvery { dataStoreManager.getLongData(any()) } returns flowOf(TestUserId.value)
        coEvery { myProfileDao.getByUserId(TestUserId.value) } returns flowOf(null)
        coEvery { myKeywordDao.getById(TestUserKeywordId.value) } returns flowOf(null)

        // when
        val result = repository.getMyDetailFromLocal(TestUserKeywordId).last()

        // then
        assertNull(result)
    }

    // ------------------------------ getDetail() ------------------------------

    @Test
    fun `키워드 상세 정보 조회 - 성공 테스트(메모리 캐시 X, DB 데이터 X)`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.getDetail(TestUserKeywordId)
        } returns NetworkResult.Success(TestUserKeywordDetailResponse)
        every { memoryCache[TestUserKeywordId] } returns null
        coEvery { feedDao.getById(TestUserKeywordId.value) } returns null
        every { memoryCache[TestUserKeywordId] = any() } just Runs

        // when
        val result = repository.getDetail(TestUserId, TestUserKeywordId).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywordDetailResponse.toDomainModel(), success.data)
    }

    @Test
    fun `키워드 상세 정보 조회 - 성공 테스트(DB 데이터 O)`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.getDetail(TestUserKeywordId)
        } returns NetworkResult.Success(TestUserKeywordDetailResponse)
        every { memoryCache[TestUserKeywordId] } returns null
        every { memoryCache[TestUserKeywordId] = any() } just Runs
        coEvery {
            feedDao.getById(TestUserKeywordId.value)
        } returns TestFeedEntity

        // when
        val result = repository.getDetail(TestUserId, TestUserKeywordId).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywordDetailResponse.toDomainModel(), success.data)
        // then: 해당 데이터가 메모리 캐시에도 승격됐는지 검증
        verify {
            memoryCache[TestUserKeywordId]
        }
    }

    @Test
    fun `키워드 상세 정보 조회 - 성공 테스트(메모리 캐시 O)`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.getDetail(TestUserKeywordId)
        } returns NetworkResult.Success(TestUserKeywordDetailResponse)
        every { memoryCache[TestUserKeywordId] = any() } just Runs
        coEvery { feedDao.getById(TestUserKeywordId.value) } returns null
        every {
            memoryCache[TestUserKeywordId]
        } returns TestUserKeywordDetailResponse.toDomainModel()

        // when
        val result = repository.getDetail(TestUserId, TestUserKeywordId).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywordDetailResponse.toDomainModel(), success.data)
    }

    // ------------------------------ getDetailRefresh() ------------------------------

    @Test
    fun `키워드 상세 정보 새로고침 - 성공 테스트(내 키워드인 경우)`() = runTest {
        // given
        coEvery { dataStoreManager.getLongData(any()) } returns flowOf(TestUserId.value)
        coEvery {
            userKeywordNetworkDataSource.getDetail(any())
        } returns NetworkResult.Success(TestUserKeywordDetailResponse)
        coEvery { myKeywordDao.upsert(any()) } just Runs

        // when
        val result = repository.getDetailRefresh(TestUserId, TestUserKeywordId).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywordDetailResponse.toDomainModel(), success.data)
        // 내 키워드인 경우 DB에 업데이트했는지 검증
        coVerify { myKeywordDao.upsert(any()) }
    }

    @Test
    fun `키워드 상세 정보 새로고침 - 성공 테스트(내 키워드가 아닌 경우)`() = runTest {
        // given
        val otherUserId = 100L
        coEvery { dataStoreManager.getLongData(any()) } returns flowOf(otherUserId)
        coEvery {
            userKeywordNetworkDataSource.getDetail(any())
        } returns NetworkResult.Success(TestUserKeywordDetailResponse)
        coEvery { memoryCache[any()] = any() } just Runs

        // when
        val result = repository.getDetailRefresh(TestUserId, TestUserKeywordId).last()

        // then
        val success = result as Result.Success
        assertEquals(TestUserKeywordDetailResponse.toDomainModel(), success.data)
        // 내 키워드가 아닌 경우 메모리 캐시에 업데이트했는지 검증
        coVerify { memoryCache[any()] = any() }
    }

    // ------------------------------ getMyKeywords() ------------------------------

    @Test
    fun `나의 키워드 상세 정보 리스트 조회 - 성공 테스트`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestMyKeywordEntity }
        coEvery {
            myKeywordDao.getAll()
        } returns flowOf(expectedList)

        // when
        val result = repository.getMyKeywords().last()

        // then
        assertEquals(expectedCount, result.size)
        assertEquals(expectedList.map { it.toUserKeywordDetail() }, result)
    }

    // ------------------------------ getMyKeywordsRefresh() ------------------------------

    @Test
    fun `나의 키워드 상세 정보 리스트 새로고침 - 성공 테스트`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestUserKeywordDetailResponse }
        coEvery {
            userNetworkDataSource.getMyKeywords()
        } returns NetworkResult.Success(expectedList)
        coEvery { myKeywordDao.upsertAll(any()) } just Runs
        coEvery { myKeywordDao.deleteAll() } just Runs

        // when
        val result = repository.getMyKeywordsRefresh().last()

        // then
        assertTrue(result is Result.Success)
    }

    // ------------------------------ getUserKeywords() ------------------------------

    @Test
    fun `사용자 키워드 상세 정보 리스트 조회 - 성공 테스트(메모리 캐시 X)`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestUserKeywordDetailResponse }
        coEvery {
            userNetworkDataSource.getUserKeywords(any())
        } returns NetworkResult.Success(expectedList)
        coEvery { memoryListCache[TestUserId] } returns null
        coEvery { memoryListCache[TestUserId] = any() } returns Unit
        coEvery { memoryCache[any()] = any() } returns Unit

        // when
        val result = repository.getUserKeywords(TestUserId).last()

        // then
        val success = result as Result.Success
        assertEquals(expectedCount, success.data.size)
        assertEquals(expectedList.map { it.toDomainModel() }, success.data)
    }

    @Test
    fun `사용자 키워드 상세 정보 리스트 조회 - 성공 테스트(메모리 캐시 O)`() = runTest {
        // given
        val expectedCount = 2
        val expectedList = List(expectedCount) { TestUserKeywordDetailResponse }
        coEvery {
            userNetworkDataSource.getUserKeywords(any())
        } returns NetworkResult.Success(expectedList)
        coEvery {
            memoryListCache[TestUserId]
        } returns expectedList.map { it.toDomainModel() }

        // when
        val result = repository.getUserKeywords(TestUserId).last()

        // then
        val success = result as Result.Success
        assertEquals(expectedCount, success.data.size)
        assertEquals(expectedList.map { it.toDomainModel() }, success.data)
    }

    // ------------------------------ createUserKeyword() ------------------------------

    @Test
    fun `사용자 키워드 생성 - 성공 테스트`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.createUserKeyword(TestCreateUserKeywordRequest)
        } returns NetworkResult.Success(TestUserKeywordResponse)
        coEvery {
            myKeywordDao.upsert(any())
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
            myKeywordDao.upsert(any())
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
            myKeywordDao.upsert(any())
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

    // ------------------------------ update() ------------------------------

    @Test
    fun `사용자 키워드 수정 - 성공 테스트`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.patch(TestPatchUserKeyword.toDataModel())
        } returns NetworkResult.Success(Unit)
        coEvery {
            myKeywordDao.update(any(), any(), any())
        } just Runs

        // when
        val result = repository.update(TestPatchUserKeyword).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 키워드 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            userKeywordNetworkDataSource.patch(TestPatchUserKeyword.toDataModel())
        } returns NetworkResult.Error(expectedError)
        coEvery {
            myKeywordDao.update(any(), any(), any())
        } just Runs

        // when
        val result = repository.update(TestPatchUserKeyword).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 키워드 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            userKeywordNetworkDataSource.patch(TestPatchUserKeyword.toDataModel())
        } throws exception
        coEvery {
            myKeywordDao.update(any(), any(), any())
        } just Runs

        // when
        val result = repository.update(TestPatchUserKeyword).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is CommonErrorType.Unexpected) {
            assertEquals(
                CommonErrorType.Unexpected(exception).cause?.message,
                (result.error as CommonErrorType.Unexpected).cause?.message,
            )
        }
    }

    // ------------------------------ deleteUserKeyword() ------------------------------

    @Test
    fun `사용자 키워드 삭제 - 성공 테스트`() = runTest {
        // given
        coEvery {
            userKeywordNetworkDataSource.deleteUserKeyword(userKeywordId = TestUserKeywordId)
        } returns NetworkResult.Success(Unit)
        coEvery {
            myKeywordDao.deleteById(any())
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
            myKeywordDao.deleteById(any())
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
            myKeywordDao.deleteById(any())
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
            description = "description",
            createdAt = 1000,
            updatedAt = 1000,
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
        private val TestMyKeywordEntity = MyKeywordEntity(
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
        private val TestFeedEntity = FeedEntity(
            userKeywordId = TestUserKeywordDetailResponse.userKeywordId,
            keywordId = TestUserKeywordDetailResponse.keywordId,
            keywordName = TestUserKeywordDetailResponse.keywordName,
            description = TestUserKeywordDetailResponse.description,
            userId = TestUserKeywordDetailResponse.userInfo.userId,
            userName = TestUserKeywordDetailResponse.userInfo.userName,
            profileImageUrl = TestUserKeywordDetailResponse.userInfo.profileImageUrl,
            createdAt = TestUserKeywordDetailResponse.createdAt,
            updatedAt = TestUserKeywordDetailResponse.updatedAt,
        )
        private val TestMyProfileEntity = MyProfileEntity(
            userId = TestUserId.value,
            displayId = "did",
            name = "name",
            profileImageUrl = null,
            introduce = "introduce",
            lastLoginAt = 1000L,
            friendsCount = 10,
            active = true,
        )
        private val TestPatchUserKeyword = PatchUserKeyword(
            userKeywordId = UserKeywordId(1L),
            keywordName = KeywordName("newKeyword"),
            description = KeywordDescription("newDescription"),
        )
    }
}

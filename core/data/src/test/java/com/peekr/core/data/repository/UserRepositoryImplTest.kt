package com.peekr.core.data.repository

import com.peekr.core.data.source.local.database.dao.MyProfileDao
import com.peekr.core.data.source.local.database.entity.MyProfileEntity
import com.peekr.core.data.source.local.database.entity.toUserKeywordDetail
import com.peekr.core.data.source.local.datastore.DataStoreKey
import com.peekr.core.data.source.local.datastore.DataStoreManager
import com.peekr.core.data.source.local.memory.MemoryCache
import com.peekr.core.data.source.network.datasource.UserNetworkDataSource
import com.peekr.core.data.source.network.dto.user.request.IntroducePatchRequest
import com.peekr.core.data.source.network.dto.user.request.UserPatchRequest
import com.peekr.core.data.source.network.dto.user.response.MyProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserProfileResponse
import com.peekr.core.data.source.network.dto.user.response.UserResponse
import com.peekr.core.data.source.network.dto.user.response.toDomainModel
import com.peekr.core.data.source.network.error.NetworkErrorType
import com.peekr.core.data.source.network.error.toCommonErrorType
import com.peekr.core.data.source.network.util.NetworkResult
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.common.error.CommonErrorType
import com.peekr.core.domain.friend.model.FriendStatus
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.Role
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.model.CoreUserProfile
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.repository.UserRepository
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryImplTest {
    private val dataSource: UserNetworkDataSource = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private val myProfileDao: MyProfileDao = mockk()
    private val memoryCache: MemoryCache<Long, CoreUserProfile> = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: UserRepository =
        UserRepositoryImpl(dataSource, dataStoreManager, memoryCache, myProfileDao, dispatcher)

    @Before
    fun setUp() {
        coEvery {
            dataStoreManager.getLongData(DataStoreKey.User.UserId)
        } returns flowOf(TestUserId.value)
    }

    @Test
    fun `사용자 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getUser()
        } returns NetworkResult.Success(TestUserResponse)

        // when
        val result = repository.getUser().last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestUserResponse.toDomainModel(),
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 조회 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.getUser()
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getUser().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.getUser()
        } throws exception

        // when
        val result = repository.getUser().last()

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
    fun `나의 프로필 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataStoreManager.getLongData(any())
        } returns flowOf(TestMyUserId.value)
        coEvery {
            myProfileDao.getByUserId(any())
        } returns flowOf(TestMyProfileEntity)

        // when
        val result = repository.getMyProfile().last()

        // then
        assertEquals(TestMyProfileEntity.toUserKeywordDetail(), result)
    }

    @Test
    fun `나의 프로필 새로고침 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getMyProfile()
        } returns NetworkResult.Success(TestMyProfileResponse)
        coEvery {
            myProfileDao.upsert(any())
        } just Runs

        // when
        val result = repository.getMyProfileRefresh().last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `나의 프로필 새로고침 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.getMyProfile()
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getMyProfileRefresh().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `나의 프로필 새로고침 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery { dataSource.getMyProfile() } throws exception

        // when
        val result = repository.getMyProfileRefresh().last()

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
    fun `사용자 프로필 조회 - 성공 테스트(캐시가 존재하지 않는 경우 네트워크에서 조회를 한다)`() = runTest {
        // given
        coEvery {
            dataSource.getUserProfile(TestUserId)
        } returns NetworkResult.Success(TestUserProfileResponse)
        coEvery { memoryCache[TestUserId.value] } returns null
        coEvery { memoryCache[TestUserId.value] = any() } returns Unit

        // when
        val result = repository.getUserProfile(TestUserId).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestUserProfileResponse.toDomainModel(),
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 프로필 조회 - 성공 테스트(캐시가 존재하는 경우 캐시 데이터를 조회한다)`() = runTest {
        // given
        coEvery {
            dataSource.getUserProfile(TestUserId)
        } returns NetworkResult.Success(TestUserProfileResponse)
        coEvery {
            memoryCache[TestUserId.value]
        } returns TestUserProfileResponse.toDomainModel()

        // when
        val result = repository.getUserProfile(TestUserId).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestUserProfileResponse.toDomainModel(),
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 프로필 조회 - 새로고침 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getUserProfile(TestUserId)
        } returns NetworkResult.Success(TestUserProfileResponse)
        coEvery { memoryCache[TestUserId.value] = any() } returns Unit

        // when
        val result = repository.getUserProfile(TestUserId, forceRefresh = true).last()

        // then
        assertTrue(result is Result.Success)
        assertEquals(
            TestUserProfileResponse.toDomainModel(),
            (result as Result.Success).data,
        )
    }

    @Test
    fun `사용자 프로필 조회 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.getUserProfile(TestUserId)
        } returns NetworkResult.Error(expectedError)
        coEvery { memoryCache[TestUserId.value] } returns null
        coEvery { memoryCache[TestUserId.value] = any() } returns Unit

        // when
        val result = repository.getUserProfile(TestUserId).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 프로필 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery { dataSource.getUserProfile(TestUserId) } throws exception
        coEvery { memoryCache[TestUserId.value] } returns null
        coEvery { memoryCache[TestUserId.value] = any() } returns Unit

        // when
        val result = repository.getUserProfile(TestUserId).last()

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
    fun `사용자 수정 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.updateUser(TestUserPatchRequest)
        } returns NetworkResult.Success(Unit)
        coEvery {
            myProfileDao.updateProfile(any(), any(), any(), any(), any())
        } just Runs

        // when
        val result = repository.updateMyProfile(TestUserPatch).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.updateUser(TestUserPatchRequest)
        } returns NetworkResult.Error(expectedError)
        coEvery {
            myProfileDao.updateProfile(any(), any(), any(), any(), any())
        } just Runs

        // when
        val result = repository.updateMyProfile(TestUserPatch).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.updateUser(TestUserPatchRequest)
        } throws exception
        coEvery {
            myProfileDao.updateProfile(any(), any(), any(), any(), any())
        } just Runs

        // when
        val result = repository.updateMyProfile(TestUserPatch).last()

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
    fun `사용자 소개글 수정 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.updateIntroduce(TestIntroducePatchRequest)
        } returns NetworkResult.Success(Unit)
        coEvery {
            myProfileDao.updateIntroduce(any(), any())
        } just Runs

        // when
        val introduce = Introduce(TestIntroducePatchRequest.introduce)
        val result = repository.updateMyIntroduce(introduce).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 소개글 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Unexpected(null)
        coEvery {
            dataSource.updateIntroduce(TestIntroducePatchRequest)
        } returns NetworkResult.Error(expectedError)
        coEvery {
            myProfileDao.updateIntroduce(any(), any())
        } just Runs

        // when
        val introduce = Introduce(TestIntroducePatchRequest.introduce)
        val result = repository.updateMyIntroduce(introduce).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(
            expectedError.toCommonErrorType(),
            (result as Result.Error).error,
        )
    }

    @Test
    fun `사용자 소개글 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.updateIntroduce(TestIntroducePatchRequest)
        } throws exception
        coEvery {
            myProfileDao.updateIntroduce(any(), any())
        } just Runs

        // when
        val introduce = Introduce(TestIntroducePatchRequest.introduce)
        val result = repository.updateMyIntroduce(introduce).last()

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
        private val TestMyUserId = UserId(1L)
        private val TestUserId = UserId(2L)
        private val TestDisplayId = DisplayId("did")
        private val TestUserResponse = UserResponse(
            id = TestMyUserId.value,
            role = Role.USER,
            provider = SocialLoginProvider.GOOGLE,
            providerId = "id",
            displayId = TestDisplayId.value,
            name = "name",
            profileImageUrl = "",
            introduce = "hello",
            lastLoginAt = 1000L,
            active = true,
        )
        private val TestUserPatchRequest = UserPatchRequest(
            displayId = "id",
            name = "name",
            profileImageUrl = null,
            introduce = "hello",
        )
        private val TestUserPatch = UserPatch(
            displayId = DisplayId("id"),
            name = Name("name"),
            profileImageUrl = null,
            introduce = Introduce("hello"),
        )
        private val TestIntroducePatchRequest = IntroducePatchRequest("hello")
        private val TestUserProfileResponse = UserProfileResponse(
            userId = TestUserId.value,
            displayId = TestDisplayId.value,
            name = "name",
            profileImageUrl = "",
            introduce = "hello",
            lastLoginAt = 1000L,
            active = true,
            friendStatus = FriendStatus.NOTHING,
            friendsCount = 51,
            isBlocked = false,
        )
        private val TestMyProfileResponse = MyProfileResponse(
            userId = TestMyUserId.value,
            displayId = "id",
            name = "name",
            profileImageUrl = "",
            introduce = "hello",
            lastLoginAt = 1000L,
            active = true,
            friendsCount = 51,
        )
        private val TestMyProfileEntity = MyProfileEntity(
            userId = TestMyUserId.value,
            displayId = "id",
            name = "name",
            profileImageUrl = "",
            introduce = "hello",
            lastLoginAt = 1000L,
            active = true,
            friendsCount = 51,
        )
    }
}

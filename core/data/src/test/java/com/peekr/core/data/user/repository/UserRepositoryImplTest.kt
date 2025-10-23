package com.peekr.core.data.user.repository

import com.peekr.core.data.network.error.NetworkErrorType
import com.peekr.core.data.network.error.toCommonErrorType
import com.peekr.core.data.network.util.NetworkResult
import com.peekr.core.data.user.network.UserDataSource
import com.peekr.core.data.user.network.request.UserPatchRequest
import com.peekr.core.data.user.network.response.UserProfileResponse
import com.peekr.core.data.user.network.response.UserResponse
import com.peekr.core.data.user.network.response.toDomainModel
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.Role
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.error.UserErrorType
import com.peekr.core.domain.user.model.UserPatch
import com.peekr.core.domain.user.repository.UserRepository
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
class UserRepositoryImplTest {
    private val dataSource: UserDataSource = mockk()
    private val dispatcher = UnconfinedTestDispatcher()
    private val repository: UserRepository = UserRepositoryImpl(dataSource, dispatcher)

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
        val expectedError = NetworkErrorType.Network.Forbidden
        coEvery {
            dataSource.getUser()
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getUser().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError.toCommonErrorType(), (result as Result.Error).error)
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
        if (result is Result.Error && result.error is UserErrorType.Unexpected) {
            assertEquals(
                UserErrorType.Unexpected(exception).cause?.message,
                (result.error as UserErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 프로필 조회 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.getUserProfile()
        } returns NetworkResult.Success(TestUserProfileResponse)

        // when
        val result = repository.getUserProfile().last()

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
        val expectedError = NetworkErrorType.Network.Forbidden
        coEvery {
            dataSource.getUserProfile()
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.getUserProfile().last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError.toCommonErrorType(), (result as Result.Error).error)
    }

    @Test
    fun `사용자 프로필 조회 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery { dataSource.getUserProfile() } throws exception

        // when
        val result = repository.getUserProfile().last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is UserErrorType.Unexpected) {
            assertEquals(
                UserErrorType.Unexpected(exception).cause?.message,
                (result.error as UserErrorType.Unexpected).cause?.message,
            )
        }
    }

    @Test
    fun `사용자 수정 - 성공 테스트`() = runTest {
        // given
        coEvery {
            dataSource.updateUserById(TestUserPatchRequest)
        } returns NetworkResult.Success(Unit)

        // when
        val result = repository.updateUser(TestUserPatch).last()

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `사용자 수정 - 알려진 에러 방출 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val expectedError = NetworkErrorType.Network.Forbidden
        coEvery {
            dataSource.updateUserById(TestUserPatchRequest)
        } returns NetworkResult.Error(expectedError)

        // when
        val result = repository.updateUser(TestUserPatch).last()

        // then
        assertTrue(result is Result.Error)
        assertEquals(expectedError.toCommonErrorType(), (result as Result.Error).error)
    }

    @Test
    fun `사용자 수정 - 예외 발생 시 정상적으로 에러를 반환한다`() = runTest {
        // given
        val exception = Exception("error!")
        coEvery {
            dataSource.updateUserById(TestUserPatchRequest)
        } throws exception

        // when
        val result = repository.updateUser(TestUserPatch).last()

        // then
        assertTrue(result is Result.Error)
        if (result is Result.Error && result.error is UserErrorType.Unexpected) {
            assertEquals(
                UserErrorType.Unexpected(exception).cause?.message,
                (result.error as UserErrorType.Unexpected).cause?.message,
            )
        }
    }

    companion object {
        private val TestUserId = UserId(1L)
        private val TestUserResponse = UserResponse(
            id = TestUserId.value,
            role = Role.USER,
            provider = SocialLoginProvider.GOOGLE,
            providerId = "id",
            displayId = "id",
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
        private val TestUserProfileResponse = UserProfileResponse(
            id = TestUserId.value,
            role = Role.USER,
            provider = SocialLoginProvider.GOOGLE,
            providerId = "id",
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

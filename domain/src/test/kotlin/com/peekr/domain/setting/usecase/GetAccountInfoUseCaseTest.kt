package com.peekr.domain.setting.usecase

import com.peekr.core.domain.auth.repository.AuthRepository
import com.peekr.core.domain.common.Result
import com.peekr.core.domain.model.DisplayId
import com.peekr.core.domain.model.Introduce
import com.peekr.core.domain.model.Name
import com.peekr.core.domain.model.SocialLoginProvider
import com.peekr.core.domain.model.UserId
import com.peekr.core.domain.user.model.CoreMyProfile
import com.peekr.core.domain.user.repository.UserRepository
import com.peekr.domain.setting.error.SettingErrorType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAccountInfoUseCaseTest {
    private val authRepository: AuthRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val usecase = GetAccountInfoUseCase(authRepository, userRepository)

    @Test
    fun `로컬 데이터가 있는 경우 계정 정보를 정상적으로 조회한다`() = runTest {
        // given
        val loginType = SocialLoginProvider.GOOGLE
        coEvery { authRepository.getLoginType() } returns loginType
        every { userRepository.getMyProfile() } returns flowOf(TestMyProfile)

        // when
        val result = usecase().last()

        // then
        val accountInfo = (result as Result.Success).data
        assertEquals(loginType, accountInfo.loginProvider)
        assertEquals(TestMyProfile.userId, accountInfo.userId)
        assertEquals(TestMyProfile.displayId, accountInfo.displayId)
        assertEquals(TestMyProfile.name, accountInfo.name)

        verify(exactly = 0) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `로컬 데이터가 없는 경우 네트워크 리프레쉬 수행 후 계정 정보를 정상적으로 조회한다`() = runTest {
        // given: 리프레쉬는 성공하고 로컬 데이터의 두 번째 호출 때 데이터를 정상적으로 반환하도록 설정
        val loginType = SocialLoginProvider.GOOGLE
        coEvery { authRepository.getLoginType() } returns loginType
        every { userRepository.getMyProfile() } returnsMany listOf(
            flowOf(null),
            flowOf(TestMyProfile),
        )
        every { userRepository.getMyProfileRefresh() } returns flow {
            emit(Result.Loading)
            emit(Result.Success(Unit))
        }

        // when
        val result = usecase().last()

        // then
        val accountInfo = (result as Result.Success).data
        assertEquals(loginType, accountInfo.loginProvider)
        assertEquals(TestMyProfile.userId, accountInfo.userId)
        assertEquals(TestMyProfile.displayId, accountInfo.displayId)
        assertEquals(TestMyProfile.name, accountInfo.name)

        verify(exactly = 1) { userRepository.getMyProfileRefresh() }
    }

    @Test
    fun `로그인 타입 조회 실패 시 에러를 방출한다`() = runTest {
        // given
        coEvery { authRepository.getLoginType() } returns null
        every { userRepository.getMyProfile() } returns flowOf(TestMyProfile)

        // when
        val result = usecase().last()

        // then
        val error = (result as Result.Error).error
        assertEquals(SettingErrorType.LoginProviderNotFound, error)
    }

    @Test
    fun `계정 정보 조회 시 첫 방출은 반드시 Loading 이다`() = runTest {
        // given
        val loginType = SocialLoginProvider.GOOGLE
        coEvery { authRepository.getLoginType() } returns loginType
        every { userRepository.getMyProfile() } returns flowOf(TestMyProfile)

        // when, then
        assertEquals(Result.Loading, usecase().first())
    }

    @Test
    fun `예상치 못한 예외 발생 시 에러를 방출한다`() = runTest {
        // given
        val exception = Exception("error")
        val loginType = SocialLoginProvider.GOOGLE
        coEvery { authRepository.getLoginType() } returns loginType
        every { userRepository.getMyProfile() } returns flow { throw exception }

        // when
        val result = usecase().last()

        // then
        val error = (result as Result.Error).error
        assertEquals(SettingErrorType.Unexpected(exception), error)
    }

    companion object {
        private val TestMyProfile = CoreMyProfile(
            userId = UserId(1L),
            displayId = DisplayId("displayId"),
            name = Name("name"),
            profileImageUrl = "profileImageUrl",
            introduce = Introduce("introduce"),
            lastLoginAt = 1000L,
            friendsCount = 10,
            active = true,
        )
    }
}
